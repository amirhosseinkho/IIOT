package algorithms;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;
import utils.DelayCalculator;

import java.util.*;

/**
 * Enhanced EPO-based CEIS Algorithm for IIoT Task Scheduling
 * Implements Oppositional Initialization, Adaptive Exploration/Exploitation,
 * Deadline-aware Repair, and Elite Hill-Climbing
 */
public class EnhancedEPOCEIS {
    
    // Algorithm parameters
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 200;
    private static final double PENALTY_M = 1000.0;
    private static final int ELITE_SIZE = 10;
    private static final double EXPLORATION_THRESHOLD = 0.5;
    
    // EPO operators weights
    private static final double RANDOM_JUMP_WEIGHT = 0.25;
    private static final double SOCIAL_FORAGE_WEIGHT = 0.25;
    private static final double AMBUSH_WEIGHT = 0.25;
    private static final double SPRINT_WEIGHT = 0.25;

    private Workflow workflow;
    private List<FogNode> fogNodes;
    private List<Chromosome> population;
    private Random random;

    public EnhancedEPOCEIS(Workflow workflow, List<FogNode> fogNodes) {
        this.workflow = workflow;
        this.fogNodes = fogNodes;
        this.population = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Main scheduling method using EPO-based optimization
     */
    public SchedulingResult schedule() {
        // Step 1: Oppositional Initialization
        initializePopulation();
        
        // Step 2: Main evolution loop
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Evaluate fitness for all chromosomes
        evaluatePopulation();

            // Sort by fitness (ascending - lower cost is better)
            Collections.sort(population);
            
            // Elite preservation
            List<Chromosome> elite = new ArrayList<>(population.subList(0, ELITE_SIZE));
            
            // Generate new population using EPO operators
            List<Chromosome> newPopulation = new ArrayList<>(elite);
            
            while (newPopulation.size() < POPULATION_SIZE) {
                Chromosome parent = selectParent();
                Chromosome offspring = applyEPOOperator(parent, generation);
                
                // Deadline-aware repair
                repairChromosome(offspring);
                
                newPopulation.add(offspring);
            }

            population = newPopulation;
            
            // Elite hill-climbing on best solutions
            applyEliteHillClimbing();
        }
        
        // Return best solution
        evaluatePopulation();
            Collections.sort(population);
        Chromosome bestSolution = population.get(0);
        
        return convertToSchedulingResult(bestSolution);
    }
    
    /**
     * Chromosome structure: ⟨deviceID, startTime⟩ for each task
     */
    private static class Chromosome implements Comparable<Chromosome> {
        private Map<Integer, Integer> taskToNodeMap;  // taskId -> nodeId
        private Map<Integer, Double> taskStartTimes; // taskId -> startTime
        private double fitness = Double.MAX_VALUE;
        
        public Chromosome() {
            this.taskToNodeMap = new HashMap<>();
            this.taskStartTimes = new HashMap<>();
        }
        
        public Chromosome(Chromosome other) {
            this.taskToNodeMap = new HashMap<>(other.taskToNodeMap);
            this.taskStartTimes = new HashMap<>(other.taskStartTimes);
            this.fitness = other.fitness;
        }
        
        @Override
        public int compareTo(Chromosome other) {
            return Double.compare(this.fitness, other.fitness);
        }
    }
    
    /**
     * Enhanced Oppositional Initialization: Generate diverse population using multiple strategies
     * Implements Opposition-Based Learning (OBL) with domain-specific optimizations
     */
    private void initializePopulation() {
        population.clear();
        
        int quarterSize = POPULATION_SIZE / 4;
        
        // Phase 1: Pure random initialization (25%)
        for (int i = 0; i < quarterSize; i++) {
            population.add(generateRandomChromosome());
        }
        
        // Phase 2: Greedy-based initialization (25%)
        for (int i = 0; i < quarterSize; i++) {
            population.add(generateGreedyChromosome());
        }
        
        // Phase 3: Opposition-based initialization (25%)
        for (int i = 0; i < quarterSize; i++) {
            Chromosome opposite = generateOppositionalChromosome(population.get(i));
            population.add(opposite);
        }
        
        // Phase 4: Hybrid initialization (25%)
        for (int i = 0; i < POPULATION_SIZE - 3 * quarterSize; i++) {
            population.add(generateHybridChromosome());
        }
        
        // Evaluate initial population and keep only the best individuals
        evaluatePopulation();
        Collections.sort(population);
        
        // If we have more than needed, keep the best ones
        if (population.size() > POPULATION_SIZE) {
            population = new ArrayList<>(population.subList(0, POPULATION_SIZE));
        }
    }
    
    /**
     * Generate random chromosome with valid task assignments
     */
    private Chromosome generateRandomChromosome() {
        Chromosome chromosome = new Chromosome();
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        
        for (IIoTTask task : sortedTasks) {
            // Random node assignment
            int randomNodeId = fogNodes.get(random.nextInt(fogNodes.size())).getId();
            chromosome.taskToNodeMap.put(task.getId(), randomNodeId);
            
            // Random start time (within reasonable bounds)
            double randomStartTime = random.nextDouble() * 100.0;
            chromosome.taskStartTimes.put(task.getId(), randomStartTime);
        }
        
        return chromosome;
    }
    
    /**
     * Generate greedy chromosome using cost-aware heuristics
     */
    private Chromosome generateGreedyChromosome() {
        Chromosome chromosome = new Chromosome();
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        
        // Initialize node available times
        for (FogNode node : fogNodes) {
            nodeAvailableTime.put(node.getId(), 0.0);
        }
        
        for (IIoTTask task : sortedTasks) {
            FogNode bestNode = null;
            double bestScore = Double.MAX_VALUE;
            double bestStartTime = 0.0;
            
            // Find the best node for this task
            for (FogNode node : fogNodes) {
                double readyTime = workflow.getReadyTime(task, node, nodeAvailableTime);
                double execTime = CostCalculator.calculateExecutionTime(task, node);
                double transferDelay = CostCalculator.calculateTransferDelay(task, node);
                double totalTime = execTime + transferDelay + node.getLatencyMs() / 1000.0;
                double finishTime = readyTime + totalTime;
                
                // Greedy score: minimize cost + deadline violation penalty
                double cost = execTime * node.getCostPerSec();
                double deadlineViolation = Math.max(0, finishTime - task.getDeadline());
                double score = cost + deadlineViolation * PENALTY_M * 0.1; // Lighter penalty for greedy init
                
                if (score < bestScore) {
                    bestScore = score;
                    bestNode = node;
                    bestStartTime = readyTime;
                }
            }
            
            if (bestNode != null) {
                chromosome.taskToNodeMap.put(task.getId(), bestNode.getId());
                chromosome.taskStartTimes.put(task.getId(), bestStartTime);
                
                double execTime = CostCalculator.calculateExecutionTime(task, bestNode);
                double transferDelay = CostCalculator.calculateTransferDelay(task, bestNode);
                double finishTime = bestStartTime + execTime + transferDelay + bestNode.getLatencyMs() / 1000.0;
                nodeAvailableTime.put(bestNode.getId(), finishTime);
            }
        }
        
        return chromosome;
    }
    
