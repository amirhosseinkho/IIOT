package algorithms;

import core.FogNode;
import core.Workflow;
import utils.CostCalculator;
import utils.DelayCalculator;

import java.util.*;

/**
 * HFCO - Hierarchical Fog-Cloud Optimization
 * Outer layer: Fog Deployment Optimization using EPO operators
 * Optimizes bit-vector ζ for selecting active fog nodes
 */
public class HFCOFogPlacementOptimizer {
    
    // Algorithm parameters
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double LATENCY_WEIGHT = 0.6;
    private static final double DEPLOY_COST_WEIGHT = 0.4;
    private static final int ELITE_SIZE = 5;
    
    // EPO operator weights
    private static final double RANDOM_JUMP_WEIGHT = 0.25;
    private static final double SOCIAL_FORAGE_WEIGHT = 0.25;
    private static final double AMBUSH_WEIGHT = 0.25;
    private static final double SPRINT_WEIGHT = 0.25;
    
    private List<FogNode> allNodes;
    private List<FogNode> fogNodes;
    private List<FogNode> cloudNodes;
    private Workflow workflow;
    private Random random;
    
    public HFCOFogPlacementOptimizer(List<FogNode> allNodes, Workflow workflow) {
        this.allNodes = allNodes;
        this.workflow = workflow;
        this.random = new Random();
        
        // Separate fog and cloud nodes
        this.fogNodes = new ArrayList<>();
        this.cloudNodes = new ArrayList<>();
        
        for (FogNode node : allNodes) {
            if (node.isCloud()) {
                cloudNodes.add(node);
            } else {
                fogNodes.add(node);
            }
        }
    }
    
    /**
     * Main optimization method for fog deployment
     * Returns optimal set of active fog nodes
     */
    public FogDeploymentResult optimizeFogDeployment() {
        // Step 1: Initialize population of bit-vectors ζ
        List<BitVector> population = initializePopulation();
        
        // Step 2: Main evolution loop
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate fitness for all bit-vectors
            evaluatePopulation(population);
            
            // Sort by fitness (ascending - lower cost is better)
            Collections.sort(population);
            
            // Elite preservation
            List<BitVector> elite = new ArrayList<>(population.subList(0, ELITE_SIZE));
            
            // Generate new population using EPO operators
            List<BitVector> newPopulation = new ArrayList<>(elite);
            
            while (newPopulation.size() < POPULATION_SIZE) {
                BitVector parent = selectParent(population);
                BitVector offspring = applyEPOOperator(parent, generation);
                
                // Repair bit-vector (ensure at least one fog node is active)
                repairBitVector(offspring);
                
                newPopulation.add(offspring);
            }
            
            population = newPopulation;
            
            // Elite hill-climbing
            applyEliteHillClimbing(population);
        }
        
        // Return best solution
        evaluatePopulation(population);
        Collections.sort(population);
        BitVector bestSolution = population.get(0);
        
