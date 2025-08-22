package test;

import algorithms.BaselineAlgorithms;
import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import java.util.*;

/**
 * Test class to verify that the fixed baseline algorithms work properly
 * This will help identify if the PSO infinite cost issue is resolved
 */
public class TestFixedAlgorithms {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing Fixed Baseline Algorithms");
        System.out.println("====================================");
        
        // Create test workflow
        Workflow workflow = createTestWorkflow();
        List<FogNode> fogNodes = createTestNodes();
        
        BaselineAlgorithms baseline = new BaselineAlgorithms(workflow, fogNodes);
        
        // Test each algorithm
        testAlgorithm("Genetic Algorithm", () -> baseline.geneticAlgorithm());
        testAlgorithm("Particle Swarm Optimization", () -> baseline.particleSwarmOptimization());
        testAlgorithm("Enhanced PSO", () -> baseline.enhancedParticleSwarmOptimization());
        testAlgorithm("NSGA-II", () -> baseline.nsgaII());
        testAlgorithm("Min-Min", () -> baseline.minMin());
        testAlgorithm("First-Fit", () -> baseline.firstFit());
        
        System.out.println("\n✅ All algorithm tests completed!");
    }
    
    private static void testAlgorithm(String name, AlgorithmTest test) {
        System.out.println("\n🔬 Testing " + name + "...");
        try {
            long startTime = System.currentTimeMillis();
            BaselineAlgorithms.SchedulingResult result = test.run();
            long endTime = System.currentTimeMillis();
            
            System.out.println("  ✅ Success!");
            System.out.println("  📊 Total Cost: " + String.format("%.4f", result.totalCost));
            System.out.println("  ⏱️  Execution Time: " + (endTime - startTime) + " ms");
            System.out.println("  📋 Tasks Assigned: " + result.taskAssignments.size());
            
            // Validate cost is not infinite or NaN
            if (Double.isInfinite(result.totalCost) || Double.isNaN(result.totalCost)) {
                System.err.println("  ❌ ERROR: Invalid cost value detected!");
            } else if (result.totalCost > 1e6) {
                System.err.println("  ⚠️  WARNING: Very high cost value: " + result.totalCost);
            }
            
        } catch (Exception e) {
            System.err.println("  ❌ FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static Workflow createTestWorkflow() {
        Workflow workflow = new Workflow();
        
        // Create test tasks
        IIoTTask task0 = new IIoTTask(0, 1000, 50, 25, 1, 10.0);
        IIoTTask task1 = new IIoTTask(1, 2000, 100, 50, 1, 15.0);
        IIoTTask task2 = new IIoTTask(2, 1500, 75, 40, 1, 12.0);
        IIoTTask task3 = new IIoTTask(3, 3000, 150, 75, 1, 20.0);
        
        workflow.addTask(task0);
        workflow.addTask(task1);
        workflow.addTask(task2);
        workflow.addTask(task3);
        
        // Add dependencies
        workflow.addDependency(0, 2);
        workflow.addDependency(1, 2);
        workflow.addDependency(2, 3);
        
        return workflow;
    }
    
    private static List<FogNode> createTestNodes() {
        List<FogNode> nodes = new ArrayList<>();
        
        // Fog nodes
        nodes.add(new FogNode(0, 1000, 2048, 1000, 10000, false, 0.01));
        nodes.add(new FogNode(1, 1500, 2048, 1500, 10000, false, 0.012));
        nodes.add(new FogNode(2, 2000, 4096, 2000, 20000, false, 0.015));
        
        // Cloud nodes
        nodes.add(new FogNode(3, 3000, 8192, 2500, 50000, true, 0.025));
        nodes.add(new FogNode(4, 4000, 4096, 3000, 30000, true, 0.03));
        
        return nodes;
    }
    
    @FunctionalInterface
    private interface AlgorithmTest {
        BaselineAlgorithms.SchedulingResult run();
    }
}
