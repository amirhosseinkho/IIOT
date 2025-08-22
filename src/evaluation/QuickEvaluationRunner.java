package evaluation;

import algorithms.*;
import core.*;
import simulation.*;
import java.util.*;
import java.io.*;

/**
 * Quick Evaluation Runner
 * 
 * نسخه سریع برای تست - 5 run برای هر الگوریتم
 */
public class QuickEvaluationRunner {
    
    private static final String RESULTS_DIR = "quick_results/";
    private static final int QUICK_RUNS = 5;
    private static final int[] WORKLOAD_SIZES = {50, 100}; // کمتر برای تست سریع
    
    private static final String[] ALGORITHMS = {
        "EPO-CEIS", "GA", "PSO", "Min-Min", "FirstFit"
    };
    
    public static void main(String[] args) {
        System.out.println("⚡ QUICK EVALUATION TEST");
        System.out.println("======================");
        System.out.println("Running 5 experiments for core algorithms");
        System.out.println("Workloads: 50, 100 tasks");
        System.out.println();
        
        try {
            // Create results directory
            new File(RESULTS_DIR).mkdirs();
            
            // Initialize CSV writers
            PrintWriter totalCostWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "total_cost.csv"));
            PrintWriter makespanWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "makespan.csv"));
            PrintWriter deadlineHitRateWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "deadline_hit_rate.csv"));
            
            // Write headers
            String header = "workload,algorithm,run,value";
            totalCostWriter.println(header);
            makespanWriter.println(header);
            deadlineHitRateWriter.println(header);
            
            int totalExperiments = WORKLOAD_SIZES.length * ALGORITHMS.length * QUICK_RUNS;
            int currentExperiment = 0;
            
            System.out.println("🚀 Starting " + totalExperiments + " quick experiments...\n");
            
            for (int workloadSize : WORKLOAD_SIZES) {
                System.out.printf("📊 WORKLOAD: %d tasks\n", workloadSize);
                System.out.println("=".repeat(30));
                
                for (String algorithm : ALGORITHMS) {
                    System.out.printf("\n🔬 Testing %s...\n", algorithm);
                    
                    for (int run = 1; run <= QUICK_RUNS; run++) {
                        currentExperiment++;
                        System.out.printf("  Run %d/%d... ", run, QUICK_RUNS);
                        
                        try {
                            EvaluationResult result = runSingleExperiment(workloadSize, algorithm, run);
                            
                            // Save results
                            totalCostWriter.printf("%d,%s,%d,%.4f\n", 
                                workloadSize, algorithm, run, result.totalCost);
                            makespanWriter.printf("%d,%s,%d,%.4f\n", 
                                workloadSize, algorithm, run, result.makespan);
                            deadlineHitRateWriter.printf("%d,%s,%d,%.4f\n", 
                                workloadSize, algorithm, run, result.deadlineHitRate);
                            
                            System.out.printf("✅ Cost=%.2f, Makespan=%.2f, HitRate=%.1f%%\n", 
                                result.totalCost, result.makespan, result.deadlineHitRate * 100);
                            
                        } catch (Exception e) {
                            System.out.printf("❌ Failed: %s\n", e.getMessage());
                            
                            // Write NaN for failed runs
                            totalCostWriter.printf("%d,%s,%d,NaN\n", workloadSize, algorithm, run);
                            makespanWriter.printf("%d,%s,%d,NaN\n", workloadSize, algorithm, run);
                            deadlineHitRateWriter.printf("%d,%s,%d,NaN\n", workloadSize, algorithm, run);
                        }
                    }
                    
                    System.out.printf("✅ %s completed\n", algorithm);
                }
                
                System.out.printf("\n✅ Workload %d completed\n\n", workloadSize);
            }
            
            // Close writers
            totalCostWriter.close();
            makespanWriter.close();
            deadlineHitRateWriter.close();
            
            System.out.println("🎉 QUICK EVALUATION COMPLETED!");
            System.out.println("===============================");
            System.out.println("📊 Results saved to quick_results/");
            
            // Generate summary
            generateQuickSummary();
            
        } catch (Exception e) {
            System.err.println("❌ Quick evaluation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static EvaluationResult runSingleExperiment(int workloadSize, String algorithm, int run) 
            throws Exception {
        
        // Generate reproducible scenario
        long seed = workloadSize * 1000L + run * 100L + algorithm.hashCode();
        Random rand = new Random(seed);
        
        // Generate workflow and nodes
        Workflow workflow = generateWorkflow(workloadSize, rand);
        List<FogNode> nodes = generateNodes(workloadSize, rand);
        
        // Run algorithm
        EvaluationResult result;
        switch (algorithm) {
            case "EPO-CEIS":
                result = runEPOCEIS(workflow, nodes);
                break;
            case "GA":
                result = runGA(workflow, nodes);
                break;
            case "PSO":
                result = runPSO(workflow, nodes);
                break;
            case "Min-Min":
                result = runMinMin(workflow, nodes);
                break;
            case "FirstFit":
                result = runFirstFit(workflow, nodes);
                break;
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
        
        return result;
    }
    
    private static Workflow generateWorkflow(int taskCount, Random rand) {
        Workflow workflow = new Workflow();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 1000 + rand.nextInt(3000);
            long fileSize = 10 + rand.nextInt(50);
            long outputSize = 5 + rand.nextInt(25);
            int pes = 1;
            
            double minExecutionTime = (double) length / 8000; // Assuming ~8000 MIPS average
            double deadline = minExecutionTime * (1.5 + rand.nextDouble() * 2.5); // 1.5x-4x min time
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // Add dependencies (25% probability)
            if (i > 1 && rand.nextDouble() < 0.25) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateNodes(int taskCount, Random rand) {
        int nodeCount = Math.max(8, taskCount / 8); // At least 8 nodes
        List<FogNode> nodes = new ArrayList<>();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.7; // 30% cloud nodes
            
            int mips, ram;
            long bw, storage;
            double costPerSec, latency;
            
            if (isCloud) {
                mips = 4000 + rand.nextInt(6000); // 4K-10K MIPS
                ram = 8192;
                bw = 1000 + rand.nextInt(2000);
                storage = 100000;
                costPerSec = 0.1 + rand.nextDouble() * 0.3;
                latency = 50 + rand.nextDouble() * 80;
            } else {
                mips = 800 + rand.nextInt(1700); // 800-2500 MIPS
                ram = 1024;
                bw = 100 + rand.nextInt(300);
                storage = 10000;
                costPerSec = 0.02 + rand.nextDouble() * 0.04;
                latency = 5 + rand.nextDouble() * 15;
            }
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    // Algorithm implementations (simplified versions)
    
    private static EvaluationResult runEPOCEIS(Workflow workflow, List<FogNode> nodes) throws Exception {
        EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
        EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespan(workflow, result, nodes),
            calculateDeadlineHitRate(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runGA(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.geneticAlgorithm();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespanBaseline(workflow, result, nodes),
            calculateDeadlineHitRateBaseline(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runPSO(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.particleSwarmOptimization();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespanBaseline(workflow, result, nodes),
            calculateDeadlineHitRateBaseline(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runMinMin(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.minMin();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespanBaseline(workflow, result, nodes),
            calculateDeadlineHitRateBaseline(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runFirstFit(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.firstFit();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespanBaseline(workflow, result, nodes),
            calculateDeadlineHitRateBaseline(workflow, result, nodes)
        );
    }
    
    // Metric calculation methods (same as ComprehensiveEvaluationRunner)
    
    private static double calculateMakespan(Workflow workflow, EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        double maxFinishTime = 0.0;
        
        Map<Integer, FogNode> nodeMap = createNodeMap(nodes);
        
        for (IIoTTask task : workflow.getAllTasks()) {
            Double startTime = result.startTimes.get(task.getId());
            Integer nodeId = result.taskAssignments.get(task.getId());
            
            if (startTime != null && nodeId != null) {
                FogNode node = nodeMap.get(nodeId);
                if (node != null) {
                    double executionTime = (double) task.getLength() / node.getMips();
                    double finishTime = startTime + executionTime;
                    maxFinishTime = Math.max(maxFinishTime, finishTime);
                }
            }
        }
        
        return maxFinishTime;
    }
    
    private static double calculateDeadlineHitRate(Workflow workflow, EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        int totalTasks = 0;
        int metDeadlines = 0;
        
        Map<Integer, FogNode> nodeMap = createNodeMap(nodes);
        
        for (IIoTTask task : workflow.getAllTasks()) {
            totalTasks++;
            
            Double startTime = result.startTimes.get(task.getId());
            Integer nodeId = result.taskAssignments.get(task.getId());
            
            if (startTime != null && nodeId != null) {
                FogNode node = nodeMap.get(nodeId);
                if (node != null) {
                    double executionTime = (double) task.getLength() / node.getMips();
                    double finishTime = startTime + executionTime;
                    
                    if (finishTime <= task.getDeadline()) {
                        metDeadlines++;
                    }
                }
            }
        }
        
        return totalTasks > 0 ? (double) metDeadlines / totalTasks : 0.0;
    }
    
    private static double calculateMakespanBaseline(Workflow workflow, BaselineAlgorithms.SchedulingResult result, List<FogNode> nodes) {
        double maxFinishTime = 0.0;
        
        Map<Integer, FogNode> nodeMap = createNodeMap(nodes);
        
        for (IIoTTask task : workflow.getAllTasks()) {
            Double startTime = result.startTimes.get(task.getId());
            Integer nodeId = result.taskAssignments.get(task.getId());
            
            if (startTime != null && nodeId != null) {
                FogNode node = nodeMap.get(nodeId);
                if (node != null) {
                    double executionTime = (double) task.getLength() / node.getMips();
                    double finishTime = startTime + executionTime;
                    maxFinishTime = Math.max(maxFinishTime, finishTime);
                }
            }
        }
        
        return maxFinishTime;
    }
    
    private static double calculateDeadlineHitRateBaseline(Workflow workflow, BaselineAlgorithms.SchedulingResult result, List<FogNode> nodes) {
        int totalTasks = 0;
        int metDeadlines = 0;
        
        Map<Integer, FogNode> nodeMap = createNodeMap(nodes);
        
        for (IIoTTask task : workflow.getAllTasks()) {
            totalTasks++;
            
            Double startTime = result.startTimes.get(task.getId());
            Integer nodeId = result.taskAssignments.get(task.getId());
            
            if (startTime != null && nodeId != null) {
                FogNode node = nodeMap.get(nodeId);
                if (node != null) {
                    double executionTime = (double) task.getLength() / node.getMips();
                    double finishTime = startTime + executionTime;
                    
                    if (finishTime <= task.getDeadline()) {
                        metDeadlines++;
                    }
                }
            }
        }
        
        return totalTasks > 0 ? (double) metDeadlines / totalTasks : 0.0;
    }
    
    private static Map<Integer, FogNode> createNodeMap(List<FogNode> nodes) {
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        return nodeMap;
    }
    
    private static void generateQuickSummary() throws IOException {
        System.out.println("\n📊 GENERATING QUICK SUMMARY");
        System.out.println("===========================");
        
        // Read and summarize results
        try (Scanner costScanner = new Scanner(new File(RESULTS_DIR + "total_cost.csv"))) {
            costScanner.nextLine(); // Skip header
            
            Map<String, List<Double>> costData = new HashMap<>();
            
            while (costScanner.hasNextLine()) {
                String[] parts = costScanner.nextLine().split(",");
                if (parts.length >= 4 && !parts[3].equals("NaN")) {
                    String algorithm = parts[1];
                    double cost = Double.parseDouble(parts[3]);
                    
                    costData.computeIfAbsent(algorithm, k -> new ArrayList<>()).add(cost);
                }
            }
            
            System.out.println("📈 Average Total Cost by Algorithm:");
            costData.entrySet().stream()
                .sorted(Map.Entry.<String, List<Double>>comparingByValue(
                    (list1, list2) -> Double.compare(
                        list1.stream().mapToDouble(Double::doubleValue).average().orElse(0),
                        list2.stream().mapToDouble(Double::doubleValue).average().orElse(0)
                    )
                ))
                .forEach(entry -> {
                    double avgCost = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    System.out.printf("  %-10s: $%.2f\n", entry.getKey(), avgCost);
                });
            
        } catch (Exception e) {
            System.err.println("Error generating summary: " + e.getMessage());
        }
    }
    
    // Data class
    private static class EvaluationResult {
        double totalCost;
        double makespan;
        double deadlineHitRate;
        
        EvaluationResult(double totalCost, double makespan, double deadlineHitRate) {
            this.totalCost = totalCost;
            this.makespan = makespan;
            this.deadlineHitRate = deadlineHitRate;
        }
    }
}