    /**
     * Generate hybrid chromosome combining random and greedy strategies
     */
    private Chromosome generateHybridChromosome() {
        Chromosome chromosome = new Chromosome();
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        
        for (IIoTTask task : sortedTasks) {
            if (random.nextDouble() < 0.7) {
                // 70% greedy selection: choose from top 3 nodes
                List<FogNode> topNodes = getTopNodesForTask(task, 3);
                FogNode selectedNode = topNodes.get(random.nextInt(topNodes.size()));
                chromosome.taskToNodeMap.put(task.getId(), selectedNode.getId());
            } else {
                // 30% random selection
                int randomNodeId = fogNodes.get(random.nextInt(fogNodes.size())).getId();
                chromosome.taskToNodeMap.put(task.getId(), randomNodeId);
            }
            
            // Smart start time: consider deadline pressure
            double deadline = task.getDeadline();
            double maxStartTime = Math.max(10.0, deadline * 0.6); // Start within 60% of deadline
            double randomStartTime = random.nextDouble() * maxStartTime;
            chromosome.taskStartTimes.put(task.getId(), randomStartTime);
        }
        
        return chromosome;
    }
    
    /**
     * Get top N nodes for a task based on performance metrics
     */
    private List<FogNode> getTopNodesForTask(IIoTTask task, int topN) {
        List<FogNode> nodeList = new ArrayList<>(fogNodes);
        
        // Sort nodes by score (lower is better)
        nodeList.sort((n1, n2) -> {
            double score1 = calculateNodeTaskScore(task, n1);
            double score2 = calculateNodeTaskScore(task, n2);
            return Double.compare(score1, score2);
        });
        
        return nodeList.subList(0, Math.min(topN, nodeList.size()));
    }
    
    /**
     * Enhanced oppositional chromosome generation with adaptive opposition
     */
    private Chromosome generateOppositionalChromosome(Chromosome original) {
        Chromosome opposite = new Chromosome();
        
        // Calculate dynamic bounds for each task
        Map<Integer, NodeBounds> taskNodeBounds = calculateDynamicNodeBounds();
        Map<Integer, TimeBounds> taskTimeBounds = calculateDynamicTimeBounds();
        
        for (Map.Entry<Integer, Integer> entry : original.taskToNodeMap.entrySet()) {
            int taskId = entry.getKey();
            int originalNodeId = entry.getValue();
            
            // Smart node opposition based on performance characteristics
            int oppositeNodeId = calculateOppositeNode(taskId, originalNodeId, taskNodeBounds.get(taskId));
            opposite.taskToNodeMap.put(taskId, oppositeNodeId);
            
            // Adaptive time opposition considering deadline constraints
            double originalStartTime = original.taskStartTimes.get(taskId);
            double oppositeStartTime = calculateOppositeTime(taskId, originalStartTime, taskTimeBounds.get(taskId));
            opposite.taskStartTimes.put(taskId, oppositeStartTime);
        }
        
        return opposite;
    }
    
    /**
     * Calculate dynamic node bounds for opposition
     */
    private Map<Integer, NodeBounds> calculateDynamicNodeBounds() {
        Map<Integer, NodeBounds> bounds = new HashMap<>();
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            
            // Find node ID bounds for this specific task
            int minNodeId = fogNodes.stream().mapToInt(FogNode::getId).min().orElse(0);
            int maxNodeId = fogNodes.stream().mapToInt(FogNode::getId).max().orElse(fogNodes.size() - 1);
            
            bounds.put(taskId, new NodeBounds(minNodeId, maxNodeId));
        }
        
