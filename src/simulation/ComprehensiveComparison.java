package simulation;

import algorithms.*;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;

import java.util.*;
import java.io.*;
import java.util.stream.DoubleStream;

/**
 * Comprehensive Comparison Framework
 * Compares EPO-based CEIS, HFCO, and baseline algorithms
 * Generates performance metrics and analysis
 */
public class ComprehensiveComparison {
    
    private Workflow workflow;
    private List<FogNode> allNodes;
    private List<FogNode> fogNodes;
    private List<FogNode> cloudNodes;
    
    // Results storage
    private Map<String, AlgorithmResult> results;
    private List<PerformanceMetrics> allMetrics;
    
    public ComprehensiveComparison(Workflow workflow, List<FogNode> allNodes) {
        this.workflow = workflow;
        this.allNodes = allNodes;
        this.results = new HashMap<>();
        this.allMetrics = new ArrayList<>();
        
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
     * Run comprehensive comparison of all algorithms
     */
    public void runComprehensiveComparison() {
        System.out.println("🚀 Starting Comprehensive Algorithm Comparison");
        System.out.println("=============================================");
        System.out.println("Workflow: " + workflow.getAllTasks().size() + " tasks");
        System.out.println("Nodes: " + fogNodes.size() + " fog, " + cloudNodes.size() + " cloud");
        System.out.println();
        
        // Run all algorithms
        runEPOBasedCEIS();
        runHFCO();
        runBaselineAlgorithms();
        
        // Generate comprehensive analysis
        generatePerformanceAnalysis();
        generateCharts();
        generateStatisticalAnalysis();
        
        // Save results
        saveResultsToFile();
        
        System.out.println("\n✅ Comprehensive comparison completed!");
    }
    
    /**
     * Run EPO-based CEIS algorithm
     */
    private void runEPOBasedCEIS() {
        System.out.println("🎯 Running EPO-based CEIS Algorithm...");
        
        EnhancedEPOCEIS ceis = new EnhancedEPOCEIS(workflow, allNodes);
        EnhancedEPOCEIS.SchedulingResult result = ceis.schedule();
        
        AlgorithmResult algorithmResult = new AlgorithmResult(
            "EPO-CEIS",
            result.taskAssignments,
            result.startTimes,
            result.totalCost,
            System.currentTimeMillis()
        );
        
        results.put("EPO-CEIS", algorithmResult);
        
        // Calculate metrics
        PerformanceMetrics metrics = calculatePerformanceMetrics(algorithmResult);
        allMetrics.add(metrics);
        
        System.out.println("   ✅ Completed - Total Cost: " + String.format("%.2f", result.totalCost));
    }
    
    /**
     * Run HFCO algorithm
     */
    private void runHFCO() {
        System.out.println("🌐 Running HFCO Algorithm...");
        
        HFCOWithPlacement hfco = new HFCOWithPlacement(allNodes, workflow);
        HFCOWithPlacement.HFCOResult result = hfco.optimize();
        
        AlgorithmResult algorithmResult = new AlgorithmResult(
            "HFCO",
            result.schedulingResult.taskAssignments,
            result.schedulingResult.startTimes,
            result.schedulingResult.totalCost,
            System.currentTimeMillis()
        );
        
        results.put("HFCO", algorithmResult);
        
        // Calculate metrics
        PerformanceMetrics metrics = calculatePerformanceMetrics(algorithmResult);
        allMetrics.add(metrics);
        
        System.out.println("   ✅ Completed - Total Cost: " + String.format("%.2f", result.schedulingResult.totalCost));
        System.out.println("   📊 Fog Deployment Cost: " + String.format("%.2f", result.fogResult.deploymentCost));
    }
    
    /**
     * Run all baseline algorithms
     */
    private void runBaselineAlgorithms() {
        System.out.println("📈 Running Baseline Algorithms...");
        
        BaselineAlgorithms baselines = new BaselineAlgorithms(workflow, allNodes);
        
        // Run each baseline algorithm
        String[] baselineNames = {"GA", "PSO", "EPSO", "NSGA-II", "Min-Min", "FirstFit"};
        
        for (String name : baselineNames) {
            System.out.println("   🔄 Running " + name + "...");
            
            BaselineAlgorithms.SchedulingResult result = null;
            long startTime = System.currentTimeMillis();
            
            try {
                switch (name) {
                    case "GA":
                        result = baselines.geneticAlgorithm();
                        break;
                    case "PSO":
                        result = baselines.particleSwarmOptimization();
                        break;
                    case "EPSO":
                        result = baselines.enhancedParticleSwarmOptimization();
                        break;
                    case "NSGA-II":
                        result = baselines.nsgaII();
                        break;
                    case "Min-Min":
                        result = baselines.minMin();
                        break;
                    case "FirstFit":
                        result = baselines.firstFit();
                        break;
                }
                
                if (result != null) {
                    AlgorithmResult algorithmResult = new AlgorithmResult(
                        name,
                        result.taskAssignments,
                        result.startTimes,
                        result.totalCost,
                        System.currentTimeMillis() - startTime
                    );
                    
                    results.put(name, algorithmResult);
                    
                    // Calculate metrics
                    PerformanceMetrics metrics = calculatePerformanceMetrics(algorithmResult);
                    allMetrics.add(metrics);
                    
                    System.out.println("      ✅ " + name + " - Total Cost: " + String.format("%.2f", result.totalCost));
                }
                
            } catch (Exception e) {
                System.err.println("      ❌ Error running " + name + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Calculate comprehensive performance metrics
     */
    private PerformanceMetrics calculatePerformanceMetrics(AlgorithmResult result) {
        double deadlineHitRate = calculateDeadlineHitRate(result);
        double makespan = calculateMakespan(result);
        double avgLatency = calculateAverageLatency(result);
        double energyConsumption = calculateEnergyConsumption(result);
        double fogUtilization = calculateFogUtilization(result);
        double cloudUtilization = calculateCloudUtilization(result);
        
        return new PerformanceMetrics(
            result.algorithmName,
            result.totalCost,
            deadlineHitRate,
            makespan,
            avgLatency,
            energyConsumption,
            fogUtilization,
            cloudUtilization,
            result.executionTime
        );
    }
    
    /**
     * Calculate deadline hit rate
     */
    private double calculateDeadlineHitRate(AlgorithmResult result) {
        int totalTasks = workflow.getAllTasks().size();
        int deadlineHits = 0;
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            Double startTimeObj = result.startTimes.get(task.getId());
            
            if (nodeIdObj == null || startTimeObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            double startTime = startTimeObj;
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            double finishTime = startTime + execTime + transferDelay + latency;
            
            if (finishTime <= task.getDeadline()) {
                deadlineHits++;
            }
        }
        
        return (double) deadlineHits / totalTasks;
    }
    
    /**
     * Calculate makespan
     */
    private double calculateMakespan(AlgorithmResult result) {
        double maxFinishTime = 0.0;
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            Double startTimeObj = result.startTimes.get(task.getId());
            
            if (nodeIdObj == null || startTimeObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            double startTime = startTimeObj;
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            double finishTime = startTime + execTime + transferDelay + latency;
            
            maxFinishTime = Math.max(maxFinishTime, finishTime);
        }
        
        return maxFinishTime;
    }
    
    /**
     * Calculate average latency
     */
    private double calculateAverageLatency(AlgorithmResult result) {
        double totalLatency = 0.0;
        int count = 0;
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            if (nodeIdObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            totalLatency += node.getLatencyMs();
            count++;
        }
        
        return count > 0 ? totalLatency / count : 0.0;
    }
    
    /**
     * Calculate energy consumption
     */
    private double calculateEnergyConsumption(AlgorithmResult result) {
        double totalEnergy = 0.0;
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            if (nodeIdObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double energy = execTime * node.getEnergyPerSec();
            totalEnergy += energy;
        }
        
        return totalEnergy;
    }
    
    /**
     * Calculate fog utilization
     */
    private double calculateFogUtilization(AlgorithmResult result) {
        if (fogNodes.isEmpty()) return 0.0;
        
        Map<Integer, Double> nodeBusyTime = new HashMap<>();
        for (FogNode node : fogNodes) {
            nodeBusyTime.put(node.getId(), 0.0);
        }
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            Double startTimeObj = result.startTimes.get(task.getId());
            
            if (nodeIdObj == null || startTimeObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            if (node.isCloud()) continue;
            
            double startTime = startTimeObj;
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            
            double taskDuration = execTime + transferDelay + latency;
            nodeBusyTime.put(nodeId, nodeBusyTime.get(nodeId) + taskDuration);
        }
        
        double totalBusyTime = 0.0;
        for (Double busyTime : nodeBusyTime.values()) {
            totalBusyTime += busyTime;
        }
        
        double makespan = calculateMakespan(result);
        return totalBusyTime / (makespan * fogNodes.size());
    }
    
    /**
     * Calculate cloud utilization
     */
    private double calculateCloudUtilization(AlgorithmResult result) {
        if (cloudNodes.isEmpty()) return 0.0;
        
        Map<Integer, Double> nodeBusyTime = new HashMap<>();
        for (FogNode node : cloudNodes) {
            nodeBusyTime.put(node.getId(), 0.0);
        }
        
        for (core.IIoTTask task : workflow.getAllTasks()) {
            Integer nodeIdObj = result.taskAssignments.get(task.getId());
            Double startTimeObj = result.startTimes.get(task.getId());
            
            if (nodeIdObj == null || startTimeObj == null) {
                continue; // Skip task if assignment is missing
            }
            
            int nodeId = nodeIdObj;
            FogNode node = getNodeById(nodeId);
            if (!node.isCloud()) continue;
            
            double startTime = startTimeObj;
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            
            double taskDuration = execTime + transferDelay + latency;
            nodeBusyTime.put(nodeId, nodeBusyTime.get(nodeId) + taskDuration);
        }
        
        double totalBusyTime = 0.0;
        for (Double busyTime : nodeBusyTime.values()) {
            totalBusyTime += busyTime;
        }
        
        double makespan = calculateMakespan(result);
        return totalBusyTime / (makespan * cloudNodes.size());
    }
    
    /**
     * Generate performance analysis
     */
    private void generatePerformanceAnalysis() {
        System.out.println("\n📊 Performance Analysis");
        System.out.println("=======================");
        
        // Sort algorithms by total cost
        allMetrics.sort(Comparator.comparing(PerformanceMetrics::getTotalCost));
        
        System.out.println("🏆 Algorithm Rankings (by Total Cost):");
        for (int i = 0; i < allMetrics.size(); i++) {
            PerformanceMetrics metrics = allMetrics.get(i);
            System.out.printf("%d. %s: %.2f\n", i + 1, metrics.algorithmName, metrics.totalCost);
        }
        
        // Find best algorithm for each metric
        findBestAlgorithmForMetric("Deadline Hit Rate", Comparator.comparing(PerformanceMetrics::getDeadlineHitRate).reversed());
        findBestAlgorithmForMetric("Makespan", Comparator.comparing(PerformanceMetrics::getMakespan));
        findBestAlgorithmForMetric("Energy Consumption", Comparator.comparing(PerformanceMetrics::getEnergyConsumption));
        findBestAlgorithmForMetric("Fog Utilization", Comparator.comparing(PerformanceMetrics::getFogUtilization).reversed());
    }
    
    /**
     * Find best algorithm for a specific metric
     */
    private void findBestAlgorithmForMetric(String metricName, Comparator<PerformanceMetrics> comparator) {
        PerformanceMetrics best = allMetrics.stream().min(comparator).orElse(null);
        if (best != null) {
            System.out.println("🥇 Best " + metricName + ": " + best.algorithmName + 
                             " (" + String.format("%.4f", getMetricValue(best, metricName)) + ")");
        }
    }
    
    /**
     * Get metric value by name
     */
    private double getMetricValue(PerformanceMetrics metrics, String metricName) {
        switch (metricName) {
            case "Deadline Hit Rate": return metrics.deadlineHitRate;
            case "Makespan": return metrics.makespan;
            case "Energy Consumption": return metrics.energyConsumption;
            case "Fog Utilization": return metrics.fogUtilization;
            default: return 0.0;
        }
    }
    
    /**
     * Generate charts (console-based visualization)
     */
    private void generateCharts() {
        System.out.println("\n📈 Performance Charts");
        System.out.println("=====================");
        
        // Cost comparison chart
        generateCostChart();
        
        // Deadline hit rate chart
        generateDeadlineHitRateChart();
        
        // Makespan comparison chart
        generateMakespanChart();
    }
    
    /**
     * Generate cost comparison chart
     */
    private void generateCostChart() {
        System.out.println("\n💰 Total Cost Comparison:");
        System.out.println("Algorithm" + " ".repeat(15) + "Cost" + " ".repeat(10) + "Bar");
        
        double maxCost = allMetrics.stream().mapToDouble(PerformanceMetrics::getTotalCost).max().orElse(1.0);
        
        for (PerformanceMetrics metrics : allMetrics) {
            String name = metrics.algorithmName;
            double cost = metrics.totalCost;
            int barLength = (int) ((cost / maxCost) * 30);
            
            String bar = "█".repeat(barLength);
            System.out.printf("%-20s %8.2f %s\n", name, cost, bar);
        }
    }
    
    /**
     * Generate deadline hit rate chart
     */
    private void generateDeadlineHitRateChart() {
        System.out.println("\n🎯 Deadline Hit Rate Comparison:");
        System.out.println("Algorithm" + " ".repeat(15) + "Rate" + " ".repeat(10) + "Bar");
        
        for (PerformanceMetrics metrics : allMetrics) {
            String name = metrics.algorithmName;
            double rate = metrics.deadlineHitRate * 100;
            int barLength = (int) (rate / 100.0 * 30);
            
            String bar = "█".repeat(barLength);
            System.out.printf("%-20s %6.1f%% %s\n", name, rate, bar);
        }
    }
    
    /**
     * Generate makespan comparison chart
     */
    private void generateMakespanChart() {
        System.out.println("\n⏱️  Makespan Comparison:");
        System.out.println("Algorithm" + " ".repeat(15) + "Time" + " ".repeat(10) + "Bar");
        
        double maxMakespan = allMetrics.stream().mapToDouble(PerformanceMetrics::getMakespan).max().orElse(1.0);
        
        for (PerformanceMetrics metrics : allMetrics) {
            String name = metrics.algorithmName;
            double makespan = metrics.makespan;
            int barLength = (int) ((makespan / maxMakespan) * 30);
            
            String bar = "█".repeat(barLength);
            System.out.printf("%-20s %8.2f %s\n", name, makespan, bar);
        }
    }
    
    /**
     * Generate statistical analysis
     */
    private void generateStatisticalAnalysis() {
        System.out.println("\n📊 Statistical Analysis");
        System.out.println("=======================");
        
        // Calculate statistics for each metric
        calculateStatistics("Total Cost", allMetrics.stream().mapToDouble(PerformanceMetrics::getTotalCost));
        calculateStatistics("Deadline Hit Rate", allMetrics.stream().mapToDouble(PerformanceMetrics::getDeadlineHitRate));
        calculateStatistics("Makespan", allMetrics.stream().mapToDouble(PerformanceMetrics::getMakespan));
        calculateStatistics("Energy Consumption", allMetrics.stream().mapToDouble(PerformanceMetrics::getEnergyConsumption));
    }
    
    /**
     * Calculate and display statistics for a metric
     */
    private void calculateStatistics(String metricName, DoubleStream values) {
        double[] array = values.toArray();
        if (array.length == 0) return;
        
        Arrays.sort(array);
        
        double min = array[0];
        double max = array[array.length - 1];
        double mean = Arrays.stream(array).average().orElse(0.0);
        double median = array.length % 2 == 0 ? 
            (array[array.length/2 - 1] + array[array.length/2]) / 2.0 : 
            array[array.length/2];
        
        System.out.printf("%s:\n", metricName);
        System.out.printf("  Min: %.4f, Max: %.4f, Mean: %.4f, Median: %.4f\n", min, max, mean, median);
    }
    
    /**
     * Save results to file
     */
    private void saveResultsToFile() {
        try {
            String filename = "comparison_results_" + System.currentTimeMillis() + ".txt";
            PrintWriter writer = new PrintWriter(new FileWriter(filename));
            
            writer.println("Comprehensive Algorithm Comparison Results");
            writer.println("========================================");
            writer.println("Generated: " + new Date());
            writer.println("Workflow: " + workflow.getAllTasks().size() + " tasks");
            writer.println("Nodes: " + fogNodes.size() + " fog, " + cloudNodes.size() + " cloud");
            writer.println();
            
            // Write detailed results
            for (PerformanceMetrics metrics : allMetrics) {
                writer.println("Algorithm: " + metrics.algorithmName);
                writer.println("  Total Cost: " + String.format("%.4f", metrics.totalCost));
                writer.println("  Deadline Hit Rate: " + String.format("%.4f", metrics.deadlineHitRate));
                writer.println("  Makespan: " + String.format("%.4f", metrics.makespan));
                writer.println("  Energy Consumption: " + String.format("%.4f", metrics.energyConsumption));
                writer.println("  Fog Utilization: " + String.format("%.4f", metrics.fogUtilization));
                writer.println("  Cloud Utilization: " + String.format("%.4f", metrics.cloudUtilization));
                writer.println("  Execution Time: " + metrics.executionTime + " ms");
                writer.println();
            }
            
            writer.close();
            System.out.println("💾 Results saved to: " + filename);
            
        } catch (IOException e) {
            System.err.println("❌ Error saving results: " + e.getMessage());
        }
    }
    
    /**
     * Helper methods
     */
    private FogNode getNodeById(int nodeId) {
        for (FogNode node : allNodes) {
            if (node.getId() == nodeId) {
                return node;
            }
        }
        return allNodes.get(0);
    }
    
    /**
     * Result classes
     */
    public static class AlgorithmResult {
        public final String algorithmName;
        public final Map<Integer, Integer> taskAssignments;
        public final Map<Integer, Double> startTimes;
        public final double totalCost;
        public final long executionTime;
        
        public AlgorithmResult(String algorithmName, Map<Integer, Integer> taskAssignments,
                            Map<Integer, Double> startTimes, double totalCost, long executionTime) {
            this.algorithmName = algorithmName;
            this.taskAssignments = taskAssignments;
            this.startTimes = startTimes;
            this.totalCost = totalCost;
            this.executionTime = executionTime;
        }
    }
    
    public static class PerformanceMetrics {
        public final String algorithmName;
        public final double totalCost;
        public final double deadlineHitRate;
        public final double makespan;
        public final double avgLatency;
        public final double energyConsumption;
        public final double fogUtilization;
        public final double cloudUtilization;
        public final long executionTime;
        
        public PerformanceMetrics(String algorithmName, double totalCost, double deadlineHitRate,
                               double makespan, double avgLatency, double energyConsumption,
                               double fogUtilization, double cloudUtilization, long executionTime) {
            this.algorithmName = algorithmName;
            this.totalCost = totalCost;
            this.deadlineHitRate = deadlineHitRate;
            this.makespan = makespan;
            this.avgLatency = avgLatency;
            this.energyConsumption = energyConsumption;
            this.fogUtilization = fogUtilization;
            this.cloudUtilization = cloudUtilization;
            this.executionTime = executionTime;
        }
        
        // Getters for sorting
        public double getTotalCost() { return totalCost; }
        public double getDeadlineHitRate() { return deadlineHitRate; }
        public double getMakespan() { return makespan; }
        public double getEnergyConsumption() { return energyConsumption; }
        public double getFogUtilization() { return fogUtilization; }
    }
}
