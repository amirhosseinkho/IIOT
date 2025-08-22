package evaluation;

import java.io.File;

/**
 * Main class for running comprehensive evaluation of IIoT scheduling algorithms
 * Usage: java evaluation.MainEvaluation [output_directory]
 */
public class MainEvaluation {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("  IIoT Task Scheduling - Comprehensive Evaluation");
        System.out.println("  Enhanced EPO-CEIS vs Baseline Algorithms");
        System.out.println("===============================================\n");
        
        // Determine output directory
        String outputDir = args.length > 0 ? args[0] : "evaluation_results";
        
        // Create output directory if it doesn't exist
        File outputFolder = new File(outputDir);
        if (!outputFolder.exists()) {
            if (outputFolder.mkdirs()) {
                System.out.println("📁 Created output directory: " + outputDir);
            } else {
                System.err.println("❌ Failed to create output directory: " + outputDir);
                System.exit(1);
            }
        }
        
        try {
            // Initialize evaluation framework
            System.out.println("🔧 Initializing evaluation framework...");
            ComprehensiveEvaluationFramework framework = new ComprehensiveEvaluationFramework(outputDir);
            
            // Run full evaluation
            System.out.println("🚀 Starting comprehensive evaluation...");
            ComprehensiveEvaluationFramework.EvaluationResults results = framework.runFullEvaluation();
            
            // Print summary
            printEvaluationSummary(results);
            
            System.out.println("\n✅ Evaluation completed successfully!");
            System.out.println("📊 Results saved to: " + new File(outputDir).getAbsolutePath());
            
        } catch (Exception e) {
            System.err.println("❌ Evaluation failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Print a summary of evaluation results
     */
    private static void printEvaluationSummary(ComprehensiveEvaluationFramework.EvaluationResults results) {
        System.out.println("\n📈 EVALUATION SUMMARY");
        System.out.println("====================");
        
        for (String scenarioName : results.scenarioResults.keySet()) {
            System.out.println("\n🎯 Scenario: " + scenarioName);
            
            ComprehensiveEvaluationFramework.ScenarioResults scenario = results.scenarioResults.get(scenarioName);
            
            // Find Enhanced EPO-CEIS results
            ComprehensiveEvaluationFramework.AlgorithmPerformance epoceisPerf = 
                scenario.algorithmResults.get("Enhanced EPO-CEIS");
            
            if (epoceisPerf != null) {
                System.out.println("  🧬 Enhanced EPO-CEIS Performance:");
                System.out.printf("     Average Cost: %.2f\n", epoceisPerf.getAverageMetric("totalCost"));
                System.out.printf("     Deadline Hit Rate: %.1f%%\n", epoceisPerf.getAverageMetric("deadlineHitRate") * 100);
                System.out.printf("     Average Makespan: %.2f s\n", epoceisPerf.getAverageMetric("makespan"));
                System.out.printf("     Average Energy: %.2f Wh\n", epoceisPerf.getAverageMetric("totalEnergyConsumption"));
                
                // Compare with best baseline
                String bestBaseline = findBestBaseline(scenario);
                if (bestBaseline != null) {
                    ComprehensiveEvaluationFramework.AlgorithmPerformance baselinePerf = 
                        scenario.algorithmResults.get(bestBaseline);
                    
                    double costImprovement = ((baselinePerf.getAverageMetric("totalCost") - 
                                             epoceisPerf.getAverageMetric("totalCost")) / 
                                             baselinePerf.getAverageMetric("totalCost")) * 100;
                    
                    System.out.printf("  📊 vs Best Baseline (%s):\n", bestBaseline);
                    System.out.printf("     Cost Improvement: %.1f%%\n", costImprovement);
                }
            }
            
            System.out.println("  📋 Algorithm Rankings (by cost):");
            printAlgorithmRankings(scenario);
        }
    }
    
    /**
     * Find the best performing baseline algorithm
     */
    private static String findBestBaseline(ComprehensiveEvaluationFramework.ScenarioResults scenario) {
        String bestBaseline = null;
        double bestCost = Double.MAX_VALUE;
        
        for (String algorithmName : scenario.algorithmResults.keySet()) {
            if (!algorithmName.equals("Enhanced EPO-CEIS")) {
                double avgCost = scenario.algorithmResults.get(algorithmName).getAverageMetric("totalCost");
                if (avgCost < bestCost) {
                    bestCost = avgCost;
                    bestBaseline = algorithmName;
                }
            }
        }
        
        return bestBaseline;
    }
    
    /**
     * Print algorithm rankings by cost
     */
    private static void printAlgorithmRankings(ComprehensiveEvaluationFramework.ScenarioResults scenario) {
        // Create list of algorithms sorted by average cost
        scenario.algorithmResults.entrySet().stream()
            .sorted((e1, e2) -> Double.compare(
                e1.getValue().getAverageMetric("totalCost"),
                e2.getValue().getAverageMetric("totalCost")
            ))
            .forEach(entry -> {
                String name = entry.getKey();
                double cost = entry.getValue().getAverageMetric("totalCost");
                double deadlineHitRate = entry.getValue().getAverageMetric("deadlineHitRate");
                
                String medal = "";
                if (name.equals("Enhanced EPO-CEIS")) {
                    medal = " 🥇";
                } else if (scenario.algorithmResults.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare(
                        e1.getValue().getAverageMetric("totalCost"),
                        e2.getValue().getAverageMetric("totalCost")
                    ))
                    .skip(1).findFirst().get().getKey().equals(name)) {
                    medal = " 🥈";
                }
                
                System.out.printf("     %s: Cost=%.2f, Hit Rate=%.1f%%%s\n", 
                    name, cost, deadlineHitRate * 100, medal);
            });
    }
}