        return bounds;
    }
    
    /**
     * Calculate dynamic time bounds for opposition
     */
    private Map<Integer, TimeBounds> calculateDynamicTimeBounds() {
        Map<Integer, TimeBounds> bounds = new HashMap<>();
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            
            // Calculate feasible time bounds based on deadline and dependencies
            double minTime = 0.0;
            double maxTime = task.getDeadline() * 0.8; // Allow up to 80% of deadline for start time
            
            bounds.put(taskId, new TimeBounds(minTime, maxTime));
        }
        
        return bounds;
    }
    
    /**
     * Calculate opposite node using performance-aware opposition
     */
    private int calculateOppositeNode(int taskId, int originalNodeId, NodeBounds bounds) {
        IIoTTask task = workflow.getTask(taskId);
        
        // Find nodes with opposite performance characteristics
        FogNode originalNode = getNodeById(originalNodeId);
        if (originalNode == null) {
            return fogNodes.get(0).getId();
        }
        
        // Opposition strategy: if original node is high-performance, choose low-cost
        // if original is low-cost, choose high-performance
        boolean originalIsHighPerformance = originalNode.getMips() > getAverageNodeMips();
        
        List<FogNode> candidateNodes = new ArrayList<>();
        for (FogNode node : fogNodes) {
            boolean nodeIsHighPerformance = node.getMips() > getAverageNodeMips();
            
            // Select nodes with opposite characteristics
            if (originalIsHighPerformance != nodeIsHighPerformance) {
                candidateNodes.add(node);
            }
        }
        
        if (candidateNodes.isEmpty()) {
            // Fallback to simple mathematical opposition
            int oppositeId = bounds.maxNodeId - (originalNodeId - bounds.minNodeId);
            return Math.max(bounds.minNodeId, Math.min(bounds.maxNodeId, oppositeId));
        }
        
        // Choose best opposite node
        return candidateNodes.stream()
            .min((n1, n2) -> Double.compare(
                calculateNodeTaskScore(task, n1),
                calculateNodeTaskScore(task, n2)
            ))
            .map(FogNode::getId)
            .orElse(originalNodeId);
    }
    
    /**
     * Calculate opposite start time with deadline awareness
     */
    private double calculateOppositeTime(int taskId, double originalTime, TimeBounds bounds) {
        // Adaptive opposition: if original is early, make it late (but feasible)
        // if original is late, make it early
        
        double range = bounds.maxTime - bounds.minTime;
        if (range <= 0) {
            return bounds.minTime;
        }
        
        // Opposition formula: opposite = max - (original - min)
        double opposite = bounds.maxTime - (originalTime - bounds.minTime);
        
        // Ensure bounds
        return Math.max(bounds.minTime, Math.min(bounds.maxTime, opposite));
    }
    
    /**
     * Get average MIPS across all nodes
     */
    private double getAverageNodeMips() {
        return fogNodes.stream()
            .mapToDouble(FogNode::getMips)
            .average()
            .orElse(1000.0);
    }
    
    // Helper classes for bounds
    private static class NodeBounds {
        final int minNodeId;
        final int maxNodeId;
        
        NodeBounds(int min, int max) {
            this.minNodeId = min;
            this.maxNodeId = max;
        }
    }
    
    private static class TimeBounds {
        final double minTime;
        final double maxTime;
        
        TimeBounds(double min, double max) {
            this.minTime = min;
            this.maxTime = max;
        }
    }
    
    /**
     * Evaluate fitness for entire population
     */
    private void evaluatePopulation() {
        for (Chromosome chromosome : population) {
            chromosome.fitness = calculateFitness(chromosome);
        }
    }
    
    /**
     * Calculate fitness: total cost + penalty for deadline misses
     */
    private double calculateFitness(Chromosome chromosome) {
        double totalCost = 0.0;
        double totalPenalty = 0.0;
        
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        for (FogNode node : fogNodes) {
            nodeAvailableTime.put(node.getId(), 0.0);
        }
        
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        
        for (IIoTTask task : sortedTasks) {
            // Ensure task has valid assignments
            if (!chromosome.taskToNodeMap.containsKey(task.getId())) {
                // Assign to random fog node if missing
                int randomNodeId = fogNodes.get(random.nextInt(fogNodes.size())).getId();
                chromosome.taskToNodeMap.put(task.getId(), randomNodeId);
            }
            
            if (!chromosome.taskStartTimes.containsKey(task.getId())) {
                // Assign random start time if missing
                chromosome.taskStartTimes.put(task.getId(), random.nextDouble() * 10.0);
            }
            
            int nodeId = chromosome.taskToNodeMap.get(task.getId());
            FogNode node = getNodeById(nodeId);
            double startTime = chromosome.taskStartTimes.get(task.getId());
            
            // Calculate execution time and transfer delay
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            
            double finishTime = startTime + execTime + transferDelay + latency;
            
            // Calculate cost
            double duration = execTime + transferDelay + latency;
            double cost = duration * node.getCostPerSec();
            totalCost += cost;
            
            // Calculate penalty for deadline miss
            if (finishTime > task.getDeadline()) {
                double penalty = (finishTime - task.getDeadline()) * PENALTY_M;
                totalPenalty += penalty;
            }
        }
        
        return totalCost + totalPenalty;
    }
    
    /**
     * Select parent using tournament selection
     */
    private Chromosome selectParent() {
        int tournamentSize = 3;
        Chromosome best = null;
        
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    /**
     * Apply EPO operator based on exploration/exploitation decision
     */
    private Chromosome applyEPOOperator(Chromosome parent, int generation) {
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
     * Calculate exploration rate based on generation and population diversity
     * Implements adaptive EPO strategy based on diversity score and intensification score
     */
    private double calculateExplorationRate(int generation) {
        // Basic linearly decreasing exploration rate
        double linearRate = Math.max(0.1, 1.0 - (double) generation / MAX_GENERATIONS);
        
        // Calculate population diversity score
        double diversityScore = calculatePopulationDiversity();
        
        // Calculate intensification score (convergence speed)
        double intensificationScore = calculateIntensificationScore(generation);
        
        // Adaptive decision: explore more if population is converging too fast
        if (diversityScore < 0.3 && intensificationScore > 0.7) {
            return Math.min(0.8, linearRate + 0.3); // Boost exploration
        } else if (diversityScore > 0.7 && intensificationScore < 0.3) {
            return Math.max(0.1, linearRate - 0.2); // Boost exploitation
        }
        
        return linearRate;
    }
    
    /**
     * Calculate population diversity score based on chromosome variations
     */
    private double calculatePopulationDiversity() {
        if (population.size() < 2) return 1.0;
        
        double totalDistance = 0.0;
        int comparisons = 0;
        
        for (int i = 0; i < population.size() - 1; i++) {
            for (int j = i + 1; j < population.size(); j++) {
                totalDistance += calculateChromosomeDistance(population.get(i), population.get(j));
                comparisons++;
            }
        }
        
        double avgDistance = totalDistance / comparisons;
        // Normalize to [0,1] range
        return Math.min(1.0, avgDistance / (fogNodes.size() * 2.0));
    }
    
    /**
     * Calculate distance between two chromosomes
     */
    private double calculateChromosomeDistance(Chromosome c1, Chromosome c2) {
        double distance = 0.0;
        
        for (Integer taskId : c1.taskToNodeMap.keySet()) {
            if (c2.taskToNodeMap.containsKey(taskId)) {
                // Node assignment difference
                if (!c1.taskToNodeMap.get(taskId).equals(c2.taskToNodeMap.get(taskId))) {
                    distance += 1.0;
                }
                
                // Start time difference
                double timeDiff = Math.abs(c1.taskStartTimes.get(taskId) - c2.taskStartTimes.get(taskId));
                distance += Math.min(1.0, timeDiff / 100.0); // Normalize time difference
            }
        }
        
        return distance;
    }
    
    /**
     * Calculate intensification score based on recent improvement rate
     */
    private double calculateIntensificationScore(int generation) {
        if (generation < 10) return 0.5; // Default for early generations
        
        // Simple metric: if best fitness hasn't improved recently, increase intensification
        Collections.sort(population);
        double currentBest = population.get(0).fitness;
        
        // This is a simplified version - could be enhanced with fitness history tracking
        return Math.min(1.0, Math.max(0.0, 1.0 - (currentBest / (PENALTY_M * 10))));
    }
    
    /**
     * Apply exploration operator (Random Jump or Social Forage)
     */
    private Chromosome applyExplorationOperator(Chromosome parent) {
        if (random.nextDouble() < 0.5) {
            return randomJump(parent);
        } else {
            return socialForage(parent);
        }
    }
    
    /**
     * Apply exploitation operator (Ambush or Sprint)
     */
    private Chromosome applyExploitationOperator(Chromosome parent) {
        if (random.nextDouble() < 0.5) {
            return ambush(parent);
        } else {
            return sprint(parent);
        }
    }
    
    /**
     * Random Jump operator: Randomly change some assignments
     */
    private Chromosome randomJump(Chromosome parent) {
        Chromosome offspring = new Chromosome(parent);
        
        // Randomly change 20% of assignments
        int numChanges = Math.max(1, (int) (offspring.taskToNodeMap.size() * 0.2));
        
        for (int i = 0; i < numChanges; i++) {
            int randomTaskId = getRandomTaskId(offspring);
            int randomNodeId = fogNodes.get(random.nextInt(fogNodes.size())).getId();
            
            offspring.taskToNodeMap.put(randomTaskId, randomNodeId);
            offspring.taskStartTimes.put(randomTaskId, random.nextDouble() * 100.0);
        }
        
        return offspring;
    }
    
    /**
     * Social Forage operator: Learn from multiple good solutions with mean-centroidal guidance
     * Implements the discrete version of social foraging as described in EPO
     */
    private Chromosome socialForage(Chromosome parent) {
        Chromosome offspring = new Chromosome(parent);
        
        // Get top elite solutions for social learning
        Collections.sort(population);
        int eliteCount = Math.min(ELITE_SIZE, population.size());
        List<Chromosome> elites = population.subList(0, eliteCount);
        
        // Calculate mean chromosome (centroidal guidance)
        Map<Integer, Integer> meanNodeAssignment = calculateMeanNodeAssignment(elites);
        Map<Integer, Double> meanStartTimes = calculateMeanStartTimes(elites);
        
        // Social forage with probability-based learning
        double forageRate = 0.4; // 40% of tasks undergo social learning
        int numForages = Math.max(1, (int) (offspring.taskToNodeMap.size() * forageRate));
        
        for (int i = 0; i < numForages; i++) {
            int randomTaskId = getRandomTaskId(offspring);
            
            if (meanNodeAssignment.containsKey(randomTaskId) && meanStartTimes.containsKey(randomTaskId)) {
                // Move towards mean with some randomness
                if (random.nextDouble() < 0.7) { // 70% chance to follow the crowd
                    offspring.taskToNodeMap.put(randomTaskId, meanNodeAssignment.get(randomTaskId));
                    
                    // Add some noise to start time to avoid premature convergence
                    double meanTime = meanStartTimes.get(randomTaskId);
                    double noise = (random.nextDouble() - 0.5) * 20.0; // ±10 seconds
                    offspring.taskStartTimes.put(randomTaskId, Math.max(0, meanTime + noise));
                }
            }
        }
        
        return offspring;
    }
    
    /**
     * Calculate mean node assignment from elite solutions
     */
    private Map<Integer, Integer> calculateMeanNodeAssignment(List<Chromosome> elites) {
        Map<Integer, Map<Integer, Integer>> taskNodeCounts = new HashMap<>();
        
        // Count node assignments for each task
        for (Chromosome elite : elites) {
            for (Map.Entry<Integer, Integer> entry : elite.taskToNodeMap.entrySet()) {
                int taskId = entry.getKey();
                int nodeId = entry.getValue();
                
                taskNodeCounts.computeIfAbsent(taskId, k -> new HashMap<>());
                taskNodeCounts.get(taskId).merge(nodeId, 1, Integer::sum);
            }
        }
        
        // Select most frequent node for each task
        Map<Integer, Integer> meanAssignment = new HashMap<>();
        for (Map.Entry<Integer, Map<Integer, Integer>> entry : taskNodeCounts.entrySet()) {
            int taskId = entry.getKey();
            Map<Integer, Integer> nodeCounts = entry.getValue();
            
            int bestNode = nodeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(fogNodes.get(0).getId());
            
            meanAssignment.put(taskId, bestNode);
        }
        
        return meanAssignment;
    }
    
    /**
     * Calculate mean start times from elite solutions
     */
    private Map<Integer, Double> calculateMeanStartTimes(List<Chromosome> elites) {
        Map<Integer, Double> meanStartTimes = new HashMap<>();
        Map<Integer, Integer> taskCounts = new HashMap<>();
        
        // Sum start times for each task
        for (Chromosome elite : elites) {
            for (Map.Entry<Integer, Double> entry : elite.taskStartTimes.entrySet()) {
                int taskId = entry.getKey();
                double startTime = entry.getValue();
                
                meanStartTimes.merge(taskId, startTime, Double::sum);
                taskCounts.merge(taskId, 1, Integer::sum);
            }
        }
        
        // Calculate averages
        for (Map.Entry<Integer, Double> entry : meanStartTimes.entrySet()) {
            int taskId = entry.getKey();
            double sum = entry.getValue();
            int count = taskCounts.get(taskId);
            meanStartTimes.put(taskId, sum / count);
        }
        
        return meanStartTimes;
    }
    
    /**
     * Ambush operator: Intelligent local search with deadline-aware fine-tuning
     * Focuses on critical path tasks and deadline-sensitive optimizations
     */
    private Chromosome ambush(Chromosome parent) {
        Chromosome offspring = new Chromosome(parent);
        Chromosome best = Collections.min(population);
        
        // Identify critical tasks (those with tight deadlines or on critical path)
        List<IIoTTask> criticalTasks = identifyCriticalTasks();
        
        // Fine-tune assignments for critical tasks with guided search
        for (IIoTTask task : criticalTasks) {
            int taskId = task.getId();
            
            if (offspring.taskToNodeMap.containsKey(taskId)) {
                // Try different nodes for critical tasks
                int currentNodeId = offspring.taskToNodeMap.get(taskId);
                FogNode currentNode = getNodeById(currentNodeId);
                
                // Look for a potentially better node
                FogNode betterNode = findBetterNodeForTask(task, currentNode);
                if (betterNode != null && betterNode.getId() != currentNodeId) {
                    offspring.taskToNodeMap.put(taskId, betterNode.getId());
                }
                
                // Fine-tune start time with deadline awareness
                if (offspring.taskStartTimes.containsKey(taskId)) {
                    double currentTime = offspring.taskStartTimes.get(taskId);
                    double optimizedTime = optimizeStartTimeForTask(task, betterNode != null ? betterNode : currentNode, currentTime);
                    offspring.taskStartTimes.put(taskId, optimizedTime);
                }
            }
        }
        
        // Small random adjustments to non-critical tasks (traditional ambush behavior)
        for (Map.Entry<Integer, Double> entry : offspring.taskStartTimes.entrySet()) {
            int taskId = entry.getKey();
            boolean isCritical = criticalTasks.stream().anyMatch(t -> t.getId() == taskId);
            
            if (!isCritical && random.nextDouble() < 0.3) { // 30% chance for non-critical tasks
            double currentTime = entry.getValue();
                double adjustment = (random.nextDouble() - 0.5) * 5.0; // ±2.5 seconds for non-critical
            entry.setValue(Math.max(0, currentTime + adjustment));
            }
        }
        
        return offspring;
    }

    /**
     * Identify critical tasks based on deadline pressure and dependencies
     */
    private List<IIoTTask> identifyCriticalTasks() {
        List<IIoTTask> allTasks = new ArrayList<>(workflow.getAllTasks());
        List<IIoTTask> criticalTasks = new ArrayList<>();
        
        for (IIoTTask task : allTasks) {
            // Consider task critical if deadline is tight or it has many dependencies
            double workload = task.getLength(); // Use getLength() instead of getWorkload()
            double deadline = task.getDeadline();
            
            // Calculate minimum possible execution time on fastest node
            double minExecTime = workload / getFastestNodeSpeed();
            double deadlinePressure = minExecTime / deadline;
            
            // Critical if deadline pressure > 50% or task has high connectivity
            if (deadlinePressure > 0.5 || task.getParents().size() > 2) {
                criticalTasks.add(task);
            }
        }
        
        // Limit to top 30% of tasks to maintain efficiency
        int maxCritical = Math.max(1, (int) (allTasks.size() * 0.3));
        return criticalTasks.subList(0, Math.min(maxCritical, criticalTasks.size()));
    }
    
    /**
     * Find a better node for a given task
     */
    private FogNode findBetterNodeForTask(IIoTTask task, FogNode currentNode) {
        FogNode bestAlternative = null;
        double bestScore = calculateNodeTaskScore(task, currentNode);
        
        for (FogNode node : fogNodes) {
            if (node.getId() != currentNode.getId()) {
                double score = calculateNodeTaskScore(task, node);
                if (score < bestScore) {
                    bestScore = score;
                    bestAlternative = node;
                }
            }
        }
        
        return bestAlternative;
    }
    
    /**
     * Calculate a score for assigning a task to a node (lower is better)
     */
    private double calculateNodeTaskScore(IIoTTask task, FogNode node) {
        double execTime = CostCalculator.calculateExecutionTime(task, node);
        double transferDelay = CostCalculator.calculateTransferDelay(task, node);
        double cost = execTime * node.getCostPerSec();
        double latency = node.getLatencyMs() / 1000.0;
        
        // Weighted score considering cost, time, and deadline pressure
        double totalTime = execTime + transferDelay + latency;
        double deadlinePressure = totalTime / task.getDeadline();
        
        return cost + (deadlinePressure * 100.0); // Penalize deadline violations heavily
    }
    
    /**
     * Optimize start time for a task on a specific node
     */
    private double optimizeStartTimeForTask(IIoTTask task, FogNode node, double currentTime) {
        double execTime = CostCalculator.calculateExecutionTime(task, node);
        double transferDelay = CostCalculator.calculateTransferDelay(task, node);
        double totalTime = execTime + transferDelay + node.getLatencyMs() / 1000.0;
        
        // Latest possible start time to meet deadline
        double latestStart = task.getDeadline() - totalTime;
        
        if (latestStart < 0) {
            return 0; // Start immediately if deadline is impossible
        }
        
        // Try to start as early as possible but respect dependencies
        double earliestStart = calculateEarliestStartTime(task);
        
        // Choose the optimal start time within feasible range
        double optimalStart = Math.max(earliestStart, Math.min(currentTime, latestStart));
        
        return Math.max(0, optimalStart);
    }
    
    /**
     * Calculate earliest possible start time based on dependencies
     */
    private double calculateEarliestStartTime(IIoTTask task) {
        double earliestStart = 0.0;
        
        for (IIoTTask dependency : workflow.getParents(task)) {
            // This is a simplified version - should consider actual scheduling
            double depFinishTime = dependency.getFinishTime();
            if (depFinishTime > earliestStart) {
                earliestStart = depFinishTime;
            }
        }
        
        return earliestStart;
    }
    
    /**
     * Get the speed of the fastest available node
     */
    private double getFastestNodeSpeed() {
        return fogNodes.stream()
            .mapToDouble(FogNode::getMips)
            .max()
            .orElse(1000.0); // Default fallback
    }

    /**
     * Sprint operator: Aggressive convergence towards best known solution with controlled randomness
     * Implements velocity-based movement towards global best
     */
    private Chromosome sprint(Chromosome parent) {
        Chromosome offspring = new Chromosome(parent);
        Chromosome globalBest = Collections.min(population);
        
        // Sprint towards global best with high probability but maintain some diversity
        double sprintIntensity = 0.8; // 80% tendency to follow global best
        
        // Node assignment sprint
        for (Map.Entry<Integer, Integer> entry : offspring.taskToNodeMap.entrySet()) {
            int taskId = entry.getKey();
            int currentNodeId = entry.getValue();
            
            if (globalBest.taskToNodeMap.containsKey(taskId)) {
                Integer bestNodeId = globalBest.taskToNodeMap.get(taskId);
                
                if (bestNodeId != null && random.nextDouble() < sprintIntensity) {
                    // Sprint towards best with some probabilistic resistance
                    if (currentNodeId != bestNodeId) {
                        // Calculate "velocity" - how much to move towards best
                        double velocity = calculateSprintVelocity(taskId, currentNodeId, bestNodeId);
                        
                        if (random.nextDouble() < velocity) {
                    offspring.taskToNodeMap.put(taskId, bestNodeId);
                        }
                    }
                }
            }
        }
        
        // Start time sprint with momentum-based adjustment
        for (Map.Entry<Integer, Double> entry : offspring.taskStartTimes.entrySet()) {
            int taskId = entry.getKey();
            double currentTime = entry.getValue();
            
            if (globalBest.taskStartTimes.containsKey(taskId)) {
                Double bestTime = globalBest.taskStartTimes.get(taskId);
                
                if (bestTime != null && random.nextDouble() < sprintIntensity) {
                    // Move towards best time with momentum
                    double timeDifference = bestTime - currentTime;
                    double momentum = 0.6; // Sprint momentum factor
                    
                    double newTime = currentTime + (timeDifference * momentum);
                    offspring.taskStartTimes.put(taskId, Math.max(0, newTime));
                }
            }
        }
        
        return offspring;
    }
    
    /**
     * Calculate sprint velocity based on node characteristics and distance from optimal
     */
    private double calculateSprintVelocity(int taskId, int currentNodeId, int bestNodeId) {
        FogNode currentNode = getNodeById(currentNodeId);
        FogNode bestNode = getNodeById(bestNodeId);
        
        if (currentNode == null || bestNode == null) {
            return 0.5; // Default velocity
        }
        
        // Higher velocity if moving to a significantly better node
        double currentScore = currentNode.getCostPerSec() + (currentNode.getLatencyMs() / 1000.0);
        double bestScore = bestNode.getCostPerSec() + (bestNode.getLatencyMs() / 1000.0);
        
        double improvement = (currentScore - bestScore) / currentScore;
        
        // Velocity increases with potential improvement
        return Math.min(0.95, Math.max(0.1, 0.5 + improvement));
    }
    
    /**
     * Comprehensive Deadline-aware Repair: Multi-stage deadline violation recovery
     * Implements sophisticated repair strategies with dependency awareness
     */
    private void repairChromosome(Chromosome chromosome) {
        // Stage 1: Validate and fix missing assignments
        validateChromosomeCompleteness(chromosome);
        
        // Stage 2: Multi-pass deadline repair with increasing aggressiveness
        boolean hasViolations = true;
        int repairPass = 0;
        final int MAX_REPAIR_PASSES = 3;
        
        while (hasViolations && repairPass < MAX_REPAIR_PASSES) {
            hasViolations = performDeadlineRepairPass(chromosome, repairPass);
            repairPass++;
        }
        
        // Stage 3: Final validation and emergency repairs
        performEmergencyRepairs(chromosome);
        
        // Stage 4: Post-repair optimization
        optimizeRepairedSchedule(chromosome);
    }
    
    /**
     * Validate chromosome completeness and fix missing assignments
     */
    private void validateChromosomeCompleteness(Chromosome chromosome) {
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            
            // Fix missing node assignment
            if (!chromosome.taskToNodeMap.containsKey(taskId)) {
                FogNode bestNode = findMostSuitableNode(task);
                chromosome.taskToNodeMap.put(taskId, bestNode.getId());
            }
            
            // Fix missing or invalid start time
            if (!chromosome.taskStartTimes.containsKey(taskId) || 
                chromosome.taskStartTimes.get(taskId) < 0) {
                double smartStartTime = calculateSmartStartTime(task, chromosome);
                chromosome.taskStartTimes.put(taskId, smartStartTime);
            }
        }
    }
    
    /**
     * Perform deadline repair pass with increasing aggressiveness
     */
    private boolean performDeadlineRepairPass(Chromosome chromosome, int passNumber) {
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        for (FogNode node : fogNodes) {
            nodeAvailableTime.put(node.getId(), 0.0);
        }
        
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        boolean hasViolations = false;
        
        for (IIoTTask task : sortedTasks) {
            int taskId = task.getId();
            RepairResult result = repairTaskDeadline(task, chromosome, nodeAvailableTime, passNumber);
            
            if (result.hadViolation) {
                hasViolations = true;
            }
            
            // Update node available time with the new schedule
            nodeAvailableTime.put(result.finalNodeId, result.finishTime);
        }
        
        return hasViolations;
    }
    
    /**
     * Repair deadline violation for a single task
     */
    private RepairResult repairTaskDeadline(IIoTTask task, Chromosome chromosome, 
                                          Map<Integer, Double> nodeAvailableTime, int aggressiveness) {
        int taskId = task.getId();
        int currentNodeId = chromosome.taskToNodeMap.get(taskId);
        double currentStartTime = chromosome.taskStartTimes.get(taskId);
        
        FogNode currentNode = getNodeById(currentNodeId);
        double execTime = CostCalculator.calculateExecutionTime(task, currentNode);
        double transferDelay = CostCalculator.calculateTransferDelay(task, currentNode);
        double latency = currentNode.getLatencyMs() / 1000.0;
        
        // Calculate earliest possible start time considering dependencies
        double earliestPossibleStart = calculateEarliestPossibleStartTime(task, chromosome);
        double effectiveStartTime = Math.max(currentStartTime, earliestPossibleStart);
        double finishTime = effectiveStartTime + execTime + transferDelay + latency;
        
        RepairResult result = new RepairResult();
        result.finalNodeId = currentNodeId;
        result.finishTime = finishTime;
        result.hadViolation = finishTime > task.getDeadline();
        
        if (!result.hadViolation) {
            // No violation, just update start time if needed
            if (effectiveStartTime != currentStartTime) {
                chromosome.taskStartTimes.put(taskId, effectiveStartTime);
            }
            return result;
        }
        
        // Deadline violation detected - apply repair strategy based on aggressiveness
        RepairStrategy strategy = getRepairStrategy(aggressiveness);
        result = applyRepairStrategy(task, chromosome, nodeAvailableTime, strategy);
        
        return result;
    }
    
    /**
     * Calculate earliest possible start time considering all dependencies
     */
    private double calculateEarliestPossibleStartTime(IIoTTask task, Chromosome chromosome) {
        double earliestStart = 0.0;
        
        for (Integer parentId : task.getParents()) {
            IIoTTask parentTask = workflow.getTask(parentId);
            if (parentTask != null && chromosome.taskToNodeMap.containsKey(parentId)) {
                int parentNodeId = chromosome.taskToNodeMap.get(parentId);
                double parentStartTime = chromosome.taskStartTimes.get(parentId);
                FogNode parentNode = getNodeById(parentNodeId);
                
                double parentExecTime = CostCalculator.calculateExecutionTime(parentTask, parentNode);
                double parentTransferDelay = CostCalculator.calculateTransferDelay(parentTask, parentNode);
                double parentFinishTime = parentStartTime + parentExecTime + parentTransferDelay;
                
                earliestStart = Math.max(earliestStart, parentFinishTime);
            }
        }
        
        return earliestStart;
    }
    
    /**
     * Get repair strategy based on aggressiveness level
     */
    private RepairStrategy getRepairStrategy(int aggressiveness) {
        switch (aggressiveness) {
            case 0: return RepairStrategy.TIME_SHIFT;      // Conservative: try time adjustments
            case 1: return RepairStrategy.NODE_MIGRATION;  // Moderate: try better nodes
            case 2: return RepairStrategy.AGGRESSIVE;      // Aggressive: cost-insensitive repairs
            default: return RepairStrategy.EMERGENCY;      // Emergency: any feasible solution
        }
    }
    
    /**
     * Apply specific repair strategy
     */
    private RepairResult applyRepairStrategy(IIoTTask task, Chromosome chromosome, 
                                           Map<Integer, Double> nodeAvailableTime, RepairStrategy strategy) {
        switch (strategy) {
            case TIME_SHIFT:
                return attemptTimeShiftRepair(task, chromosome, nodeAvailableTime);
            case NODE_MIGRATION:
                return attemptNodeMigrationRepair(task, chromosome, nodeAvailableTime);
            case AGGRESSIVE:
                return attemptAggressiveRepair(task, chromosome, nodeAvailableTime);
            case EMERGENCY:
                return attemptEmergencyRepair(task, chromosome, nodeAvailableTime);
            default:
                return new RepairResult(); // Fallback
        }
    }
    
    /**
     * Attempt repair by shifting start time earlier
     */
    private RepairResult attemptTimeShiftRepair(IIoTTask task, Chromosome chromosome, 
                                              Map<Integer, Double> nodeAvailableTime) {
        int taskId = task.getId();
        int nodeId = chromosome.taskToNodeMap.get(taskId);
        FogNode node = getNodeById(nodeId);
        
        double execTime = CostCalculator.calculateExecutionTime(task, node);
        double transferDelay = CostCalculator.calculateTransferDelay(task, node);
        double latency = node.getLatencyMs() / 1000.0;
        double totalTime = execTime + transferDelay + latency;
        
        // Calculate latest possible start time to meet deadline
        double latestStart = task.getDeadline() - totalTime;
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        
        RepairResult result = new RepairResult();
        result.finalNodeId = nodeId;
        
        if (latestStart >= earliestStart) {
            // Feasible to meet deadline on current node
            double optimalStart = Math.max(earliestStart, 
                nodeAvailableTime.getOrDefault(nodeId, 0.0));
            
            if (optimalStart <= latestStart) {
                chromosome.taskStartTimes.put(taskId, optimalStart);
                result.finishTime = optimalStart + totalTime;
                result.hadViolation = false;
                return result;
            }
        }
        
        // Time shift failed
        result.hadViolation = true;
        result.finishTime = earliestStart + totalTime;
        return result;
    }
    
    /**
     * Attempt repair by migrating to a better node
     */
    private RepairResult attemptNodeMigrationRepair(IIoTTask task, Chromosome chromosome, 
                                                  Map<Integer, Double> nodeAvailableTime) {
        int taskId = task.getId();
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        
        FogNode bestNode = null;
        double bestFinishTime = Double.MAX_VALUE;
        double bestStartTime = 0.0;
        
        // Try all nodes to find one that can meet the deadline
        for (FogNode node : fogNodes) {
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            double totalTime = execTime + transferDelay + latency;
            
            double nodeAvailable = nodeAvailableTime.getOrDefault(node.getId(), 0.0);
            double startTime = Math.max(earliestStart, nodeAvailable);
            double finishTime = startTime + totalTime;
            
            if (finishTime <= task.getDeadline() && finishTime < bestFinishTime) {
                bestNode = node;
                bestFinishTime = finishTime;
                bestStartTime = startTime;
            }
        }
        
        RepairResult result = new RepairResult();
        
        if (bestNode != null) {
            // Found a node that can meet the deadline
            chromosome.taskToNodeMap.put(taskId, bestNode.getId());
            chromosome.taskStartTimes.put(taskId, bestStartTime);
            result.finalNodeId = bestNode.getId();
            result.finishTime = bestFinishTime;
            result.hadViolation = false;
        } else {
            // No node can meet deadline, choose the fastest one
            FogNode fastestNode = findFastestNode(task);
            chromosome.taskToNodeMap.put(taskId, fastestNode.getId());
            
            double execTime = CostCalculator.calculateExecutionTime(task, fastestNode);
            double transferDelay = CostCalculator.calculateTransferDelay(task, fastestNode);
            double latency = fastestNode.getLatencyMs() / 1000.0;
            double startTime = Math.max(earliestStart, 
                nodeAvailableTime.getOrDefault(fastestNode.getId(), 0.0));
            
            chromosome.taskStartTimes.put(taskId, startTime);
            result.finalNodeId = fastestNode.getId();
            result.finishTime = startTime + execTime + transferDelay + latency;
            result.hadViolation = true;
        }
        
        return result;
    }
    
    /**
     * Attempt aggressive repair (cost-insensitive)
     */
    private RepairResult attemptAggressiveRepair(IIoTTask task, Chromosome chromosome, 
                                               Map<Integer, Double> nodeAvailableTime) {
        // In aggressive mode, prioritize fastest execution regardless of cost
        FogNode fastestNode = fogNodes.stream()
            .min(Comparator.comparingDouble(node -> 
                CostCalculator.calculateExecutionTime(task, node)))
            .orElse(fogNodes.get(0));
        
        int taskId = task.getId();
        chromosome.taskToNodeMap.put(taskId, fastestNode.getId());
        
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        chromosome.taskStartTimes.put(taskId, earliestStart);
        
        double execTime = CostCalculator.calculateExecutionTime(task, fastestNode);
        double transferDelay = CostCalculator.calculateTransferDelay(task, fastestNode);
        double latency = fastestNode.getLatencyMs() / 1000.0;
        
        RepairResult result = new RepairResult();
        result.finalNodeId = fastestNode.getId();
        result.finishTime = earliestStart + execTime + transferDelay + latency;
        result.hadViolation = result.finishTime > task.getDeadline();
        
        return result;
    }
    
    /**
     * Emergency repair - just make it feasible
     */
    private RepairResult attemptEmergencyRepair(IIoTTask task, Chromosome chromosome, 
                                              Map<Integer, Double> nodeAvailableTime) {
        // Emergency: assign to random node with earliest possible time
        int taskId = task.getId();
        FogNode randomNode = fogNodes.get(random.nextInt(fogNodes.size()));
        chromosome.taskToNodeMap.put(taskId, randomNode.getId());
        
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        chromosome.taskStartTimes.put(taskId, earliestStart);
        
        double execTime = CostCalculator.calculateExecutionTime(task, randomNode);
        double transferDelay = CostCalculator.calculateTransferDelay(task, randomNode);
        double latency = randomNode.getLatencyMs() / 1000.0;
        
        RepairResult result = new RepairResult();
        result.finalNodeId = randomNode.getId();
        result.finishTime = earliestStart + execTime + transferDelay + latency;
        result.hadViolation = result.finishTime > task.getDeadline();
        
        return result;
    }
    
    /**
     * Perform emergency repairs for remaining violations
     */
    private void performEmergencyRepairs(Chromosome chromosome) {
        // Last resort: relax deadlines temporarily or mark as infeasible
        // This is a placeholder for more sophisticated emergency handling
    }
    
    /**
     * Optimize the repaired schedule
     */
    private void optimizeRepairedSchedule(Chromosome chromosome) {
        // Perform light optimizations to improve cost without violating deadlines
        // This could include small time adjustments or better resource utilization
    }
    
    /**
     * Find most suitable node for a task based on multiple criteria
     */
    private FogNode findMostSuitableNode(IIoTTask task) {
        return fogNodes.stream()
            .min((n1, n2) -> Double.compare(
                calculateNodeTaskScore(task, n1),
                calculateNodeTaskScore(task, n2)
            ))
            .orElse(fogNodes.get(0));
    }
    
    /**
     * Calculate smart start time for a task
     */
    private double calculateSmartStartTime(IIoTTask task, Chromosome chromosome) {
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        double deadline = task.getDeadline();
        
        // Start time that gives some buffer before deadline
        return Math.max(0.0, Math.min(earliestStart, deadline * 0.6));
    }
    
    // Enums and helper classes for repair mechanism
    private enum RepairStrategy {
        TIME_SHIFT,      // Try adjusting start times
        NODE_MIGRATION,  // Try different nodes
        AGGRESSIVE,      // Cost-insensitive repairs
        EMERGENCY        // Last resort repairs
    }
    
    private static class RepairResult {
        int finalNodeId;
        double finishTime;
        boolean hadViolation;
        
        RepairResult() {
            this.hadViolation = false;
        }
    }
    
    /**
     * Enhanced Elite Hill-climbing: Multi-strategy local search for elite solutions
     * Implements multiple local search operators with adaptive selection
     */
    private void applyEliteHillClimbing() {
        List<Chromosome> improvedElites = new ArrayList<>();
        
        for (int i = 0; i < ELITE_SIZE && i < population.size(); i++) {
            Chromosome elite = population.get(i);
            
            // Apply multiple hill-climbing strategies
            Chromosome improved = performMultiStrategyHillClimb(elite);
            improvedElites.add(improved);
        }
        
        // Replace original elites with improved versions
        for (int i = 0; i < improvedElites.size(); i++) {
            Chromosome improved = improvedElites.get(i);
            if (improved.fitness < population.get(i).fitness) {
                population.set(i, improved);
            }
        }
    }
    
    /**
     * Multi-strategy hill-climbing with adaptive operator selection
     */
    private Chromosome performMultiStrategyHillClimb(Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        best.fitness = calculateFitness(best);
        
        // Strategy 1: Task Swap Hill-Climbing
        Chromosome swapResult = taskSwapHillClimb(chromosome);
        if (swapResult.fitness < best.fitness) {
            best = swapResult;
        }
        
        // Strategy 2: Node Migration Hill-Climbing
        Chromosome migrationResult = nodeMigrationHillClimb(chromosome);
        if (migrationResult.fitness < best.fitness) {
            best = migrationResult;
        }
        
        // Strategy 3: Time Optimization Hill-Climbing
        Chromosome timeResult = timeOptimizationHillClimb(chromosome);
        if (timeResult.fitness < best.fitness) {
            best = timeResult;
        }
        
        // Strategy 4: Critical Path Optimization
        Chromosome criticalResult = criticalPathHillClimb(chromosome);
        if (criticalResult.fitness < best.fitness) {
            best = criticalResult;
        }
        
        // Strategy 5: Hybrid Local Search (combine strategies)
        Chromosome hybridResult = hybridLocalSearch(best);
        if (hybridResult.fitness < best.fitness) {
            best = hybridResult;
        }
        
        return best;
    }
    
    /**
     * Task swap hill-climbing: Try swapping assignments between tasks
     */
    private Chromosome taskSwapHillClimb(Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        best.fitness = calculateFitness(best);
        
        List<Integer> taskIds = new ArrayList<>(chromosome.taskToNodeMap.keySet());
        final int MAX_SWAP_ATTEMPTS = Math.min(10, taskIds.size() * (taskIds.size() - 1) / 2);
        
        for (int attempt = 0; attempt < MAX_SWAP_ATTEMPTS; attempt++) {
            // Select two different tasks
            int task1Id = taskIds.get(random.nextInt(taskIds.size()));
            int task2Id = taskIds.get(random.nextInt(taskIds.size()));
            
            if (task1Id == task2Id) continue;
            
            // Create candidate with swapped assignments
            Chromosome candidate = new Chromosome(chromosome);
            
            // Swap node assignments
            Integer node1 = candidate.taskToNodeMap.get(task1Id);
            Integer node2 = candidate.taskToNodeMap.get(task2Id);
            candidate.taskToNodeMap.put(task1Id, node2);
            candidate.taskToNodeMap.put(task2Id, node1);
            
            // Swap start times
            Double time1 = candidate.taskStartTimes.get(task1Id);
            Double time2 = candidate.taskStartTimes.get(task2Id);
            candidate.taskStartTimes.put(task1Id, time2);
            candidate.taskStartTimes.put(task2Id, time1);
            
            // Repair and evaluate
            repairChromosome(candidate);
            candidate.fitness = calculateFitness(candidate);
            
            if (candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    /**
     * Node migration hill-climbing: Try moving tasks to better nodes
     */
    private Chromosome nodeMigrationHillClimb(Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        best.fitness = calculateFitness(best);
        
        List<Integer> taskIds = new ArrayList<>(chromosome.taskToNodeMap.keySet());
        
        for (Integer taskId : taskIds) {
            IIoTTask task = workflow.getTask(taskId);
            if (task == null) continue;
            
            int currentNodeId = chromosome.taskToNodeMap.get(taskId);
            
            // Try each alternative node
            for (FogNode node : fogNodes) {
                if (node.getId() == currentNodeId) continue;
                
                Chromosome candidate = new Chromosome(chromosome);
                candidate.taskToNodeMap.put(taskId, node.getId());
                
                // Optimize start time for new node
                double optimizedStartTime = optimizeStartTimeForTaskOnNode(task, node, candidate);
                candidate.taskStartTimes.put(taskId, optimizedStartTime);
                
                repairChromosome(candidate);
                candidate.fitness = calculateFitness(candidate);
                
                if (candidate.fitness < best.fitness) {
                    best = candidate;
                }
            }
        }
        
        return best;
    }
    
    /**
     * Time optimization hill-climbing: Fine-tune start times
     */
    private Chromosome timeOptimizationHillClimb(Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        best.fitness = calculateFitness(best);
        
        List<Integer> taskIds = new ArrayList<>(chromosome.taskToNodeMap.keySet());
        
        for (Integer taskId : taskIds) {
            IIoTTask task = workflow.getTask(taskId);
            if (task == null) continue;
            
            double currentStartTime = chromosome.taskStartTimes.get(taskId);
            double bestTime = currentStartTime;
            
            // Try different start times around current time
            double[] timeDeltas = {-5.0, -2.0, -1.0, -0.5, 0.5, 1.0, 2.0, 5.0};
            
            for (double delta : timeDeltas) {
                double newStartTime = Math.max(0.0, currentStartTime + delta);
                
                Chromosome candidate = new Chromosome(chromosome);
                candidate.taskStartTimes.put(taskId, newStartTime);
                
                // Check if this time is feasible considering dependencies
                if (isStartTimeFeasible(task, newStartTime, candidate)) {
                    repairChromosome(candidate);
                    candidate.fitness = calculateFitness(candidate);
                    
                    if (candidate.fitness < best.fitness) {
                        best = candidate;
                        bestTime = newStartTime;
                    }
                }
            }
        }
        
        return best;
    }
    
    /**
     * Critical path optimization hill-climbing
     */
    private Chromosome criticalPathHillClimb(Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        best.fitness = calculateFitness(best);
        
        // Identify critical path tasks (those with tight deadlines or high impact)
        List<IIoTTask> criticalTasks = identifyCriticalTasks();
        
        for (IIoTTask task : criticalTasks) {
            int taskId = task.getId();
            
            // Try to optimize critical tasks more aggressively
            Chromosome candidate = optimizeCriticalTask(task, chromosome);
            
            if (candidate != null) {
                repairChromosome(candidate);
                candidate.fitness = calculateFitness(candidate);
                
                if (candidate.fitness < best.fitness) {
                    best = candidate;
                }
            }
        }
        
        return best;
    }
    
    /**
     * Hybrid local search: Combine multiple operators
     */
    private Chromosome hybridLocalSearch(Chromosome chromosome) {
        Chromosome current = new Chromosome(chromosome);
        current.fitness = calculateFitness(current);
        
        boolean improved = true;
        int iterations = 0;
        final int MAX_ITERATIONS = 3;
        
        while (improved && iterations < MAX_ITERATIONS) {
            improved = false;
            Chromosome previous = new Chromosome(current);
            
            // Apply operators in sequence
            current = taskSwapHillClimb(current);
            if (current.fitness < previous.fitness) {
                improved = true;
            }
            
            current = timeOptimizationHillClimb(current);
            if (current.fitness < previous.fitness) {
                improved = true;
            }
            
            iterations++;
        }
        
        return current;
    }
    
    /**
     * Optimize start time for a task on a specific node
     */
    private double optimizeStartTimeForTaskOnNode(IIoTTask task, FogNode node, Chromosome chromosome) {
        double earliestStart = calculateEarliestPossibleStartTime(task, chromosome);
        
        double execTime = CostCalculator.calculateExecutionTime(task, node);
        double transferDelay = CostCalculator.calculateTransferDelay(task, node);
        double latency = node.getLatencyMs() / 1000.0;
        double totalTime = execTime + transferDelay + latency;
        
        double latestStart = task.getDeadline() - totalTime;
        
        // Choose optimal start time within feasible range
        return Math.max(earliestStart, Math.min(latestStart, earliestStart));
    }
    
    /**
     * Check if start time is feasible for a task
     */
    private boolean isStartTimeFeasible(IIoTTask task, double startTime, Chromosome chromosome) {
        // Check dependency constraints
        double earliestPossible = calculateEarliestPossibleStartTime(task, chromosome);
        if (startTime < earliestPossible) {
            return false;
        }
        
        // Check deadline constraint
        int nodeId = chromosome.taskToNodeMap.get(task.getId());
        FogNode node = getNodeById(nodeId);
        
        double execTime = CostCalculator.calculateExecutionTime(task, node);
        double transferDelay = CostCalculator.calculateTransferDelay(task, node);
        double latency = node.getLatencyMs() / 1000.0;
        double finishTime = startTime + execTime + transferDelay + latency;
        
        return finishTime <= task.getDeadline();
    }
    
    /**
     * Optimize a critical task aggressively
     */
    private Chromosome optimizeCriticalTask(IIoTTask task, Chromosome chromosome) {
        Chromosome best = new Chromosome(chromosome);
        int taskId = task.getId();
        
        // Try the most suitable node for this critical task
        FogNode bestNode = findMostSuitableNode(task);
        best.taskToNodeMap.put(taskId, bestNode.getId());
        
        // Calculate optimal start time
        double optimalStartTime = optimizeStartTimeForTaskOnNode(task, bestNode, best);
        best.taskStartTimes.put(taskId, optimalStartTime);
        
        return best;
    }
    
    /**
     * Helper methods
     */
    private int getRandomTaskId(Chromosome chromosome) {
        List<Integer> taskIds = new ArrayList<>(chromosome.taskToNodeMap.keySet());
        if (taskIds.isEmpty()) {
            // If no tasks assigned, get from workflow
            List<IIoTTask> allTasks = new ArrayList<>(workflow.getAllTasks());
            if (!allTasks.isEmpty()) {
                return allTasks.get(random.nextInt(allTasks.size())).getId();
            }
            return 1; // fallback
        }
        
        // Ensure we have valid task IDs
        if (taskIds.isEmpty()) {
            List<IIoTTask> allTasks = new ArrayList<>(workflow.getAllTasks());
            if (!allTasks.isEmpty()) {
                return allTasks.get(random.nextInt(allTasks.size())).getId();
            }
            return 1; // fallback
        }
        
        return taskIds.get(random.nextInt(taskIds.size()));
    }
    
    private FogNode getNodeById(int nodeId) {
        for (FogNode node : fogNodes) {
            if (node.getId() == nodeId) {
                return node;
            }
        }
        // If node not found, return first available fog node
        if (!fogNodes.isEmpty()) {
            return fogNodes.get(0);
        }
        // This should never happen, but just in case
        throw new IllegalStateException("No fog nodes available for node ID: " + nodeId);
    }
    
    private FogNode findFastestNode(IIoTTask task) {
        FogNode fastest = null;
        double fastestTime = Double.MAX_VALUE;
        
        for (FogNode node : fogNodes) {
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            if (execTime < fastestTime) {
                fastestTime = execTime;
                fastest = node;
            }
        }
        
        return fastest;
    }
    
    /**
     * Convert chromosome to scheduling result
     */
    private SchedulingResult convertToSchedulingResult(Chromosome chromosome) {
        Map<Integer, Integer> taskAssignments = new HashMap<>(chromosome.taskToNodeMap);
        Map<Integer, Double> startTimes = new HashMap<>(chromosome.taskStartTimes);
        
        return new SchedulingResult(taskAssignments, startTimes, chromosome.fitness);
    }
    
    /**
     * Result class for scheduling output
     */
    public static class SchedulingResult {
        public final Map<Integer, Integer> taskAssignments;
        public final Map<Integer, Double> startTimes;
        public final double totalCost;
        
        public SchedulingResult(Map<Integer, Integer> taskAssignments, 
                             Map<Integer, Double> startTimes, 
                             double totalCost) {
            this.taskAssignments = taskAssignments;
            this.startTimes = startTimes;
            this.totalCost = totalCost;
        }
    }
}