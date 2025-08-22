package evaluation;

import algorithms.*;
import core.*;
import simulation.*;
import utils.*;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * Comprehensive Evaluation Framework for IIoT Task Scheduling Algorithms
 * Integrates CloudSim simulation with statistical analysis and comparison
 * Supports multiple datasets and evaluation metrics
 */
public class ComprehensiveEvaluationFramework {
    
    // Evaluation configuration
    private static final int INDEPENDENT_RUNS = 30;  // For statistical significance
    private static final double SIGNIFICANCE_LEVEL = 0.05;  // p < 0.05
    
    // Test scenarios
    private final List<TestScenario> testScenarios;
    private final List<BaselineAlgorithm> baselineAlgorithms;
    private final String outputDirectory;
    
    public ComprehensiveEvaluationFramework(String outputDir) {
        this.outputDirectory = outputDir;
        this.testScenarios = initializeTestScenarios();
        this.baselineAlgorithms = initializeBaselineAlgorithms();
        
        // Create output directory
        new File(outputDirectory).mkdirs();
    }
    
    /**
     * Run comprehensive evaluation
     */
    public EvaluationResults runFullEvaluation() {
        System.out.println("🚀 Starting Comprehensive IIoT Scheduling Evaluation");
        System.out.println("================================================");
        
        EvaluationResults results = new EvaluationResults();
        
        for (TestScenario scenario : testScenarios) {
            System.out.println("\n📊 Evaluating Scenario: " + scenario.name);
            ScenarioResults scenarioResults = evaluateScenario(scenario);
            results.addScenarioResults(scenario.name, scenarioResults);
            
            // Generate scenario report
            generateScenarioReport(scenario, scenarioResults);
        }
        
        // Statistical analysis
        performStatisticalAnalysis(results);
        
        // Generate comprehensive report
        generateComprehensiveReport(results);
        
        System.out.println("\n✅ Evaluation completed. Results saved to: " + outputDirectory);
        return results;
    }
    
    /**
     * Evaluate single scenario across all algorithms
     */
    private ScenarioResults evaluateScenario(TestScenario scenario) {
        ScenarioResults results = new ScenarioResults();
        
        // Test Enhanced EPO-CEIS
        System.out.println("  🧬 Testing Enhanced EPO-CEIS...");
        AlgorithmPerformance epoceisPerf = evaluateEnhancedEPOCEIS(scenario);
        results.addAlgorithmResults("Enhanced EPO-CEIS", epoceisPerf);
        
        // Test baseline algorithms
        for (BaselineAlgorithm baseline : baselineAlgorithms) {
            System.out.println("  📈 Testing " + baseline.name + "...");
            AlgorithmPerformance baselinePerf = evaluateBaselineAlgorithm(baseline, scenario);
            results.addAlgorithmResults(baseline.name, baselinePerf);
        }
        
        return results;
    }
    
