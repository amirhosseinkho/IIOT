package test;

import simulation.CloudSimWrapper;
import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import java.util.*;

/**
 * Test class for CloudSimWrapper to verify CloudSim integration
 */
public class TestCloudSimWrapper {
    
    public static void main(String[] args) {
        System.out.println("🧪 Testing CloudSimWrapper");
        System.out.println("===========================");
        
        // Initialize CloudSim
        CloudSimWrapper.initializeCloudSim();
        
        // Create test workflow
        Workflow workflow = createTestWorkflow();
        List<FogNode> nodes = createTestNodes();
        
        System.out.println("\n📊 Test Workflow:");
        System.out.println("  - Tasks: " + workflow.getAllTasks().size());
        System.out.println("  - Nodes: " + nodes.size());
        
        // Test algorithm comparison
        System.out.println("\n🔬 Testing Algorithm Comparison...");
        Map<String, CloudSimWrapper.SimulationResult> results = 
            CloudSimWrapper.compareAlgorithms(workflow, nodes);
        
        // Display results
        System.out.println("\n📈 Results:");
        System.out.println("============");
        
        for (Map.Entry<String, CloudSimWrapper.SimulationResult> entry : results.entrySet()) {
            String algorithm = entry.getKey();
            CloudSimWrapper.SimulationResult result = entry.getValue();
            
            System.out.println("\n" + algorithm + ":");
            System.out.println("  " + result.toString());
        }
        
        System.out.println("\n✅ CloudSimWrapper test completed!");
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
}
