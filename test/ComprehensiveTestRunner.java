package test;

import algorithms.*;
import core.*;
import evaluation.ResultsExporter;
import java.util.*;

/**
 * Comprehensive Test Runner for IIoT Task Scheduling Algorithms
 * Tests all algorithms and exports results to CSV files for Python analysis
 */
public class ComprehensiveTestRunner {
    
    private static final String[] SCENARIOS = {
        "Basic", "Cloud_Expensive", "Delay_Heavy", "RAM_Limited"
    };
    
    private static final int[] TASK_COUNTS = {10, 20, 50, 100};
    private static final int[] NODE_COUNTS = {5, 10, 15, 20};
    
    public static void main(String[] args) {
        System.out.println("🚀 Comprehensive IIoT Scheduler Test Runner");
        System.out.println("==========================================");
        
        ResultsExporter exporter = new ResultsExporter();
        
        try {
            // Test different scenarios
            for (String scenario : SCENARIOS) {
                System.out.println("\n📊 Testing Scenario: " + scenario);
                testScenario(scenario, exporter);
            }
            
            // Test scalability with different task/node counts
            System.out.println("\n📈 Testing Scalability");
            testScalability(exporter);
            
            System.out.println("\n✅ All tests completed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Test execution failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            exporter.closeWriters();
        }
    }
    
    private static void testScenario(String scenario, ResultsExporter exporter) {
        try {
            // Create workflow and nodes for this scenario
            Workflow workflow = createScenarioWorkflow(scenario);
            List<FogNode> nodes = createScenarioNodes(scenario);
            
            System.out.println("  - Tasks: " + workflow.getAllTasks().size());
            System.out.println("  - Nodes: " + nodes.size());
            
            // Test Enhanced EPO-CEIS
            testAlgorithm("Enhanced_EPO_CEIS", scenario, workflow, nodes, exporter, () -> {
                EnhancedEPOCEIS epoceis = new EnhancedEPOCEIS(workflow, nodes);
                return epoceis.schedule();
            });
            
            // Test HFCO
            testAlgorithm("HFCO", scenario, workflow, nodes, exporter, () -> {
                HFCOWithPlacement hfco = new HFCOWithPlacement(nodes, workflow);
                return hfco.optimize();
            });
            
            // Test baseline algorithms
            BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, nodes);
            
            testAlgorithm("Genetic_Algorithm", scenario, workflow, nodes, exporter, () -> {
                return baseline.geneticAlgorithm();
            });
            
            testAlgorithm("Particle_Swarm_Optimization", scenario, workflow, nodes, exporter, () -> {
                return baseline.particleSwarmOptimization();
            });
            
            testAlgorithm("Enhanced_PSO", scenario, workflow, nodes, exporter, () -> {
                return baseline.enhancedParticleSwarmOptimization();
            });
            
            testAlgorithm("NSGA_II", scenario, workflow, nodes, exporter, () -> {
                return baseline.nsgaII();
            });
            
            testAlgorithm("Min_Min", scenario, workflow, nodes, exporter, () -> {
                return baseline.minMin();
            });
            
            testAlgorithm("First_Fit", scenario, workflow, nodes, exporter, () -> {
                return baseline.firstFit();
            });
            
        } catch (Exception e) {
            System.err.println("  ❌ Error testing scenario " + scenario + ": " + e.getMessage());
        }
    }
    
    private static void testScalability(ResultsExporter exporter) {
        for (int taskCount : TASK_COUNTS) {
            for (int nodeCount : NODE_COUNTS) {
                try {
                    System.out.println("  - Testing: " + taskCount + " tasks, " + nodeCount + " nodes");
                    
                    Workflow workflow = createScalableWorkflow(taskCount);
                    List<FogNode> nodes = createScalableNodes(nodeCount);
                    
                    // Test key algorithms for scalability
                    testAlgorithm("Enhanced_EPO_CEIS_Scalability", "Scalability", workflow, nodes, exporter, () -> {
                        EnhancedEPOCEIS epoceis = new EnhancedEPOCEIS(workflow, nodes);
                        return epoceis.schedule();
                    });
                    
                    testAlgorithm("HFCO_Scalability", "Scalability", workflow, nodes, exporter, () -> {
                        HFCOWithPlacement hfco = new HFCOWithPlacement(nodes, workflow);
                        return hfco.optimize();
                    });
                    
                } catch (Exception e) {
                    System.err.println("    ❌ Error testing scalability: " + e.getMessage());
                }
            }
        }
    }
    
