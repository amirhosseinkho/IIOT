package algorithms;

import core.FogNode;
import core.IIoTTask;
import core.Workflow;
import utils.CostCalculator;
import utils.DelayCalculator;

import java.util.*;

/**
 * HFCO - Hierarchical Fog-Cloud Optimization
 * Integrated algorithm combining:
 * 1. Outer layer: Fog deployment optimization (HFCOFogPlacementOptimizer)
 * 2. Inner layer: Task scheduling (EnhancedEPOCEIS)
 */
public class HFCOWithPlacement {
    
    private List<FogNode> allNodes;
    private Workflow workflow;
    private HFCOFogPlacementOptimizer fogOptimizer;
    private EnhancedEPOCEIS taskScheduler;
    
    public HFCOWithPlacement(List<FogNode> allNodes, Workflow workflow) {
        this.allNodes = allNodes;
        this.workflow = workflow;
        this.fogOptimizer = new HFCOFogPlacementOptimizer(allNodes, workflow);
    }
    
    /**
     * Main optimization method: Two-phase approach
     * Phase 1: Optimize fog deployment (ζ*)
     * Phase 2: Optimize task scheduling with selected nodes
     */
    public HFCOResult optimize() {
        System.out.println("🔄 Starting HFCO optimization...");
        
        // Phase 1: Fog Deployment Optimization
        System.out.println("📡 Phase 1: Optimizing fog deployment...");
        HFCOFogPlacementOptimizer.FogDeploymentResult fogResult = 
            fogOptimizer.optimizeFogDeployment();
        
        System.out.println("✅ Fog deployment optimized:");
        System.out.println("   - Active fog nodes: " + fogResult.activeFogNodes.size());
        System.out.println("   - Total active nodes: " + fogResult.allActiveNodes.size());
        System.out.println("   - Deployment cost: " + String.format("%.2f", fogResult.deploymentCost));
        
        // Phase 2: Task Scheduling with optimized nodes
        System.out.println("⚡ Phase 2: Optimizing task scheduling...");
        taskScheduler = new EnhancedEPOCEIS(workflow, fogResult.allActiveNodes);
        EnhancedEPOCEIS.SchedulingResult schedulingResult = taskScheduler.schedule();
        
        System.out.println("✅ Task scheduling optimized:");
        System.out.println("   - Total cost: " + String.format("%.2f", schedulingResult.totalCost));
        System.out.println("   - Tasks assigned: " + schedulingResult.taskAssignments.size());
        
        // Calculate comprehensive metrics
        ComprehensiveMetrics metrics = calculateComprehensiveMetrics(
            fogResult, schedulingResult, fogResult.allActiveNodes);
        
        return new HFCOResult(fogResult, schedulingResult, metrics);
    }
    
    /**
     * Calculate comprehensive performance metrics
     */
    private ComprehensiveMetrics calculateComprehensiveMetrics(
            HFCOFogPlacementOptimizer.FogDeploymentResult fogResult,
            EnhancedEPOCEIS.SchedulingResult schedulingResult,
            List<FogNode> activeNodes) {
        
        // Calculate deadline hit rate
        double deadlineHitRate = calculateDeadlineHitRate(schedulingResult);
        
        // Calculate makespan
        double makespan = calculateMakespan(schedulingResult);
        
        // Calculate average latency
        double avgLatency = calculateAverageLatency(activeNodes);
        
        // Calculate energy consumption
        double energyConsumption = calculateEnergyConsumption(schedulingResult, activeNodes);
        
        // Calculate utilization
        double fogUtilization = calculateFogUtilization(schedulingResult, fogResult.activeFogNodes);
        double cloudUtilization = calculateCloudUtilization(schedulingResult, getCloudNodes(activeNodes));
        
        return new ComprehensiveMetrics(
            deadlineHitRate, makespan, avgLatency, energyConsumption,
            fogUtilization, cloudUtilization
        );
    }
    
    /**
     * Calculate deadline hit rate
     */
    private double calculateDeadlineHitRate(EnhancedEPOCEIS.SchedulingResult schedulingResult) {
        int totalTasks = workflow.getAllTasks().size();
        int deadlineHits = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int nodeId = schedulingResult.taskAssignments.get(task.getId());
            FogNode node = getNodeById(nodeId);
            double startTime = schedulingResult.startTimes.get(task.getId());
            
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
     * Calculate makespan (total execution time)
     */
    private double calculateMakespan(EnhancedEPOCEIS.SchedulingResult schedulingResult) {
        double maxFinishTime = 0.0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int nodeId = schedulingResult.taskAssignments.get(task.getId());
            FogNode node = getNodeById(nodeId);
            double startTime = schedulingResult.startTimes.get(task.getId());
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double latency = node.getLatencyMs() / 1000.0;
            double finishTime = startTime + execTime + transferDelay + latency;
            
            maxFinishTime = Math.max(maxFinishTime, finishTime);
        }
        
        return maxFinishTime;
    }
    
    /**
     * Calculate average latency across active nodes
     */
    private double calculateAverageLatency(List<FogNode> activeNodes) {
        double totalLatency = 0.0;
        for (FogNode node : activeNodes) {
            totalLatency += node.getLatencyMs();
        }
        return totalLatency / activeNodes.size();
    }
    