        return convertToDeploymentResult(bestSolution);
    }
    
    /**
     * Bit-vector ζ: represents which fog nodes are active (1) or inactive (0)
     */
    private static class BitVector implements Comparable<BitVector> {
        private boolean[] activeFogNodes;  // ζ[i] = true means fog node i is active
        private double fitness = Double.MAX_VALUE;
        
        public BitVector(int numFogNodes) {
            this.activeFogNodes = new boolean[numFogNodes];
        }
        
        public BitVector(BitVector other) {
            this.activeFogNodes = other.activeFogNodes.clone();
            this.fitness = other.fitness;
        }
        
        public void setActive(int fogNodeIndex, boolean active) {
            activeFogNodes[fogNodeIndex] = active;
        }
        
        public boolean isActive(int fogNodeIndex) {
            return activeFogNodes[fogNodeIndex];
        }
        
        public int getNumActiveFogNodes() {
            int count = 0;
            for (boolean active : activeFogNodes) {
                if (active) count++;
            }
            return count;
        }
        
        public int getTotalFogNodes() {
            return activeFogNodes.length;
        }
        
        @Override
        public int compareTo(BitVector other) {
            return Double.compare(this.fitness, other.fitness);
        }
    }
    
    /**
     * Initialize population with random bit-vectors
     */
    private List<BitVector> initializePopulation() {
        List<BitVector> population = new ArrayList<>();
        
        for (int i = 0; i < POPULATION_SIZE; i++) {
            BitVector bitVector = new BitVector(fogNodes.size());
            
            // Randomly activate fog nodes (ensure at least one is active)
            boolean hasActiveNode = false;
            for (int j = 0; j < fogNodes.size(); j++) {
                boolean active = random.nextDouble() < 0.5;
                bitVector.setActive(j, active);
                if (active) hasActiveNode = true;
            }
            
            // Ensure at least one fog node is active
            if (!hasActiveNode) {
                int randomIndex = random.nextInt(fogNodes.size());
                bitVector.setActive(randomIndex, true);
            }
            
            population.add(bitVector);
        }
        
        return population;
    }
    
    /**
     * Evaluate fitness for entire population
     */
    private void evaluatePopulation(List<BitVector> population) {
        for (BitVector bitVector : population) {
            bitVector.fitness = calculateFitness(bitVector);
        }
    }
    
    /**
     * Calculate fitness: weighted latency + deployment cost
     */
    private double calculateFitness(BitVector bitVector) {
        // Get active fog nodes
        List<FogNode> activeFogNodes = getActiveFogNodes(bitVector);
        
        // Calculate average latency
        double avgLatency = calculateAverageLatency(activeFogNodes);
        
        // Calculate deployment cost
        double deployCost = calculateDeploymentCost(bitVector);
        
        // Weighted fitness function
        return LATENCY_WEIGHT * avgLatency + DEPLOY_COST_WEIGHT * deployCost;
    }
    
    /**
     * Get list of active fog nodes based on bit-vector
     */
    private List<FogNode> getActiveFogNodes(BitVector bitVector) {
        List<FogNode> activeNodes = new ArrayList<>();
        
        for (int i = 0; i < bitVector.getTotalFogNodes(); i++) {
            if (bitVector.isActive(i)) {
                activeNodes.add(fogNodes.get(i));
            }
        }
        
        return activeNodes;
    }
    
    /**
     * Calculate average latency for active fog nodes
     */
    private double calculateAverageLatency(List<FogNode> activeFogNodes) {
        if (activeFogNodes.isEmpty()) {
            return Double.MAX_VALUE; // Penalty for no fog nodes
        }
        
        double totalLatency = 0.0;
        for (FogNode node : activeFogNodes) {
            totalLatency += node.getLatencyMs();
        }
        
        return totalLatency / activeFogNodes.size();
    }
    
    /**
     * Calculate deployment cost based on active fog nodes
     */
    private double calculateDeploymentCost(BitVector bitVector) {
        double totalCost = 0.0;
        
        for (int i = 0; i < bitVector.getTotalFogNodes(); i++) {
            if (bitVector.isActive(i)) {
                FogNode node = fogNodes.get(i);
                // Base deployment cost + operational cost
                totalCost += 100.0 + (node.getCostPerSec() * 3600); // 1 hour operation
            }
        }
        
        return totalCost;
    }
    
    /**
     * Select parent using tournament selection
     */
    private BitVector selectParent(List<BitVector> population) {
        int tournamentSize = 3;
        BitVector best = null;
        
        for (int i = 0; i < tournamentSize; i++) {
            BitVector candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    /**
     * Apply EPO operator based on exploration/exploitation decision
     */
    private BitVector applyEPOOperator(BitVector parent, int generation) {
        double explorationRate = calculateExplorationRate(generation);
        
        if (random.nextDouble() < explorationRate) {
            // Exploration phase
            return applyExplorationOperator(parent);
        } else {
            // Exploitation phase
            return applyExploitationOperator(parent);
        }
    }
    
    /**
     * Calculate exploration rate based on generation
     */
    private double calculateExplorationRate(int generation) {
        return Math.max(0.1, 1.0 - (double) generation / MAX_GENERATIONS);
    }
    
    /**
     * Apply exploration operator
     */
    private BitVector applyExplorationOperator(BitVector parent) {
        if (random.nextDouble() < 0.5) {
            return randomJump(parent);
        } else {
            return socialForage(parent);
        }
    }
    
    /**
     * Apply exploitation operator
     */
    private BitVector applyExploitationOperator(BitVector parent) {
        if (random.nextDouble() < 0.5) {
            return ambush(parent);
        } else {
            return sprint(parent);
        }
    }
    
    /**
     * Random Jump operator: Randomly flip some bits
     */
    private BitVector randomJump(BitVector parent) {
        BitVector offspring = new BitVector(parent);
        
        // Randomly flip 30% of bits
        int numFlips = Math.max(1, (int) (offspring.getTotalFogNodes() * 0.3));
        
        for (int i = 0; i < numFlips; i++) {
            int randomIndex = random.nextInt(offspring.getTotalFogNodes());
            offspring.setActive(randomIndex, !offspring.isActive(randomIndex));
        }
        
        return offspring;
    }
    
    /**
     * Social Forage operator: Learn from best solutions
     */
    private BitVector socialForage(BitVector parent) {
        // This would need access to best solutions from population
        // For now, implement as random mutation
        return randomJump(parent);
    }
    
    /**
     * Ambush operator: Fine-tune existing solution
     */
    private BitVector ambush(BitVector parent) {
        BitVector offspring = new BitVector(parent);
        
        // Small adjustments: flip 1-2 bits
        int numFlips = random.nextInt(2) + 1;
        
        for (int i = 0; i < numFlips; i++) {
            int randomIndex = random.nextInt(offspring.getTotalFogNodes());
            offspring.setActive(randomIndex, !offspring.isActive(randomIndex));
        }
        
        return offspring;
    }
    
    /**
     * Sprint operator: Optimize towards best known solution
     */
    private BitVector sprint(BitVector parent) {
        BitVector offspring = new BitVector(parent);
        
        // Move towards more efficient configuration
        // Activate more fog nodes if latency is high, deactivate if cost is high
        if (random.nextDouble() < 0.5) {
            // Activate a random inactive node
            for (int i = 0; i < offspring.getTotalFogNodes(); i++) {
                if (!offspring.isActive(i)) {
                    offspring.setActive(i, true);
                    break;
                }
            }
        } else {
            // Deactivate a random active node (if more than one)
            if (offspring.getNumActiveFogNodes() > 1) {
                for (int i = 0; i < offspring.getTotalFogNodes(); i++) {
                    if (offspring.isActive(i)) {
                        offspring.setActive(i, false);
                        break;
                    }
                }
            }
        }
        
        return offspring;
    }
    
    /**
     * Repair bit-vector: ensure at least one fog node is active
     */
    private void repairBitVector(BitVector bitVector) {
        if (bitVector.getNumActiveFogNodes() == 0) {
            // Activate a random fog node
            int randomIndex = random.nextInt(bitVector.getTotalFogNodes());
            bitVector.setActive(randomIndex, true);
        }
    }
    
    /**
     * Elite hill-climbing: Improve best solutions
     */
    private void applyEliteHillClimbing(List<BitVector> population) {
        for (int i = 0; i < ELITE_SIZE; i++) {
            BitVector elite = population.get(i);
            BitVector improved = hillClimb(elite);
            
            if (improved.fitness < elite.fitness) {
                population.set(i, improved);
            }
        }
    }
    
    /**
     * Hill-climbing with bit-flip and repair
     */
    private BitVector hillClimb(BitVector bitVector) {
        BitVector best = new BitVector(bitVector);
        
        // Try flipping each bit individually
        for (int i = 0; i < bitVector.getTotalFogNodes(); i++) {
            BitVector candidate = new BitVector(bitVector);
            candidate.setActive(i, !candidate.isActive(i));
            
            // Repair if necessary
            repairBitVector(candidate);
            
            // Evaluate
            candidate.fitness = calculateFitness(candidate);
            
            if (candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    /**
     * Convert bit-vector to deployment result
     */
    private FogDeploymentResult convertToDeploymentResult(BitVector bitVector) {
        List<FogNode> activeFogNodes = getActiveFogNodes(bitVector);
        List<FogNode> allActiveNodes = new ArrayList<>(activeFogNodes);
        allActiveNodes.addAll(cloudNodes); // Cloud nodes are always active
        
        return new FogDeploymentResult(activeFogNodes, allActiveNodes, bitVector.fitness);
    }
    
    /**
     * Result class for fog deployment optimization
     */
    public static class FogDeploymentResult {
        public final List<FogNode> activeFogNodes;
        public final List<FogNode> allActiveNodes; // fog + cloud
        public final double deploymentCost;
        
        public FogDeploymentResult(List<FogNode> activeFogNodes, 
                                 List<FogNode> allActiveNodes, 
                                 double deploymentCost) {
            this.activeFogNodes = activeFogNodes;
            this.allActiveNodes = allActiveNodes;
            this.deploymentCost = deploymentCost;
        }
    }
}
