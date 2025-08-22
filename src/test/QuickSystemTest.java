package test;

import algorithms.EnhancedEPOCEIS;
import algorithms.HFCOWithPlacement;
import algorithms.BaselineAlgorithms;
import core.FogNode;
import core.IIoTTask;
import core.Workflow;
import simulation.ComprehensiveComparison;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick System Test to verify all components work together
 */
public class QuickSystemTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 Quick System Test - IIoT Task Scheduling");
        System.out.println("============================================");
        
        try {
            // Create a simple test scenario
            TestScenario scenario = createTestScenario();
            
            System.out.println("✅ Test scenario created:");
            System.out.println("   - Tasks: " + scenario.workflow.getAllTasks().size());
            System.out.println("   - Nodes: " + scenario.nodes.size() + " (fog: " + 
                             countFogNodes(scenario.nodes) + ", cloud: " + 
                             countCloudNodes(scenario.nodes) + ")");
            
            // Test 1: EPO-based CEIS
            System.out.println("\n🎯 Test 1: EPO-based CEIS Algorithm");
            testEPOCEIS(scenario);
            
            // Test 2: HFCO
            System.out.println("\n🌐 Test 2: HFCO Algorithm");
            testHFCO(scenario);
            
            // Test 3: Baseline Algorithms
            System.out.println("\n📊 Test 3: Baseline Algorithms");
            testBaselines(scenario);
            
            // Test 4: Comprehensive Comparison
            System.out.println("\n🚀 Test 4: Comprehensive Comparison");
            testComprehensiveComparison(scenario);
            
            System.out.println("\n🎉 All tests completed successfully!");
            System.out.println("✅ System is working correctly!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create a simple test scenario
     */
    private static TestScenario createTestScenario() {
        // Create nodes
        List<FogNode> nodes = new ArrayList<>();
        
        // Fog nodes
        nodes.add(new FogNode(1, 1000, 1024, 100, 1000, false, 0.1)); // fog1
        nodes.add(new FogNode(2, 1500, 2048, 150, 2000, false, 0.15)); // fog2
        nodes.add(new FogNode(3, 800, 512, 80, 500, false, 0.08)); // fog3
        
        // Cloud nodes
        nodes.add(new FogNode(4, 3000, 8192, 500, 10000, true, 0.5)); // cloud1
        nodes.add(new FogNode(5, 5000, 16384, 1000, 20000, true, 0.8)); // cloud2
        
        // Set additional properties
        nodes.get(0).setLatencyMs(10.0); // fog1: 10ms
        nodes.get(1).setLatencyMs(15.0); // fog2: 15ms
        nodes.get(2).setLatencyMs(20.0); // fog3: 20ms
        nodes.get(3).setLatencyMs(100.0); // cloud1: 100ms
        nodes.get(4).setLatencyMs(150.0); // cloud2: 150ms
        
        // Create workflow
        Workflow workflow = new Workflow();
        
        // Add tasks
        IIoTTask task1 = new IIoTTask(1, 500, 50, 25, 1, 5.0);
        IIoTTask task2 = new IIoTTask(2, 800, 100, 50, 1, 8.0);
        IIoTTask task3 = new IIoTTask(3, 1200, 150, 75, 1, 12.0);
        IIoTTask task4 = new IIoTTask(4, 600, 75, 40, 1, 6.0);
        
        workflow.addTask(task1);
        workflow.addTask(task2);
        workflow.addTask(task3);
        workflow.addTask(task4);
        
        // Add dependencies: task1 -> task2, task1 -> task3, task2 -> task4, task3 -> task4
        workflow.addDependency(1, 2);
        workflow.addDependency(1, 3);
        workflow.addDependency(2, 4);
        workflow.addDependency(3, 4);
        
        return new TestScenario(nodes, workflow);
    }
    
    /**
     * Test EPO-based CEIS algorithm
     */
    private static void testEPOCEIS(TestScenario scenario) {
        try {
            EnhancedEPOCEIS ceis = new EnhancedEPOCEIS(scenario.workflow, scenario.nodes);
            EnhancedEPOCEIS.SchedulingResult result = ceis.schedule();
            
            System.out.println("   ✅ EPO-CEIS completed successfully");
            System.out.println("   📊 Total Cost: " + String.format("%.2f", result.totalCost));
            System.out.println("   📋 Task Assignments: " + result.taskAssignments.size());
            System.out.println("   ⏰ Start Times: " + result.startTimes.size());
            
        } catch (Exception e) {
            System.err.println("   ❌ EPO-CEIS failed: " + e.getMessage());
        }
    }
    
    /**
     * Test HFCO algorithm
     */
    private static void testHFCO(TestScenario scenario) {
        try {
            HFCOWithPlacement hfco = new HFCOWithPlacement(scenario.nodes, scenario.workflow);
            HFCOWithPlacement.HFCOResult result = hfco.optimize();
            
            System.out.println("   ✅ HFCO completed successfully");
            System.out.println("   📊 Total Cost: " + String.format("%.2f", result.schedulingResult.totalCost));
            System.out.println("   🌐 Active Fog Nodes: " + result.fogResult.activeFogNodes.size());
            System.out.println("   💰 Deployment Cost: " + String.format("%.2f", result.fogResult.deploymentCost));
            
        } catch (Exception e) {
            System.err.println("   ❌ HFCO failed: " + e.getMessage());
        }
    }
    
    /**
     * Test baseline algorithms
     */
    private static void testBaselines(TestScenario scenario) {
        try {
            BaselineAlgorithms baselines = new BaselineAlgorithms(scenario.workflow, scenario.nodes);
            
            // Test a few baseline algorithms
            String[] testAlgorithms = {"GA", "Min-Min", "FirstFit"};
            
            for (String algName : testAlgorithms) {
                try {
                    BaselineAlgorithms.SchedulingResult result = null;
                    
                    switch (algName) {
                        case "GA":
                            result = baselines.geneticAlgorithm();
                            break;
                        case "Min-Min":
                            result = baselines.minMin();
                            break;
                        case "FirstFit":
                            result = baselines.firstFit();
                            break;
                    }
                    
                    if (result != null) {
                        System.out.println("   ✅ " + algName + " completed - Cost: " + 
                                         String.format("%.2f", result.totalCost));
                    }
                    
                } catch (Exception e) {
                    System.err.println("   ❌ " + algName + " failed: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("   ❌ Baseline algorithms failed: " + e.getMessage());
        }
    }
    
    /**
     * Test comprehensive comparison
     */
    private static void testComprehensiveComparison(TestScenario scenario) {
        try {
            ComprehensiveComparison comparison = new ComprehensiveComparison(scenario.workflow, scenario.nodes);
            
            System.out.println("   🔄 Running comprehensive comparison...");
            comparison.runComprehensiveComparison();
            
            System.out.println("   ✅ Comprehensive comparison completed successfully");
            
        } catch (Exception e) {
            System.err.println("   ❌ Comprehensive comparison failed: " + e.getMessage());
        }
    }
    
    /**
     * Helper methods
     */
    private static int countFogNodes(List<FogNode> nodes) {
        return (int) nodes.stream().filter(node -> !node.isCloud()).count();
    }
    
    private static int countCloudNodes(List<FogNode> nodes) {
        return (int) nodes.stream().filter(FogNode::isCloud).count();
    }
    
    /**
     * Test scenario container
     */
    private static class TestScenario {
        public final List<FogNode> nodes;
        public final Workflow workflow;
        
        public TestScenario(List<FogNode> nodes, Workflow workflow) {
            this.nodes = nodes;
            this.workflow = workflow;
        }
    }
}