    /**
     * Calculate total energy consumption
     */
    private double calculateEnergyConsumption(EnhancedEPOCEIS.SchedulingResult schedulingResult, 
                                           List<FogNode> activeNodes) {
        double totalEnergy = 0.0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int nodeId = schedulingResult.taskAssignments.get(task.getId());
            FogNode node = getNodeById(nodeId);
            
            double execTime = CostCalculator.calculateExecutionTime(task, node);
            double energy = execTime * node.getEnergyPerSec();
            totalEnergy += energy;
        }
        
        return totalEnergy;
    }
    
    /**
     * Calculate fog node utilization
     */
    private double calculateFogUtilization(EnhancedEPOCEIS.SchedulingResult schedulingResult,
                                         List<FogNode> fogNodes) {
        if (fogNodes.isEmpty()) return 0.0;
        
        Map<Integer, Double> nodeBusyTime = new HashMap<>();
        for (FogNode node : fogNodes) {
            nodeBusyTime.put(node.getId(), 0.0);
        }
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int nodeId = schedulingResult.taskAssignments.get(task.getId());
            if (getNodeById(nodeId).isCloud()) continue; // Skip cloud tasks
            
            FogNode node = getNodeById(nodeId);
            double startTime = schedulingResult.startTimes.get(task.getId());
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
        
        double makespan = calculateMakespan(schedulingResult);
        return totalBusyTime / (makespan * fogNodes.size());
    }
    
    /**
     * Calculate cloud node utilization
     */
    private double calculateCloudUtilization(EnhancedEPOCEIS.SchedulingResult schedulingResult,
                                           List<FogNode> cloudNodes) {
        if (cloudNodes.isEmpty()) return 0.0;
        
        Map<Integer, Double> nodeBusyTime = new HashMap<>();
        for (FogNode node : cloudNodes) {
            nodeBusyTime.put(node.getId(), 0.0);
        }
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int nodeId = schedulingResult.taskAssignments.get(task.getId());
            if (!getNodeById(nodeId).isCloud()) continue; // Skip fog tasks
            
            FogNode node = getNodeById(nodeId);
            double startTime = schedulingResult.startTimes.get(task.getId());
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
        
        double makespan = calculateMakespan(schedulingResult);
        return totalBusyTime / (makespan * cloudNodes.size());
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
        return allNodes.get(0); // fallback
    }
    
    private List<FogNode> getCloudNodes(List<FogNode> activeNodes) {
        List<FogNode> cloudNodes = new ArrayList<>();
        for (FogNode node : activeNodes) {
            if (node.isCloud()) {
                cloudNodes.add(node);
            }
        }
        return cloudNodes;
    }
    
    /**
     * Result class containing all optimization results
     */
    public static class HFCOResult {
        public final HFCOFogPlacementOptimizer.FogDeploymentResult fogResult;
        public final EnhancedEPOCEIS.SchedulingResult schedulingResult;
        public final ComprehensiveMetrics metrics;
        
        public HFCOResult(HFCOFogPlacementOptimizer.FogDeploymentResult fogResult,
                         EnhancedEPOCEIS.SchedulingResult schedulingResult,
                         ComprehensiveMetrics metrics) {
            this.fogResult = fogResult;
            this.schedulingResult = schedulingResult;
            this.metrics = metrics;
        }
        
        public void printSummary() {
            System.out.println("\n🎯 HFCO Optimization Results Summary:");
            System.out.println("=====================================");
            System.out.println("📡 Fog Deployment:");
            System.out.println("   - Active fog nodes: " + fogResult.activeFogNodes.size());
            System.out.println("   - Deployment cost: " + String.format("%.2f", fogResult.deploymentCost));
            System.out.println("   - Total active nodes: " + fogResult.allActiveNodes.size());
            
            System.out.println("\n⚡ Task Scheduling:");
            System.out.println("   - Total cost: " + String.format("%.2f", schedulingResult.totalCost));
            System.out.println("   - Tasks assigned: " + schedulingResult.taskAssignments.size());
            
            System.out.println("\n📊 Performance Metrics:");
            System.out.println("   - Deadline hit rate: " + String.format("%.2f%%", metrics.deadlineHitRate * 100));
            System.out.println("   - Makespan: " + String.format("%.2f s", metrics.makespan));
            System.out.println("   - Average latency: " + String.format("%.2f ms", metrics.avgLatency));
            System.out.println("   - Energy consumption: " + String.format("%.2f J", metrics.energyConsumption));
            System.out.println("   - Fog utilization: " + String.format("%.2f%%", metrics.fogUtilization * 100));
            System.out.println("   - Cloud utilization: " + String.format("%.2f%%", metrics.cloudUtilization * 100));
        }
    }
    
    /**
     * Comprehensive metrics class
     */
    public static class ComprehensiveMetrics {
        public final double deadlineHitRate;
        public final double makespan;
        public final double avgLatency;
        public final double energyConsumption;
        public final double fogUtilization;
        public final double cloudUtilization;
        
        public ComprehensiveMetrics(double deadlineHitRate, double makespan, double avgLatency,
                                 double energyConsumption, double fogUtilization, double cloudUtilization) {
            this.deadlineHitRate = deadlineHitRate;
            this.makespan = makespan;
            this.avgLatency = avgLatency;
            this.energyConsumption = energyConsumption;
            this.fogUtilization = fogUtilization;
            this.cloudUtilization = cloudUtilization;
        }
    }
}