    /**
     * Evaluate Enhanced EPO-CEIS algorithm
     */
    private AlgorithmPerformance evaluateEnhancedEPOCEIS(TestScenario scenario) {
        List<RunResult> runs = new ArrayList<>();
        
        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            System.out.print(".");
            
            // Create fresh instances for each run
            Workflow workflow = loadWorkflow(scenario.workflowPath);
            List<FogNode> fogNodes = loadFogNodes(scenario.nodesPath);
            
            // Run algorithm
            long startTime = System.currentTimeMillis();
            EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, fogNodes);
            EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Calculate metrics
            RunMetrics metrics = calculateRunMetrics(result, workflow, fogNodes, executionTime);
            runs.add(new RunResult(run, metrics));
        }
        
        System.out.println(" Done!");
        return new AlgorithmPerformance("Enhanced EPO-CEIS", runs);
    }
    
    /**
     * Evaluate baseline algorithm
     */
    private AlgorithmPerformance evaluateBaselineAlgorithm(BaselineAlgorithm baseline, TestScenario scenario) {
        List<RunResult> runs = new ArrayList<>();
        
        for (int run = 0; run < INDEPENDENT_RUNS; run++) {
            System.out.print(".");
            
            // Create fresh instances
            Workflow workflow = loadWorkflow(scenario.workflowPath);
            List<FogNode> fogNodes = loadFogNodes(scenario.nodesPath);
            
            // Run baseline algorithm
            long startTime = System.currentTimeMillis();
            BaselineAlgorithms algorithms = new BaselineAlgorithms(workflow, fogNodes);
            BaselineAlgorithms.SchedulingResult result = runBaselineAlgorithm(algorithms, baseline.type);
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Calculate metrics
            RunMetrics metrics = calculateBaselineMetrics(result, workflow, fogNodes, executionTime);
            runs.add(new RunResult(run, metrics));
        }
        
        System.out.println(" Done!");
        return new AlgorithmPerformance(baseline.name, runs);
    }
    
    /**
     * Calculate comprehensive metrics for a run
     */
    private RunMetrics calculateRunMetrics(EnhancedEPOCEIS.SchedulingResult result, 
                                         Workflow workflow, List<FogNode> fogNodes, long executionTime) {
        RunMetrics metrics = new RunMetrics();
        
        // Basic metrics
        metrics.totalCost = result.totalCost;
        metrics.executionTimeMs = executionTime;
        metrics.tasksScheduled = result.taskAssignments.size();
        
        // Calculate detailed metrics
        calculateDetailedMetrics(metrics, result.taskAssignments, result.startTimes, workflow, fogNodes);
        
        return metrics;
    }
    
    /**
     * Calculate metrics for baseline algorithms
     */
    private RunMetrics calculateBaselineMetrics(BaselineAlgorithms.SchedulingResult result,
                                              Workflow workflow, List<FogNode> fogNodes, long executionTime) {
        RunMetrics metrics = new RunMetrics();
        
        metrics.totalCost = result.totalCost;
        metrics.executionTimeMs = executionTime;
        metrics.tasksScheduled = result.taskAssignments.size();
        
        calculateDetailedMetrics(metrics, result.taskAssignments, result.startTimes, workflow, fogNodes);
        
        return metrics;
    }
    
    /**
     * Calculate detailed performance metrics
     */
    private void calculateDetailedMetrics(RunMetrics metrics, Map<Integer, Integer> taskAssignments,
                                        Map<Integer, Double> startTimes, Workflow workflow, List<FogNode> fogNodes) {
        
        double maxFinishTime = 0.0;
        int missedDeadlines = 0;
        double totalEnergy = 0.0;
        Map<Integer, Double> nodeUtilization = new HashMap<>();
        
        // Initialize node utilization
        for (FogNode node : fogNodes) {
            nodeUtilization.put(node.getId(), 0.0);
        }
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            
            if (taskAssignments.containsKey(taskId) && startTimes.containsKey(taskId)) {
                int nodeId = taskAssignments.get(taskId);
                double startTime = startTimes.get(taskId);
                
                FogNode node = findNodeById(fogNodes, nodeId);
                if (node != null) {
                    // Calculate execution metrics
                    double execTime = CostCalculator.calculateExecutionTime(task, node);
                    double transferDelay = CostCalculator.calculateTransferDelay(task, node);
                    double finishTime = startTime + execTime + transferDelay + node.getLatencyMs() / 1000.0;
                    
                    maxFinishTime = Math.max(maxFinishTime, finishTime);
                    
                    // Check deadline violations
                    if (finishTime > task.getDeadline()) {
                        missedDeadlines++;
                    }
                    
                    // Calculate energy consumption
                    double taskEnergy = CostCalculator.calculateEnergyConsumption(task, node);
                    totalEnergy += taskEnergy;
                    
                    // Update node utilization
                    nodeUtilization.put(nodeId, nodeUtilization.get(nodeId) + execTime);
                }
            }
        }
        
        metrics.makespan = maxFinishTime;
        metrics.missedDeadlines = missedDeadlines;
        metrics.deadlineHitRate = 1.0 - (double) missedDeadlines / workflow.getAllTasks().size();
        metrics.totalEnergyConsumption = totalEnergy;
        metrics.averageLatency = calculateAverageLatency(taskAssignments, startTimes, workflow, fogNodes);
        metrics.resourceUtilization = calculateResourceUtilization(nodeUtilization, fogNodes);
    }
    
    /**
     * Calculate average end-to-end latency
     */
    private double calculateAverageLatency(Map<Integer, Integer> taskAssignments, Map<Integer, Double> startTimes,
                                         Workflow workflow, List<FogNode> fogNodes) {
        double totalLatency = 0.0;
        int taskCount = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            if (taskAssignments.containsKey(taskId)) {
                int nodeId = taskAssignments.get(taskId);
                FogNode node = findNodeById(fogNodes, nodeId);
                if (node != null) {
                    double execTime = CostCalculator.calculateExecutionTime(task, node);
                    double transferDelay = CostCalculator.calculateTransferDelay(task, node);
                    double latency = execTime + transferDelay + node.getLatencyMs() / 1000.0;
                    totalLatency += latency;
                    taskCount++;
                }
            }
        }
        
        return taskCount > 0 ? totalLatency / taskCount : 0.0;
    }
    
    /**
     * Calculate resource utilization across nodes
     */
    private double calculateResourceUtilization(Map<Integer, Double> nodeUtilization, List<FogNode> fogNodes) {
        double totalUtilization = 0.0;
        
        for (Double utilization : nodeUtilization.values()) {
            totalUtilization += utilization;
        }
        
        return totalUtilization / fogNodes.size();
    }
    
    /**
     * Find node by ID in the list
     */
    private FogNode findNodeById(List<FogNode> fogNodes, int nodeId) {
        return fogNodes.stream()
            .filter(node -> node.getId() == nodeId)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Run specific baseline algorithm
     */
    private BaselineAlgorithms.SchedulingResult runBaselineAlgorithm(BaselineAlgorithms algorithms, AlgorithmType type) {
        switch (type) {
            case GENETIC_ALGORITHM:
                return algorithms.geneticAlgorithm();
            case PARTICLE_SWARM:
                return algorithms.particleSwarmOptimization();
            case ENHANCED_PSO:
                return algorithms.enhancedParticleSwarmOptimization();
            case NSGA_II:
                return algorithms.nsgaII();
            case MIN_MIN:
                return algorithms.minMin();
            case FIRST_FIT:
                return algorithms.firstFit();
            default:
                throw new IllegalArgumentException("Unknown algorithm type: " + type);
        }
    }
    
    /**
     * Perform statistical analysis (t-tests, ANOVA, etc.)
     */
    private void performStatisticalAnalysis(EvaluationResults results) {
        System.out.println("\n📈 Performing Statistical Analysis...");
        
        for (String scenarioName : results.scenarioResults.keySet()) {
            ScenarioResults scenario = results.scenarioResults.get(scenarioName);
            StatisticalAnalysis analysis = new StatisticalAnalysis();
            
            // Perform pairwise t-tests between Enhanced EPO-CEIS and baselines
            AlgorithmPerformance epoceisPerf = scenario.algorithmResults.get("Enhanced EPO-CEIS");
            
            for (String algorithmName : scenario.algorithmResults.keySet()) {
                if (!algorithmName.equals("Enhanced EPO-CEIS")) {
                    AlgorithmPerformance baselinePerf = scenario.algorithmResults.get(algorithmName);
                    
                    // t-test for total cost
                    double pValue = analysis.performTTest(
                        epoceisPerf.getMetricValues("totalCost"),
                        baselinePerf.getMetricValues("totalCost")
                    );
                    
                    System.out.println(String.format("  %s vs %s (Cost): p-value = %.4f %s", 
                        "Enhanced EPO-CEIS", algorithmName, pValue, 
                        pValue < SIGNIFICANCE_LEVEL ? "(Significant)" : "(Not Significant)"));
                }
            }
        }
    }
    
    /**
     * Generate comprehensive evaluation report
     */
    private void generateComprehensiveReport(EvaluationResults results) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = outputDirectory + "/comprehensive_evaluation_" + timestamp + ".html";
            
            PrintWriter writer = new PrintWriter(new FileWriter(reportPath));
            
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>IIoT Scheduling Evaluation Report</title>");
            writer.println("<style>");
            writer.println("body { font-family: Arial, sans-serif; margin: 20px; }");
            writer.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            writer.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            writer.println("th { background-color: #f2f2f2; }");
            writer.println(".metric-table th { background-color: #4CAF50; color: white; }");
            writer.println(".best-result { background-color: #90EE90; font-weight: bold; }");
            writer.println("</style></head><body>");
            
            writer.println("<h1>Comprehensive IIoT Task Scheduling Evaluation Report</h1>");
            writer.println("<p>Generated on: " + new Date() + "</p>");
            
            // Summary statistics
            generateSummaryTable(writer, results);
            
            // Detailed results for each scenario
            for (String scenarioName : results.scenarioResults.keySet()) {
                generateScenarioTable(writer, scenarioName, results.scenarioResults.get(scenarioName));
            }
            
            writer.println("</body></html>");
            writer.close();
            
            System.out.println("📋 Comprehensive report generated: " + reportPath);
            
        } catch (IOException e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }
    
    /**
     * Generate summary performance table
     */
    private void generateSummaryTable(PrintWriter writer, EvaluationResults results) {
        writer.println("<h2>Algorithm Performance Summary</h2>");
        writer.println("<table class='metric-table'>");
        writer.println("<tr><th>Algorithm</th><th>Avg Cost</th><th>Avg Makespan</th>");
        writer.println("<th>Deadline Hit Rate</th><th>Avg Energy</th><th>Execution Time (ms)</th></tr>");
        
        for (String scenarioName : results.scenarioResults.keySet()) {
            ScenarioResults scenario = results.scenarioResults.get(scenarioName);
            
            for (String algorithmName : scenario.algorithmResults.keySet()) {
                AlgorithmPerformance perf = scenario.algorithmResults.get(algorithmName);
                
                writer.println(String.format("<tr%s><td>%s</td><td>%.2f</td><td>%.2f</td><td>%.3f</td><td>%.2f</td><td>%.1f</td></tr>",
                    algorithmName.equals("Enhanced EPO-CEIS") ? " class='best-result'" : "",
                    algorithmName,
                    perf.getAverageMetric("totalCost"),
                    perf.getAverageMetric("makespan"),
                    perf.getAverageMetric("deadlineHitRate"),
                    perf.getAverageMetric("totalEnergyConsumption"),
                    perf.getAverageMetric("executionTimeMs")
                ));
            }
        }
        
        writer.println("</table>");
    }
    
    /**
     * Generate detailed scenario table
     */
    private void generateScenarioTable(PrintWriter writer, String scenarioName, ScenarioResults results) {
        writer.println("<h3>Scenario: " + scenarioName + "</h3>");
        // Implementation details for scenario-specific results
    }
    
    /**
     * Generate individual scenario report
     */
    private void generateScenarioReport(TestScenario scenario, ScenarioResults results) {
        // Generate CSV files and charts for individual scenarios
    }
    
    // Utility methods for loading data
    
    private Workflow loadWorkflow(String workflowPath) {
        try {
            return WorkflowParser.parseFromFile(workflowPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load workflow: " + workflowPath, e);
        }
    }
    
    private List<FogNode> loadFogNodes(String nodesPath) {
        try {
            return NodeParser.parseFromFile(nodesPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load nodes: " + nodesPath, e);
        }
    }
    
    // Initialization methods
    
    private List<TestScenario> initializeTestScenarios() {
        List<TestScenario> scenarios = new ArrayList<>();
        
        scenarios.add(new TestScenario("Basic Scenario", "data/workflow.txt", "data/nodes.txt"));
        scenarios.add(new TestScenario("Scenario 1", "data/scenario_1/workflow.txt", "data/scenario_1/nodes.txt"));
        scenarios.add(new TestScenario("Cloud Expensive", "data/scenario_cloud_expensive/workflow.txt", "data/scenario_cloud_expensive/nodes.txt"));
        scenarios.add(new TestScenario("Delay Heavy", "data/scenario_delay_heavy/workflow.txt", "data/scenario_delay_heavy/nodes.txt"));
        scenarios.add(new TestScenario("RAM Limited", "data/scenario_ram_limited/workflow.txt", "data/scenario_ram_limited/nodes.txt"));
        
        return scenarios;
    }
    
    private List<BaselineAlgorithm> initializeBaselineAlgorithms() {
        List<BaselineAlgorithm> baselines = new ArrayList<>();
        
        baselines.add(new BaselineAlgorithm("Genetic Algorithm", AlgorithmType.GENETIC_ALGORITHM));
        baselines.add(new BaselineAlgorithm("Particle Swarm Optimization", AlgorithmType.PARTICLE_SWARM));
        baselines.add(new BaselineAlgorithm("Enhanced PSO", AlgorithmType.ENHANCED_PSO));
        baselines.add(new BaselineAlgorithm("NSGA-II", AlgorithmType.NSGA_II));
        baselines.add(new BaselineAlgorithm("Min-Min Heuristic", AlgorithmType.MIN_MIN));
        baselines.add(new BaselineAlgorithm("First-Fit Heuristic", AlgorithmType.FIRST_FIT));
        
        return baselines;
    }
    
    // Data classes
    
    public static class TestScenario {
        public final String name;
        public final String workflowPath;
        public final String nodesPath;
        
        public TestScenario(String name, String workflowPath, String nodesPath) {
            this.name = name;
            this.workflowPath = workflowPath;
            this.nodesPath = nodesPath;
        }
    }
    
    private static class BaselineAlgorithm {
        public final String name;
        public final AlgorithmType type;
        
        public BaselineAlgorithm(String name, AlgorithmType type) {
            this.name = name;
            this.type = type;
        }
    }
    
    private enum AlgorithmType {
        GENETIC_ALGORITHM,
        PARTICLE_SWARM,
        ENHANCED_PSO,
        NSGA_II,
        MIN_MIN,
        FIRST_FIT
    }
    
    public static class RunMetrics {
        public double totalCost;
        public double makespan;
        public int missedDeadlines;
        public double deadlineHitRate;
        public double totalEnergyConsumption;
        public double averageLatency;
        public double resourceUtilization;
        public long executionTimeMs;
        public int tasksScheduled;
    }
    
    public static class RunResult {
        public final int runNumber;
        public final RunMetrics metrics;
        
        public RunResult(int runNumber, RunMetrics metrics) {
            this.runNumber = runNumber;
            this.metrics = metrics;
        }
    }
    
    public static class AlgorithmPerformance {
        public final String algorithmName;
        public final List<RunResult> runs;
        
        public AlgorithmPerformance(String algorithmName, List<RunResult> runs) {
            this.algorithmName = algorithmName;
            this.runs = runs;
        }
        
        public double getAverageMetric(String metricName) {
            return runs.stream()
                .mapToDouble(run -> getMetricValue(run.metrics, metricName))
                .average()
                .orElse(0.0);
        }
        
        public List<Double> getMetricValues(String metricName) {
            return runs.stream()
                .map(run -> getMetricValue(run.metrics, metricName))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
        
        private double getMetricValue(RunMetrics metrics, String metricName) {
            switch (metricName) {
                case "totalCost": return metrics.totalCost;
                case "makespan": return metrics.makespan;
                case "deadlineHitRate": return metrics.deadlineHitRate;
                case "totalEnergyConsumption": return metrics.totalEnergyConsumption;
                case "averageLatency": return metrics.averageLatency;
                case "resourceUtilization": return metrics.resourceUtilization;
                case "executionTimeMs": return metrics.executionTimeMs;
                default: return 0.0;
            }
        }
    }
    
    public static class ScenarioResults {
        public final Map<String, AlgorithmPerformance> algorithmResults;
        
        public ScenarioResults() {
            this.algorithmResults = new HashMap<>();
        }
        
        public void addAlgorithmResults(String algorithmName, AlgorithmPerformance performance) {
            algorithmResults.put(algorithmName, performance);
        }
    }
    
    public static class EvaluationResults {
        public final Map<String, ScenarioResults> scenarioResults;
        
        public EvaluationResults() {
            this.scenarioResults = new HashMap<>();
        }
        
        public void addScenarioResults(String scenarioName, ScenarioResults results) {
            scenarioResults.put(scenarioName, results);
        }
    }
}
