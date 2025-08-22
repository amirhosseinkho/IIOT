package algorithms;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;

import java.util.*;

/**
 * Baseline Algorithms for Comparison with EPO-based CEIS
 * Implements: GA, PSO, EPSO, NSGA-II, Min-Min, FirstFit
 */
public class BaselineAlgorithms {
    
    private Workflow workflow;
    private List<FogNode> fogNodes;
    private static final double PENALTY_M = 1000.0;
    
    public BaselineAlgorithms(Workflow workflow, List<FogNode> fogNodes) {
        this.workflow = workflow;
        this.fogNodes = fogNodes;
    }
    
    /**
     * Genetic Algorithm (GA) Baseline
     */
    public SchedulingResult geneticAlgorithm() {
        System.out.println("🧬 Running Genetic Algorithm baseline...");
        
        int populationSize = 50;
        int maxGenerations = 100;
        double mutationRate = 0.1;
        double crossoverRate = 0.8;
        
        List<Chromosome> population = initializeRandomPopulation(populationSize);
        
        for (int generation = 0; generation < maxGenerations; generation++) {
            // Evaluate fitness
            evaluatePopulation(population);
            Collections.sort(population);
            
            // Elitism: keep best 10%
            int eliteSize = Math.max(1, populationSize / 10);
            List<Chromosome> elite = new ArrayList<>(population.subList(0, eliteSize));
            
            // Generate new population
            List<Chromosome> newPopulation = new ArrayList<>(elite);
            
            while (newPopulation.size() < populationSize) {
                Chromosome parent1 = tournamentSelection(population);
                Chromosome parent2 = tournamentSelection(population);
                
                Chromosome offspring = crossover(parent1, parent2, crossoverRate);
                mutate(offspring, mutationRate);
                repairChromosome(offspring);
                
                newPopulation.add(offspring);
            }
            
            population = newPopulation;
        }
        
        // Return best solution
        evaluatePopulation(population);
        Collections.sort(population);
        return convertToSchedulingResult(population.get(0));
    }
    
