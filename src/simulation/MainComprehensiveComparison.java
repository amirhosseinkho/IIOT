package simulation;

import core.FogNode;
import core.Workflow;
import simulation.NodeParser;
import simulation.WorkflowParser;

import java.util.List;

/**
 * Main class for Comprehensive Algorithm Comparison
 * Demonstrates EPO-based CEIS, HFCO, and baseline algorithms
 */
public class MainComprehensiveComparison {
    
    public static void main(String[] args) {
        System.out.println("🚀 IIoT Task Scheduling - Comprehensive Algorithm Comparison");
        System.out.println("=============================================================");
        System.out.println("Project: Cost-Optimised IIoT Task Scheduling with Deadline Guarantees");
        System.out.println("Algorithms: EPO-based CEIS, HFCO, GA, PSO, EPSO, NSGA-II, Min-Min, FirstFit");
        System.out.println();
        
        try {
            // Load different scenarios
            runScenario("scenario_1", "Basic IIoT Workflow");
            runScenario("scenario_cloud_expensive", "Cloud-Expensive Scenario");
            runScenario("scenario_delay_heavy", "Delay-Heavy Scenario");
            runScenario("scenario_ram_limited", "RAM-Limited Scenario");
            
            System.out.println("\n🎉 All scenarios completed successfully!");
            System.out.println("📊 Check generated result files for detailed analysis.");
            
        } catch (Exception e) {
            System.err.println("❌ Error running comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run comparison on a specific scenario
     */
    private static void runScenario(String scenarioName, String description) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌍 Scenario: " + description + " (" + scenarioName + ")");
        System.out.println("=".repeat(60));
        
        try {
            // Load nodes and workflow for this scenario
            String nodesFile = "data/" + scenarioName + "/nodes.txt";
            String workflowFile = "data/" + scenarioName + "/workflow.txt";
            
            // Parse nodes and workflow
            List<FogNode> nodes = NodeParser.parseFromFile(nodesFile);
            Workflow workflow = WorkflowParser.parseFromFile(workflowFile);
            
            System.out.println("📡 Loaded " + nodes.size() + " nodes from " + nodesFile);
            System.out.println("⚡ Loaded workflow with " + workflow.getAllTasks().size() + " tasks from " + workflowFile);
            
            // Count fog vs cloud nodes
            int fogCount = 0, cloudCount = 0;
            for (FogNode node : nodes) {
                if (node.isCloud()) cloudCount++; else fogCount++;
            }
            System.out.println("🏗️  Node distribution: " + fogCount + " fog nodes, " + cloudCount + " cloud nodes");
            
            // Run comprehensive comparison
            ComprehensiveComparison comparison = new ComprehensiveComparison(workflow, nodes);
            comparison.runComprehensiveComparison();
            
            System.out.println("✅ Scenario " + scenarioName + " completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Error in scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run single algorithm comparison (for testing)
     */
    public static void runSingleComparison(String scenarioName) {
        System.out.println("🔬 Running Single Algorithm Comparison for " + scenarioName);
        
        try {
            String nodesFile = "data/" + scenarioName + "/nodes.txt";
            String workflowFile = "data/" + scenarioName + "/workflow.txt";
            
            List<FogNode> nodes = NodeParser.parseFromFile(nodesFile);
            Workflow workflow = WorkflowParser.parseFromFile(workflowFile);
            
            System.out.println("📊 Workflow: " + workflow.getAllTasks().size() + " tasks");
            System.out.println("🏗️  Nodes: " + nodes.size() + " total");
            
            // Run comparison
            ComprehensiveComparison comparison = new ComprehensiveComparison(workflow, nodes);
            comparison.runComprehensiveComparison();
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Run performance analysis on different workload sizes
     */
    public static void runWorkloadAnalysis() {
        System.out.println("\n📈 Workload Size Analysis");
        System.out.println("=========================");
        
        String[] scenarios = {"scenario_1", "scenario_cloud_expensive", "scenario_delay_heavy", "scenario_ram_limited"};
        
        for (String scenario : scenarios) {
            try {
                String nodesFile = "data/" + scenario + "/nodes.txt";
                String workflowFile = "data/" + scenario + "/workflow.txt";
                
                List<FogNode> nodes = NodeParser.parseFromFile(nodesFile);
                Workflow workflow = WorkflowParser.parseFromFile(workflowFile);
                
                int taskCount = workflow.getAllTasks().size();
                System.out.println("📊 " + scenario + ": " + taskCount + " tasks");
                
                // Run EPO-CEIS only for quick analysis
                long startTime = System.currentTimeMillis();
                algorithms.EnhancedEPOCEIS ceis = new algorithms.EnhancedEPOCEIS(workflow, nodes);
                algorithms.EnhancedEPOCEIS.SchedulingResult result = ceis.schedule();
                long executionTime = System.currentTimeMillis() - startTime;
                
                System.out.println("   🎯 EPO-CEIS: Cost=" + String.format("%.2f", result.totalCost) + 
                                 ", Time=" + executionTime + "ms");
                
            } catch (Exception e) {
                System.err.println("   ❌ Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Run fog density analysis
     */
    public static void runFogDensityAnalysis() {
        System.out.println("\n🌐 Fog Density Analysis");
        System.out.println("=======================");
        
        String[] scenarios = {"scenario_1", "scenario_cloud_expensive", "scenario_delay_heavy", "scenario_ram_limited"};
        
        for (String scenario : scenarios) {
            try {
                String nodesFile = "data/" + scenario + "/nodes.txt";
                String workflowFile = "data/" + scenario + "/workflow.txt";
                
                List<FogNode> nodes = NodeParser.parseFromFile(nodesFile);
                Workflow workflow = WorkflowParser.parseFromFile(workflowFile);
                
                // Count fog nodes
                int fogCount = 0;
                for (FogNode node : nodes) {
                    if (!node.isCloud()) fogCount++;
                }
                
                System.out.println("📡 " + scenario + ": " + fogCount + " fog nodes");
                
                // Run HFCO for fog deployment analysis
                long startTime = System.currentTimeMillis();
                algorithms.HFCOWithPlacement hfco = new algorithms.HFCOWithPlacement(nodes, workflow);
                algorithms.HFCOWithPlacement.HFCOResult result = hfco.optimize();
                long executionTime = System.currentTimeMillis() - startTime;
                
                System.out.println("   🌐 HFCO: Active fog=" + result.fogResult.activeFogNodes.size() + 
                                 ", Deploy cost=" + String.format("%.2f", result.fogResult.deploymentCost) +
                                 ", Time=" + executionTime + "ms");
                
            } catch (Exception e) {
                System.err.println("   ❌ Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Run sensitivity analysis
     */
    public static void runSensitivityAnalysis() {
        System.out.println("\n🔍 Sensitivity Analysis");
        System.out.println("=======================");
        
        try {
            // Use scenario_1 for sensitivity analysis
            String nodesFile = "data/scenario_1/nodes.txt";
            String workflowFile = "data/scenario_1/workflow.txt";
            
            List<FogNode> nodes = NodeParser.parseFromFile(nodesFile);
            Workflow workflow = WorkflowParser.parseFromFile(workflowFile);
            
            System.out.println("📊 Analyzing sensitivity to parameter changes...");
            
            // Test different penalty values for deadline violations
            double[] penaltyValues = {100.0, 500.0, 1000.0, 2000.0, 5000.0};
            
            for (double penalty : penaltyValues) {
                System.out.println("   🔧 Testing penalty M = " + penalty);
                
                // Create modified cost calculator with different penalty
                // (This would require modifying the CostCalculator class)
                // For now, just show the concept
                System.out.println("      💡 Would test cost sensitivity with penalty " + penalty);
            }
            
            // Test different fog node densities
            System.out.println("   🌐 Testing fog node density variations...");
            System.out.println("      💡 Would test with 25%, 50%, 75%, 100% of fog nodes active");
            
        } catch (Exception e) {
            System.err.println("❌ Error in sensitivity analysis: " + e.getMessage());
        }
    }
}
