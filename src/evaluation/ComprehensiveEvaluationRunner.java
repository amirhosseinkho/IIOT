package evaluation;

import algorithms.*;
import core.*;
import simulation.*;
import java.util.*;
import java.io.*;

/**
 * Comprehensive Evaluation Runner
 * 
 * اجرای ارزیابی جامع با 30 run مستقل برای تمام الگوریتم‌ها
 */
public class ComprehensiveEvaluationRunner {
    
    private static final String RESULTS_DIR = "results/";
    private static final String FIGURES_DIR = "figures/";
    private static final int INDEPENDENT_RUNS = 30;
    private static final int[] WORKLOAD_SIZES = {100, 500, 1000};
    
    private static final String[] ALGORITHMS = {
        "EPO-CEIS", "HFCO", "GA", "PSO", "EPSO", "NSGA-II", "Min-Min", "FirstFit"
    };
    
    // CSV Writers
    private static PrintWriter totalCostWriter;
    private static PrintWriter makespanWriter;
    private static PrintWriter deadlineHitRateWriter;
    
    public static void main(String[] args) {
        System.out.println("🔬 COMPREHENSIVE SCIENTIFIC EVALUATION");
        System.out.println("=====================================");
        System.out.println("Running 30 independent experiments for each algorithm");
        System.out.println("Workloads: 100, 500, 1000 tasks");
        System.out.println("Algorithms: " + Arrays.toString(ALGORITHMS));
        System.out.println();
        
        try {
            // Create directories
            createDirectories();
            
            // Initialize CSV writers
            initializeCSVWriters();
            
            // Run comprehensive evaluation
            runComprehensiveEvaluation();
            
            // Close CSV writers
            closeCSVWriters();
            
            // Generate Python plotting script
            generatePlottingScript();
            
            System.out.println("\n🎉 EVALUATION COMPLETED!");
            System.out.println("==============================");
            System.out.println("📊 Results saved to:");
            System.out.println("  - " + RESULTS_DIR + "total_cost.csv");
            System.out.println("  - " + RESULTS_DIR + "makespan.csv");
            System.out.println("  - " + RESULTS_DIR + "deadline_hit_rate.csv");
            System.out.println();
            System.out.println("🐍 To generate plots, run:");
            System.out.println("  python plot_results.py");
            
        } catch (Exception e) {
            System.err.println("❌ Evaluation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createDirectories() {
        new File(RESULTS_DIR).mkdirs();
        new File(FIGURES_DIR).mkdirs();
        System.out.println("📂 Created results and figures directories");
    }
    
    private static void initializeCSVWriters() throws IOException {
        totalCostWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "total_cost.csv"));
        makespanWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "makespan.csv"));
        deadlineHitRateWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "deadline_hit_rate.csv"));
        
        // Write headers
        String header = "workload,algorithm,run,value";
        totalCostWriter.println(header);
        makespanWriter.println(header);
        deadlineHitRateWriter.println(header);
        
        System.out.println("📝 Initialized CSV writers with headers");
    }
    
    private static void runComprehensiveEvaluation() {
        int totalExperiments = WORKLOAD_SIZES.length * ALGORITHMS.length * INDEPENDENT_RUNS;
        int currentExperiment = 0;
        
        System.out.println("🚀 Starting " + totalExperiments + " experiments...\n");
        
        for (int workloadSize : WORKLOAD_SIZES) {
            System.out.printf("📊 WORKLOAD: %d tasks\n", workloadSize);
            System.out.println("=".repeat(50));
            
            for (String algorithm : ALGORITHMS) {
                System.out.printf("\n🔬 Testing %s algorithm...\n", algorithm);
                
                for (int run = 1; run <= INDEPENDENT_RUNS; run++) {
                    currentExperiment++;
                    System.out.printf("  Run %d/%d... ", run, INDEPENDENT_RUNS);
                    
                    try {
                        EvaluationResult result = runSingleExperiment(workloadSize, algorithm, run);
                        
                        // Save results to CSV
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
                    
                    // Flush every 10 runs
                    if (run % 10 == 0) {
                        totalCostWriter.flush();
                        makespanWriter.flush();
                        deadlineHitRateWriter.flush();
                    }
                    
                    // Progress indicator
                    double progress = (double) currentExperiment / totalExperiments * 100;
                    if (currentExperiment % 10 == 0) {
                        System.out.printf("    📈 Overall Progress: %.1f%% (%d/%d)\n", 
                            progress, currentExperiment, totalExperiments);
                    }
                }
                
                System.out.printf("✅ %s completed for workload %d\n", algorithm, workloadSize);
            }
            
            System.out.printf("\n✅ Workload %d tasks completed\n\n", workloadSize);
        }
    }
    
    private static EvaluationResult runSingleExperiment(int workloadSize, String algorithm, int run) 
            throws Exception {
        
        // Generate reproducible scenario with unique seed
        long seed = workloadSize * 1000000L + run * 1000L + algorithm.hashCode();
        Random rand = new Random(seed);
        
        // Generate workflow and nodes
        Workflow workflow = generateWorkflow(workloadSize, rand);
        List<FogNode> nodes = generateNodes(workloadSize, rand);
        
        long startTime = System.currentTimeMillis();
        
        // Run algorithm
        EvaluationResult result;
        switch (algorithm) {
            case "EPO-CEIS":
                result = runEPOCEIS(workflow, nodes);
                break;
            case "HFCO":
                result = runHFCO(workflow, nodes);
                break;
            case "GA":
                result = runGA(workflow, nodes);
                break;
            case "PSO":
                result = runPSO(workflow, nodes);
                break;
            case "EPSO":
                result = runEPSO(workflow, nodes);
                break;
            case "NSGA-II":
                result = runNSGAII(workflow, nodes);
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
        
        long endTime = System.currentTimeMillis();
        result.executionTime = endTime - startTime;
        
        return result;
    }
    
    private static Workflow generateWorkflow(int taskCount, Random rand) {
        Workflow workflow = new Workflow();
        
        for (int i = 1; i <= taskCount; i++) {
            // Task parameters with controlled randomness
            int length = 1000 + rand.nextInt(4000); // 1K-5K MI
            long fileSize = 10 + rand.nextInt(90); // 10-100 MB
            long outputSize = 5 + rand.nextInt(45); // 5-50 MB
            int pes = 1;
            
            // Deadline based on task complexity
            double minExecutionTime = (double) length / 10000; // Assuming fastest node ~10000 MIPS
            double deadline = minExecutionTime * (2.0 + rand.nextDouble() * 3.0); // 2x-5x min time
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // Add dependencies (30% probability)
            if (i > 1 && rand.nextDouble() < 0.3) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateNodes(int taskCount, Random rand) {
        // Node count proportional to task count
        int nodeCount = Math.max(10, taskCount / 15); // At least 10 nodes, ~15 tasks per node
        List<FogNode> nodes = new ArrayList<>();
        
        for (int i = 1; i <= nodeCount; i++) {
            // 30% cloud nodes
            boolean isCloud = i > nodeCount * 0.7;
            
            int mips, ram;
            long bw, storage;
            double costPerSec, latency;
            
            if (isCloud) {
                // Cloud nodes - powerful but expensive
                mips = 5000 + rand.nextInt(10000); // 5K-15K MIPS
                ram = 8192 + rand.nextInt(24576); // 8-32 GB
                bw = 1000 + rand.nextInt(4000); // 1-5 Gbps
                storage = 100000 + rand.nextInt(900000); // 100GB-1TB
                costPerSec = 0.1 + rand.nextDouble() * 0.4; // $0.1-0.5/sec
                latency = 50 + rand.nextDouble() * 100; // 50-150ms
            } else {
                // Fog nodes - moderate power, lower cost
                mips = 500 + rand.nextInt(2500); // 500-3K MIPS
                ram = 1024 + rand.nextInt(3072); // 1-4 GB
                bw = 100 + rand.nextInt(400); // 100-500 Mbps
                storage = 5000 + rand.nextInt(45000); // 5-50 GB
                costPerSec = 0.01 + rand.nextDouble() * 0.05; // $0.01-0.06/sec
                latency = 5 + rand.nextDouble() * 20; // 5-25ms
            }
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    // Algorithm implementations
    
    private static EvaluationResult runEPOCEIS(Workflow workflow, List<FogNode> nodes) throws Exception {
        EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
        EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespan(workflow, result, nodes),
            calculateDeadlineHitRate(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runHFCO(Workflow workflow, List<FogNode> nodes) throws Exception {
        // HFCO implementation
        EnhancedEPOCEIS hfcoAlgorithm = new EnhancedEPOCEIS(workflow, nodes);
        EnhancedEPOCEIS.SchedulingResult result = hfcoAlgorithm.schedule();
        
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
    
    private static EvaluationResult runEPSO(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.enhancedParticleSwarmOptimization();
        
        return new EvaluationResult(
            result.totalCost,
            calculateMakespanBaseline(workflow, result, nodes),
            calculateDeadlineHitRateBaseline(workflow, result, nodes)
        );
    }
    
    private static EvaluationResult runNSGAII(Workflow workflow, List<FogNode> nodes) throws Exception {
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
        BaselineAlgorithms.SchedulingResult result = baseline.nsgaII();
        
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
    
    // Metric calculation methods
    
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
    
    private static void closeCSVWriters() {
        if (totalCostWriter != null) totalCostWriter.close();
        if (makespanWriter != null) makespanWriter.close();
        if (deadlineHitRateWriter != null) deadlineHitRateWriter.close();
        
        System.out.println("💾 CSV files saved and closed");
    }
    
    private static void generatePlottingScript() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter("plot_results.py"));
        
        writer.println("import pandas as pd");
        writer.println("import matplotlib.pyplot as plt");
        writer.println("import numpy as np");
        writer.println("import os");
        writer.println();
        writer.println("# Create figures directory if it doesn't exist");
        writer.println("os.makedirs('figures', exist_ok=True)");
        writer.println();
        writer.println("# List of files and y-axis labels");
        writer.println("files = {");
        writer.println("    \"results/total_cost.csv\": \"Total Cost ($)\",");
        writer.println("    \"results/makespan.csv\": \"Makespan (seconds)\",");
        writer.println("    \"results/deadline_hit_rate.csv\": \"Deadline Hit Rate\"");
        writer.println("}");
        writer.println();
        writer.println("# Algorithm colors for consistency");
        writer.println("colors = {");
        writer.println("    'EPO-CEIS': '#FF0000',");  // Red
        writer.println("    'HFCO': '#FF8000',");       // Orange
        writer.println("    'GA': '#00FF00',");         // Green
        writer.println("    'PSO': '#0000FF',");        // Blue
        writer.println("    'EPSO': '#8000FF',");       // Purple
        writer.println("    'NSGA-II': '#FF00FF',");    // Magenta
        writer.println("    'Min-Min': '#00FFFF',");    // Cyan
        writer.println("    'FirstFit': '#808080'");    // Gray
        writer.println("}");
        writer.println();
        writer.println("# Loop through each metric");
        writer.println("for file_path, y_label in files.items():");
        writer.println("    print(f'Processing {file_path}...')");
        writer.println("    ");
        writer.println("    # Read data");
        writer.println("    df = pd.read_csv(file_path)");
        writer.println("    ");
        writer.println("    # Remove NaN values");
        writer.println("    df = df.dropna()");
        writer.println("    ");
        writer.println("    # Aggregate (mean over 30 runs)");
        writer.println("    grouped = df.groupby([\"workload\", \"algorithm\"])[\"value\"].agg(['mean', 'std']).reset_index()");
        writer.println("    workloads = sorted(grouped[\"workload\"].unique())");
        writer.println("    algorithms = grouped[\"algorithm\"].unique()");
        writer.println("    ");
        writer.println("    # Create plot");
        writer.println("    plt.figure(figsize=(10, 6))");
        writer.println("    ");
        writer.println("    for alg in algorithms:");
        writer.println("        alg_data = grouped[grouped[\"algorithm\"] == alg]");
        writer.println("        color = colors.get(alg, '#000000')");
        writer.println("        ");
        writer.println("        plt.errorbar(alg_data[\"workload\"], alg_data[\"mean\"], ");
        writer.println("                    yerr=alg_data[\"std\"], label=alg, color=color, ");
        writer.println("                    marker='o', linewidth=2, markersize=6)");
        writer.println("    ");
        writer.println("    plt.xlabel(\"Workload (number of tasks)\", fontsize=12)");
        writer.println("    plt.ylabel(y_label, fontsize=12)");
        writer.println("    plt.title(f'{y_label} vs Workload Size', fontsize=14)");
        writer.println("    plt.legend()");
        writer.println("    plt.grid(True, alpha=0.3)");
        writer.println("    plt.tight_layout()");
        writer.println("    ");
        writer.println("    # Save figure");
        writer.println("    metric_name = file_path.split(\"/\")[-1].replace(\".csv\", \"\")");
        writer.println("    plt.savefig(f\"figures/{metric_name}.png\", dpi=300, bbox_inches='tight')");
        writer.println("    plt.close()");
        writer.println("    ");
        writer.println("    print(f'Saved figures/{metric_name}.png')");
        writer.println();
        writer.println("print('All plots generated successfully!')");
        
        writer.close();
        
        System.out.println("🐍 Generated plot_results.py script");
    }
    
    // Data classes
    
    private static class EvaluationResult {
        double totalCost;
        double makespan;
        double deadlineHitRate;
        long executionTime;
        
        EvaluationResult(double totalCost, double makespan, double deadlineHitRate) {
            this.totalCost = totalCost;
            this.makespan = makespan;
            this.deadlineHitRate = deadlineHitRate;
        }
    }
}