    private static void testAlgorithm(String algorithmName, String scenario, 
                                    Workflow workflow, List<FogNode> nodes,
                                    ResultsExporter exporter, AlgorithmTest test) {
        try {
            System.out.println("    🧬 Testing " + algorithmName + "...");
            
            long startTime = System.currentTimeMillis();
            Object result = test.run();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Export results
            exporter.exportAlgorithmResults(algorithmName, scenario, workflow, nodes, result, executionTime);
            
            System.out.println("      ✅ Success! Execution time: " + executionTime + " ms");
            
        } catch (Exception e) {
            System.err.println("      ❌ Failed: " + e.getMessage());
        }
    }
    
    // Scenario creation methods
    private static Workflow createScenarioWorkflow(String scenario) {
        Workflow workflow = new Workflow();
        
        int baseTaskCount = 20;
        switch (scenario) {
            case "Basic":
                baseTaskCount = 20;
                break;
            case "Cloud_Expensive":
                baseTaskCount = 25;
                break;
            case "Delay_Heavy":
                baseTaskCount = 30;
                break;
            case "RAM_Limited":
                baseTaskCount = 15;
                break;
        }
        
        // Create tasks
        for (int i = 0; i < baseTaskCount; i++) {
            int length = 1000 + (i * 100);
            long fileSize = 50 + (i * 5);
            long outputSize = 25 + (i * 3);
            double deadline = 10.0 + (i * 0.5);
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, 1, deadline);
            workflow.addTask(task);
        }
        
        // Add dependencies (simple chain)
        for (int i = 0; i < baseTaskCount - 1; i++) {
            workflow.addDependency(i, i + 1);
        }
        
        return workflow;
    }
    
    private static List<FogNode> createScenarioNodes(String scenario) {
        List<FogNode> nodes = new ArrayList<>();
        
        int baseNodeCount = 8;
        double baseCost = 0.01;
        
        switch (scenario) {
            case "Basic":
                baseNodeCount = 8;
                baseCost = 0.01;
                break;
            case "Cloud_Expensive":
                baseNodeCount = 10;
                baseCost = 0.05; // Higher cost
                break;
            case "Delay_Heavy":
                baseNodeCount = 12;
                baseCost = 0.015;
                break;
            case "RAM_Limited":
                baseNodeCount = 6;
                baseCost = 0.02;
                break;
        }
        
        // Create fog nodes
        for (int i = 0; i < baseNodeCount - 2; i++) {
            int mips = 1000 + (i * 200);
            int ram = 2048 + (i * 512);
            long bw = 1000 + (i * 200);
            long storage = 10000 + (i * 5000);
            double cost = baseCost + (i * 0.002);
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, false, cost);
            nodes.add(node);
        }
        
        // Create cloud nodes
        for (int i = baseNodeCount - 2; i < baseNodeCount; i++) {
            int mips = 3000 + (i * 500);
            int ram = 8192 + (i * 1024);
            long bw = 2500 + (i * 500);
            long storage = 50000 + (i * 10000);
            double cost = baseCost * 2.5 + (i * 0.005);
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, true, cost);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    // Scalability testing methods
    private static Workflow createScalableWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        
        for (int i = 0; i < taskCount; i++) {
            int length = 1000 + (i * 50);
            long fileSize = 50 + (i * 2);
            long outputSize = 25 + (i * 1);
            double deadline = 10.0 + (i * 0.2);
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, 1, deadline);
            workflow.addTask(task);
        }
        
        // Add dependencies (simple chain)
        for (int i = 0; i < taskCount - 1; i++) {
            workflow.addDependency(i, i + 1);
        }
        
        return workflow;
    }
    
    private static List<FogNode> createScalableNodes(int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        
        // Create fog nodes (70% of total)
        int fogNodeCount = (int) (nodeCount * 0.7);
        for (int i = 0; i < fogNodeCount; i++) {
            int mips = 1000 + (i * 150);
            int ram = 2048 + (i * 256);
            long bw = 1000 + (i * 100);
            long storage = 10000 + (i * 2000);
            double cost = 0.01 + (i * 0.001);
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, false, cost);
            nodes.add(node);
        }
        
        // Create cloud nodes (30% of total)
        for (int i = fogNodeCount; i < nodeCount; i++) {
            int mips = 3000 + (i * 300);
            int ram = 8192 + (i * 512);
            long bw = 2500 + (i * 200);
            long storage = 50000 + (i * 5000);
            double cost = 0.025 + (i * 0.002);
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, true, cost);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    @FunctionalInterface
    private interface AlgorithmTest {
        Object run();
    }
}