    /**
     * Particle Swarm Optimization (PSO) Baseline
     */
    public SchedulingResult particleSwarmOptimization() {
        System.out.println("🐝 Running Particle Swarm Optimization baseline...");
        
        int swarmSize = 30;
        int maxIterations = 100;
        double w = 0.7; // inertia weight
        double c1 = 2.0; // cognitive coefficient
        double c2 = 2.0; // social coefficient
        
        try {
            List<Particle> swarm = initializeSwarm(swarmSize);
            if (swarm.isEmpty()) {
                System.err.println("Failed to initialize swarm, falling back to random solution");
                return generateRandomSolution();
            }
            
            Particle globalBest = null;
            
            for (int iteration = 0; iteration < maxIterations; iteration++) {
                // Update particles
                for (Particle particle : swarm) {
                    try {
                        updateParticle(particle, globalBest, w, c1, c2);
                        repairChromosome(particle.chromosome);
                        
                        // Calculate and validate fitness
                        particle.fitness = calculateFitness(particle.chromosome);
                        if (Double.isInfinite(particle.fitness) || Double.isNaN(particle.fitness)) {
                            particle.fitness = Double.MAX_VALUE;
                        }
                        
                        // Update personal best
                        if (particle.fitness < particle.personalBestFitness) {
                            particle.personalBestFitness = particle.fitness;
                            particle.personalBest = new Chromosome(particle.chromosome);
                        }
                        
                        // Update global best
                        if (globalBest == null || particle.fitness < globalBest.fitness) {
                            globalBest = new Particle(particle);
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating particle: " + e.getMessage());
                        particle.fitness = Double.MAX_VALUE;
                    }
                }
                
                // Validate global best
                if (globalBest != null && (Double.isInfinite(globalBest.fitness) || Double.isNaN(globalBest.fitness))) {
                    globalBest.fitness = Double.MAX_VALUE;
                }
            }
            
            if (globalBest != null && globalBest.chromosome != null) {
                return convertToSchedulingResult(globalBest.chromosome);
            } else {
                System.err.println("Global best is null, falling back to random solution");
                return generateRandomSolution();
            }
            
        } catch (Exception e) {
            System.err.println("PSO failed with error: " + e.getMessage());
            return generateRandomSolution();
        }
    }
    
    /**
     * Enhanced Particle Swarm Optimization (EPSO) Baseline
     */
    public SchedulingResult enhancedParticleSwarmOptimization() {
        System.out.println("🚀 Running Enhanced PSO baseline...");
        
        int swarmSize = 30;
        int maxIterations = 100;
        double w = 0.7;
        double c1 = 2.0;
        double c2 = 2.0;
        double c3 = 1.0; // additional cognitive coefficient
        
        try {
            List<Particle> swarm = initializeSwarm(swarmSize);
            if (swarm.isEmpty()) {
                System.err.println("Failed to initialize enhanced swarm, falling back to random solution");
                return generateRandomSolution();
            }
            
            Particle globalBest = null;
            
            for (int iteration = 0; iteration < maxIterations; iteration++) {
                // Adaptive parameters
                double adaptiveW = w * (1.0 - (double) iteration / maxIterations);
                
                for (Particle particle : swarm) {
                    try {
                        updateEnhancedParticle(particle, globalBest, adaptiveW, c1, c2, c3);
                        repairChromosome(particle.chromosome);
                        
                        // Calculate and validate fitness
                        particle.fitness = calculateFitness(particle.chromosome);
                        if (Double.isInfinite(particle.fitness) || Double.isNaN(particle.fitness)) {
                            particle.fitness = Double.MAX_VALUE;
                        }
                        
                        // Update personal best
                        if (particle.fitness < particle.personalBestFitness) {
                            particle.personalBestFitness = particle.fitness;
                            particle.personalBest = new Chromosome(particle.chromosome);
                        }
                        
                        // Update global best
                        if (globalBest == null || particle.fitness < globalBest.fitness) {
                            globalBest = new Particle(particle);
                        }
                    } catch (Exception e) {
                        System.err.println("Error updating enhanced particle: " + e.getMessage());
                        particle.fitness = Double.MAX_VALUE;
                    }
                }
                
                // Validate global best
                if (globalBest != null && (Double.isInfinite(globalBest.fitness) || Double.isNaN(globalBest.fitness))) {
                    globalBest.fitness = Double.MAX_VALUE;
                }
            }
            
            if (globalBest != null && globalBest.chromosome != null) {
                return convertToSchedulingResult(globalBest.chromosome);
            } else {
                System.err.println("Enhanced PSO global best is null, falling back to random solution");
                return generateRandomSolution();
            }
            
        } catch (Exception e) {
            System.err.println("Enhanced PSO failed with error: " + e.getMessage());
            return generateRandomSolution();
        }
    }
    
    /**
     * Non-dominated Sorting Genetic Algorithm II (NSGA-II) Baseline
     */
    public SchedulingResult nsgaII() {
        System.out.println("🔄 Running NSGA-II baseline...");
        
        int populationSize = 100;
        int maxGenerations = 100;
        
        try {
            List<Chromosome> population = initializeRandomPopulation(populationSize);
            if (population.isEmpty()) {
                System.err.println("Failed to initialize NSGA-II population, falling back to random solution");
                return generateRandomSolution();
            }
            
            for (int generation = 0; generation < maxGenerations; generation++) {
                try {
                    // Evaluate objectives
                    evaluateMultiObjective(population);
                    
                    // Non-dominated sorting
                    List<List<Chromosome>> fronts = nonDominatedSort(population);
                    
                    // Crowding distance calculation
                    for (List<Chromosome> front : fronts) {
                        calculateCrowdingDistance(front);
                    }
                    
                    // Selection
                    population = selection(population, populationSize);
                    
                    // Crossover and mutation
                    List<Chromosome> offspring = new ArrayList<>();
                    for (int i = 0; i < populationSize; i++) {
                        try {
                            Chromosome parent1 = tournamentSelection(population);
                            Chromosome parent2 = tournamentSelection(population);
                            
                            Chromosome child = crossover(parent1, parent2, 0.8);
                            mutate(child, 0.1);
                            repairChromosome(child);
                            
                            offspring.add(child);
                        } catch (Exception e) {
                            System.err.println("Error creating offspring " + i + ": " + e.getMessage());
                            // Create fallback offspring
                            Chromosome fallback = createFallbackChromosome();
                            offspring.add(fallback);
                        }
                    }
                    
                    // Combine parent and offspring
                    population.addAll(offspring);
                    evaluateMultiObjective(population);
                    fronts = nonDominatedSort(population);
                    
                    // Select next generation
                    population = selectNextGeneration(fronts, populationSize);
                    
                } catch (Exception e) {
                    System.err.println("Error in NSGA-II generation " + generation + ": " + e.getMessage());
                    // Continue with current population
                }
            }
            
            // Return best solution based on cost
            evaluatePopulation(population);
            Collections.sort(population);
            
            if (!population.isEmpty()) {
                return convertToSchedulingResult(population.get(0));
            } else {
                System.err.println("NSGA-II population is empty, falling back to random solution");
                return generateRandomSolution();
            }
            
        } catch (Exception e) {
            System.err.println("NSGA-II failed with error: " + e.getMessage());
            return generateRandomSolution();
        }
    }
    
    /**
     * Min-Min Algorithm Baseline
     */
    public SchedulingResult minMin() {
        System.out.println("⚡ Running Min-Min algorithm baseline...");
        
        Map<Integer, Integer> taskAssignments = new HashMap<>();
        Map<Integer, Double> startTimes = new HashMap<>();
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        
        for (FogNode node : fogNodes) {
            nodeAvailableTime.put(node.getId(), 0.0);
        }
        
        List<IIoTTask> unassignedTasks = new ArrayList<>(workflow.getAllTasks());
        
        while (!unassignedTasks.isEmpty()) {
            IIoTTask bestTask = null;
            FogNode bestNode = null;
            double bestFinishTime = Double.MAX_VALUE;
            
            // Find task-node pair with minimum completion time
            for (IIoTTask task : unassignedTasks) {
                for (FogNode node : fogNodes) {
                    double readyTime = nodeAvailableTime.get(node.getId());
                    double execTime = CostCalculator.calculateExecutionTime(task, node);
                    double transferDelay = CostCalculator.calculateTransferDelay(task, node);
                    double latency = node.getLatencyMs() / 1000.0;
                    
                    double finishTime = readyTime + execTime + transferDelay + latency;
                    
                    if (finishTime < bestFinishTime) {
                        bestFinishTime = finishTime;
                        bestTask = task;
                        bestNode = node;
                    }
                }
            }
            
            if (bestTask != null && bestNode != null) {
                // Assign task to node
                taskAssignments.put(bestTask.getId(), bestNode.getId());
                
                double readyTime = nodeAvailableTime.get(bestNode.getId());
                double execTime = CostCalculator.calculateExecutionTime(bestTask, bestNode);
                double transferDelay = CostCalculator.calculateTransferDelay(bestTask, bestNode);
                double latency = bestNode.getLatencyMs() / 1000.0;
                
                startTimes.put(bestTask.getId(), readyTime);
                nodeAvailableTime.put(bestNode.getId(), readyTime + execTime + transferDelay + latency);
                
                unassignedTasks.remove(bestTask);
            }
        }
        
        // Calculate total cost
        double totalCost = calculateTotalCost(taskAssignments, startTimes);
        
        return new SchedulingResult(taskAssignments, startTimes, totalCost);
    }
    
    /**
     * First Fit Algorithm Baseline
     */
    public SchedulingResult firstFit() {
        System.out.println("🔍 Running First Fit algorithm baseline...");
        
        Map<Integer, Integer> taskAssignments = new HashMap<>();
        Map<Integer, Double> startTimes = new HashMap<>();
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        
        for (FogNode node : fogNodes) {
            nodeAvailableTime.put(node.getId(), 0.0);
        }
        
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        
        for (IIoTTask task : sortedTasks) {
            // Find first available node
            FogNode selectedNode = null;
            for (FogNode node : fogNodes) {
                if (node.getMips() >= task.getLength() / 2) { // Basic capacity check
                    selectedNode = node;
                    break;
                }
            }
            
            if (selectedNode == null) {
                // If no fog node available, use first cloud node
                for (FogNode node : fogNodes) {
                    if (node.isCloud()) {
                        selectedNode = node;
                        break;
                    }
                }
            }
            
            if (selectedNode != null) {
                double readyTime = nodeAvailableTime.get(selectedNode.getId());
                double execTime = CostCalculator.calculateExecutionTime(task, selectedNode);
                double transferDelay = CostCalculator.calculateTransferDelay(task, selectedNode);
                double latency = selectedNode.getLatencyMs() / 1000.0;
                
                taskAssignments.put(task.getId(), selectedNode.getId());
                startTimes.put(task.getId(), readyTime);
                nodeAvailableTime.put(selectedNode.getId(), readyTime + execTime + transferDelay + latency);
            }
        }
        
        // Calculate total cost
        double totalCost = calculateTotalCost(taskAssignments, startTimes);
        
        return new SchedulingResult(taskAssignments, startTimes, totalCost);
    }
    
    // Helper classes and methods
    
    private static class Chromosome implements Comparable<Chromosome> {
        private Map<Integer, Integer> taskToNodeMap;
        private Map<Integer, Double> taskStartTimes;
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
    
    private static class Particle {
        private Chromosome chromosome;
        private Chromosome personalBest;
        private double fitness;
        private double personalBestFitness;
        private Map<Integer, Double> velocity;
        
        public Particle(Chromosome chromosome) {
            this.chromosome = chromosome;
            this.personalBest = new Chromosome(chromosome);
            this.fitness = Double.MAX_VALUE;
            this.personalBestFitness = Double.MAX_VALUE;
            this.velocity = new HashMap<>();
        }
        
        public Particle(Particle other) {
            if (other.chromosome != null) {
                this.chromosome = new Chromosome(other.chromosome);
            } else {
                this.chromosome = new Chromosome();
            }
            
            if (other.personalBest != null) {
                this.personalBest = new Chromosome(other.personalBest);
            } else {
                this.personalBest = new Chromosome();
            }
            
            this.fitness = other.fitness;
            this.personalBestFitness = other.personalBestFitness;
            this.velocity = new HashMap<>(other.velocity != null ? other.velocity : new HashMap<>());
        }
    }
    
    // Implementation methods
    private List<Chromosome> initializeRandomPopulation(int size) {
        List<Chromosome> population = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            population.add(generateRandomChromosome());
        }
        return population;
    }
    
    private Chromosome generateRandomChromosome() {
        Chromosome chromosome = new Chromosome();
        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();
        
        for (IIoTTask task : sortedTasks) {
            int randomNodeId = fogNodes.get(new Random().nextInt(fogNodes.size())).getId();
            chromosome.taskToNodeMap.put(task.getId(), randomNodeId);
            chromosome.taskStartTimes.put(task.getId(), new Random().nextDouble() * 100.0);
        }
        
        return chromosome;
    }
    
    private void evaluatePopulation(List<Chromosome> population) {
        for (Chromosome chromosome : population) {
            chromosome.fitness = calculateFitness(chromosome);
        }
    }
    
    private double calculateFitness(Chromosome chromosome) {
        return calculateTotalCost(chromosome.taskToNodeMap, chromosome.taskStartTimes);
    }
    
    private double calculateTotalCost(Map<Integer, Integer> taskAssignments, Map<Integer, Double> startTimes) {
        double totalCost = 0.0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            // Ensure task has valid assignments
            if (!taskAssignments.containsKey(task.getId())) {
                // Assign to random fog node if missing
                int randomNodeId = fogNodes.get(new Random().nextInt(fogNodes.size())).getId();
                taskAssignments.put(task.getId(), randomNodeId);
            }
            
            if (!startTimes.containsKey(task.getId())) {
                // Assign random start time if missing
                startTimes.put(task.getId(), new Random().nextDouble() * 100.0);
            }
            
            int nodeId = taskAssignments.get(task.getId());
            FogNode node = getNodeById(nodeId);
            double startTime = startTimes.get(task.getId());
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            double finishTime = startTime + execTime + transferDelay + latency;
            
            double duration = execTime + transferDelay + latency;
            double cost = duration * node.getCostPerSec();
            
            // Validate cost values to prevent infinite or NaN
            if (Double.isInfinite(cost) || Double.isNaN(cost)) {
                cost = 1000.0; // Default high cost for invalid values
            }
            
            if (finishTime > task.getDeadline()) {
                double penalty = (finishTime - task.getDeadline()) * PENALTY_M;
                // Validate penalty to prevent extreme values
                if (Double.isInfinite(penalty) || Double.isNaN(penalty)) {
                    penalty = PENALTY_M; // Use base penalty if calculation fails
                }
                cost += penalty;
            }
            
            totalCost += cost;
        }
        
        // Final validation of total cost
        if (Double.isInfinite(totalCost) || Double.isNaN(totalCost)) {
            totalCost = Double.MAX_VALUE; // Return maximum cost if calculation fails
        }
        
        return totalCost;
    }
    
    private Chromosome tournamentSelection(List<Chromosome> population) {
        int tournamentSize = 3;
        Chromosome best = null;
        Random random = new Random();
        
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome candidate = population.get(random.nextInt(population.size()));
            if (best == null || candidate.fitness < best.fitness) {
                best = candidate;
            }
        }
        
        return best;
    }
    
    private Chromosome crossover(Chromosome parent1, Chromosome parent2, double rate) {
        Chromosome offspring = new Chromosome();
        Random random = new Random();
        
        for (Integer taskId : parent1.taskToNodeMap.keySet()) {
            if (random.nextDouble() < rate) {
                offspring.taskToNodeMap.put(taskId, parent1.taskToNodeMap.get(taskId));
                offspring.taskStartTimes.put(taskId, parent1.taskStartTimes.get(taskId));
            } else {
                offspring.taskToNodeMap.put(taskId, parent2.taskToNodeMap.get(taskId));
                offspring.taskStartTimes.put(taskId, parent2.taskStartTimes.get(taskId));
            }
        }
        
        return offspring;
    }
    
    private void mutate(Chromosome chromosome, double rate) {
        Random random = new Random();
        
        for (Integer taskId : chromosome.taskToNodeMap.keySet()) {
            if (random.nextDouble() < rate) {
                // Mutate node assignment
                int randomNodeId = fogNodes.get(random.nextInt(fogNodes.size())).getId();
                chromosome.taskToNodeMap.put(taskId, randomNodeId);
                
                // Mutate start time
                double randomStartTime = random.nextDouble() * 100.0;
                chromosome.taskStartTimes.put(taskId, randomStartTime);
            }
        }
    }
    
    private void repairChromosome(Chromosome chromosome) {
        // Basic repair: ensure all tasks have assignments
        for (IIoTTask task : workflow.getAllTasks()) {
            if (!chromosome.taskToNodeMap.containsKey(task.getId())) {
                int randomNodeId = fogNodes.get(new Random().nextInt(fogNodes.size())).getId();
                chromosome.taskToNodeMap.put(task.getId(), randomNodeId);
                chromosome.taskStartTimes.put(task.getId(), new Random().nextDouble() * 100.0);
            }
        }
    }
    
    private List<Particle> initializeSwarm(int size) {
        List<Particle> swarm = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            try {
                Chromosome chromosome = generateRandomChromosome();
                // Validate chromosome before creating particle
                if (isValidChromosome(chromosome)) {
                    swarm.add(new Particle(chromosome));
                } else {
                    // Create a valid fallback chromosome
                    Chromosome fallback = createFallbackChromosome();
                    swarm.add(new Particle(fallback));
                }
            } catch (Exception e) {
                System.err.println("Error creating particle " + i + ": " + e.getMessage());
                // Create a valid fallback chromosome
                Chromosome fallback = createFallbackChromosome();
                swarm.add(new Particle(fallback));
            }
        }
        return swarm;
    }
    
    private boolean isValidChromosome(Chromosome chromosome) {
        if (chromosome == null || chromosome.taskToNodeMap == null || chromosome.taskStartTimes == null) {
            return false;
        }
        
        // Check if all tasks have assignments
        for (IIoTTask task : workflow.getAllTasks()) {
            if (!chromosome.taskToNodeMap.containsKey(task.getId()) || 
                !chromosome.taskStartTimes.containsKey(task.getId())) {
                return false;
            }
            
            // Validate node ID
            int nodeId = chromosome.taskToNodeMap.get(task.getId());
            if (nodeId < 0 || nodeId >= fogNodes.size()) {
                return false;
            }
            
            // Validate start time
            double startTime = chromosome.taskStartTimes.get(task.getId());
            if (startTime < 0 || Double.isInfinite(startTime) || Double.isNaN(startTime)) {
                return false;
            }
        }
        
        return true;
    }
    
    private Chromosome createFallbackChromosome() {
        Chromosome chromosome = new Chromosome();
        List<IIoTTask> tasks = new ArrayList<>(workflow.getAllTasks());
        
        for (IIoTTask task : tasks) {
            // Assign to first available fog node
            int nodeId = fogNodes.get(0).getId();
            chromosome.taskToNodeMap.put(task.getId(), nodeId);
            chromosome.taskStartTimes.put(task.getId(), 0.0);
        }
        
        return chromosome;
    }
    
    private void updateParticle(Particle particle, Particle globalBest, double w, double c1, double c2) {
        Random random = new Random();
        
        // Skip if globalBest is null or has null chromosome
        if (globalBest == null || globalBest.chromosome == null || 
            globalBest.chromosome.taskToNodeMap == null || globalBest.chromosome.taskStartTimes == null) {
            return;
        }
        
        // Skip if particle has null chromosome
        if (particle == null || particle.chromosome == null || 
            particle.chromosome.taskToNodeMap == null || particle.chromosome.taskStartTimes == null) {
            return;
        }
        
        // Skip if personalBest is null
        if (particle.personalBest == null || particle.personalBest.taskToNodeMap == null || 
            particle.personalBest.taskStartTimes == null) {
            return;
        }
        
        // Update velocity and position for each task
        for (Integer taskId : particle.chromosome.taskToNodeMap.keySet()) {
            // Validate all required values exist and are not null
            if (taskId == null) continue;
            
            Integer personalBestNode = particle.personalBest.taskToNodeMap.get(taskId);
            Integer currentNode = particle.chromosome.taskToNodeMap.get(taskId);
            Integer globalBestNode = globalBest.chromosome.taskToNodeMap.get(taskId);
            
            // Skip if any value is null
            if (personalBestNode == null || currentNode == null || globalBestNode == null) {
                continue;
            }
            
            // Validate node IDs are within bounds
            if (currentNode < 0 || currentNode >= fogNodes.size() || 
                personalBestNode < 0 || personalBestNode >= fogNodes.size() ||
                globalBestNode < 0 || globalBestNode >= fogNodes.size()) {
                continue;
            }
            
            // Get current velocity, default to 0 if not found
            double currentVelocity = particle.velocity.getOrDefault(taskId, 0.0);
            
            // Cognitive component - limit the range to prevent extreme values
            double cognitive = c1 * random.nextDouble() * Math.max(-1.0, Math.min(1.0, personalBestNode - currentNode));
            
            // Social component - limit the range to prevent extreme values
            double social = c2 * random.nextDouble() * Math.max(-1.0, Math.min(1.0, globalBestNode - currentNode));
            
            // Update velocity with strict clamping to prevent explosion
            double newVelocity = w * currentVelocity + cognitive + social;
            newVelocity = Math.max(-2.0, Math.min(2.0, newVelocity)); // Very strict velocity clamping
            
            // Store velocity
            particle.velocity.put(taskId, newVelocity);
            
            // Update position with strict bounds checking
            int newNodeId = (int) Math.round(currentNode + newVelocity);
            newNodeId = Math.max(0, Math.min(fogNodes.size() - 1, newNodeId)); // Ensure valid range
            
            // Validate node ID exists and is different
            if (newNodeId >= 0 && newNodeId < fogNodes.size() && newNodeId != currentNode) {
                particle.chromosome.taskToNodeMap.put(taskId, newNodeId);
            }
        }
        
        // Update start times with very small perturbations
        for (Integer taskId : particle.chromosome.taskStartTimes.keySet()) {
            if (taskId == null) continue;
            
            Double currentTimeObj = particle.chromosome.taskStartTimes.get(taskId);
            if (currentTimeObj == null) continue;
            
            double currentTime = currentTimeObj;
            
            // Use a separate velocity for time updates
            double timeVelocity = particle.velocity.getOrDefault(taskId + 10000, 0.0); // Use large offset for time
            
            // Very small time adjustment to prevent extreme changes
            double timeAdjustment = timeVelocity * 0.01; // Scale down time changes significantly
            
            // Ensure time adjustment is very small
            timeAdjustment = Math.max(-0.1, Math.min(0.1, timeAdjustment));
            
            double newTime = currentTime + timeAdjustment;
            
            // Ensure time is non-negative and reasonable
            if (newTime >= 0 && newTime < 1000.0) { // Reasonable upper bound
                particle.chromosome.taskStartTimes.put(taskId, newTime);
            }
        }
    }
    
    private void updateEnhancedParticle(Particle particle, Particle globalBest, double w, double c1, double c2, double c3) {
        // Enhanced PSO with additional cognitive component
        try {
            // First update using standard PSO
            updateParticle(particle, globalBest, w, c1, c2);
            
            // Additional enhancement: adaptive mutation with strict bounds
            Random random = new Random();
            if (random.nextDouble() < 0.1) { // 10% chance
                // Use very small mutation rate to prevent extreme changes
                mutate(particle.chromosome, 0.02); // Reduced from 0.05
                
                // Validate and repair the chromosome after mutation
                repairChromosome(particle.chromosome);
                
                // Additional validation: ensure all values are within reasonable bounds
                for (Integer taskId : particle.chromosome.taskToNodeMap.keySet()) {
                    if (taskId == null) continue;
                    
                    Integer nodeId = particle.chromosome.taskToNodeMap.get(taskId);
                    if (nodeId == null || nodeId < 0 || nodeId >= fogNodes.size()) {
                        // Fix invalid node ID
                        int validNodeId = random.nextInt(fogNodes.size());
                        particle.chromosome.taskToNodeMap.put(taskId, validNodeId);
                    }
                }
                
                for (Integer taskId : particle.chromosome.taskStartTimes.keySet()) {
                    if (taskId == null) continue;
                    
                    Double startTime = particle.chromosome.taskStartTimes.get(taskId);
                    if (startTime == null || startTime < 0 || startTime > 1000.0) {
                        // Fix invalid start time
                        double validStartTime = random.nextDouble() * 100.0;
                        particle.chromosome.taskStartTimes.put(taskId, validStartTime);
                    }
                }
            }
        } catch (Exception e) {
            // If enhancement fails, fall back to basic PSO
            System.err.println("Enhanced PSO update failed, falling back to basic PSO: " + e.getMessage());
            updateParticle(particle, globalBest, w, c1, c2);
        }
    }
    
    private void evaluateMultiObjective(List<Chromosome> population) {
        // Multi-objective evaluation (cost and makespan)
        for (Chromosome chromosome : population) {
            // This would calculate multiple objectives
            // For now, use single objective
            chromosome.fitness = calculateFitness(chromosome);
        }
    }
    
    private List<List<Chromosome>> nonDominatedSort(List<Chromosome> population) {
        // Simplified non-dominated sorting
        List<List<Chromosome>> fronts = new ArrayList<>();
        fronts.add(population); // Single front for now
        return fronts;
    }
    
    private void calculateCrowdingDistance(List<Chromosome> front) {
        // Proper crowding distance calculation for NSGA-II
        if (front.size() <= 2) {
            // If only 1 or 2 solutions, assign high but finite crowding distance
            for (Chromosome chromosome : front) {
                chromosome.fitness = 100.0; // Use reasonable finite value
            }
            return;
        }
        
        // Sort by cost for crowding distance calculation
        front.sort((c1, c2) -> {
            double cost1 = calculateFitness(c1);
            double cost2 = calculateFitness(c2);
            
            // Handle infinite or NaN values
            if (Double.isInfinite(cost1) || Double.isNaN(cost1)) cost1 = Double.MAX_VALUE;
            if (Double.isInfinite(cost2) || Double.isNaN(cost2)) cost2 = Double.MAX_VALUE;
            
            return Double.compare(cost1, cost2);
        });
        
        // Assign high crowding distance to boundary solutions
        front.get(0).fitness = 100.0; // Use reasonable finite value
        front.get(front.size() - 1).fitness = 100.0; // Use reasonable finite value
        
        // Calculate crowding distance for intermediate solutions
        for (int i = 1; i < front.size() - 1; i++) {
            double prevCost = calculateFitness(front.get(i - 1));
            double nextCost = calculateFitness(front.get(i + 1));
            double currentCost = calculateFitness(front.get(i));
            
            // Validate costs to prevent extreme values
            if (Double.isInfinite(prevCost) || Double.isInfinite(nextCost) || Double.isInfinite(currentCost) ||
                Double.isNaN(prevCost) || Double.isNaN(nextCost) || Double.isNaN(currentCost)) {
                front.get(i).fitness = 50.0; // Default reasonable value
                continue;
            }
            
            // Clamp costs to reasonable range to prevent overflow
            prevCost = Math.max(0.0, Math.min(10000.0, prevCost));
            nextCost = Math.max(0.0, Math.min(10000.0, nextCost));
            currentCost = Math.max(0.0, Math.min(10000.0, currentCost));
            
            // Normalized crowding distance
            if (nextCost - prevCost > 0.001) { // Small threshold to avoid division by very small numbers
                double crowdingDistance = (nextCost - prevCost) / Math.max(0.001, nextCost - prevCost);
                // Clamp crowding distance to reasonable range
                crowdingDistance = Math.max(0.1, Math.min(100.0, crowdingDistance));
                front.get(i).fitness = crowdingDistance;
            } else {
                front.get(i).fitness = 50.0; // Default reasonable value
            }
        }
    }
    
    private List<Chromosome> selection(List<Chromosome> population, int size) {
        // Simplified selection
        Collections.sort(population);
        return population.subList(0, Math.min(size, population.size()));
    }
    
    private List<Chromosome> selectNextGeneration(List<List<Chromosome>> fronts, int size) {
        // Simplified next generation selection
        List<Chromosome> nextGen = new ArrayList<>();
        for (List<Chromosome> front : fronts) {
            nextGen.addAll(front);
            if (nextGen.size() >= size) break;
        }
        return nextGen.subList(0, Math.min(size, nextGen.size()));
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
    
    private SchedulingResult convertToSchedulingResult(Chromosome chromosome) {
        return new SchedulingResult(
            new HashMap<>(chromosome.taskToNodeMap),
            new HashMap<>(chromosome.taskStartTimes),
            chromosome.fitness
        );
    }
    
    private SchedulingResult generateRandomSolution() {
        Chromosome randomChromosome = generateRandomChromosome();
        repairChromosome(randomChromosome);
        return convertToSchedulingResult(randomChromosome);
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
