package algorithms;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;

import java.util.*;

public class HFCOAlgorithm {

    private Workflow workflow;
    private List<FogNode> fogNodes;
    private Map<Integer, Integer> taskToNodeMap;
    private double totalCost = 0.0;
    private static final double PENALTY_M = 1000.0;

    public HFCOAlgorithm(Workflow workflow, List<FogNode> fogNodes) {
        this.workflow = workflow;
        this.fogNodes = fogNodes;
        this.taskToNodeMap = new HashMap<>();
    }

    public void schedule() {
        Set<Integer> completedTasks = new HashSet<>();
        Map<Integer, Double> nodeAvailableTime = new HashMap<>();
        for (FogNode node : fogNodes) nodeAvailableTime.put(node.getId(), 0.0);

        List<IIoTTask> sortedTasks = workflow.getTopologicallySortedTasks();

        for (IIoTTask task : sortedTasks) {
            FogNode selectedNode = selectNodeByHeuristic(task);

            double readyTime = workflow.getReadyTime(task, selectedNode, nodeAvailableTime);
            double cost = CostCalculator.calculateTotalCost(task, selectedNode, readyTime, PENALTY_M);

            taskToNodeMap.put(task.getId(), selectedNode.getId());

            double execTime = CostCalculator.calculateExecutionTime(task, selectedNode);
            double transferDelay = CostCalculator.calculateTransferDelay(task, selectedNode) + selectedNode.getLatencyMs() / 1000.0;
            double finishTime = readyTime + execTime + transferDelay;

            task.setStartTime(readyTime);
            task.setFinishTime(finishTime);

            nodeAvailableTime.put(selectedNode.getId(), finishTime);
            totalCost += cost;
        }
    }

    private FogNode selectNodeByHeuristic(IIoTTask task) {
        FogNode bestFog = null;
        FogNode bestCloud = null;

        for (FogNode node : fogNodes) {
            if (node.isCloud()) {
                if (bestCloud == null || node.getCostPerSec() < bestCloud.getCostPerSec()) {
                    bestCloud = node;
                }
            } else {
                if (node.getMips() >= task.getLength() / 2 &&
                        (bestFog == null || node.getCostPerSec() < bestFog.getCostPerSec())) {
                    bestFog = node;
                }
            }
        }

        return bestFog != null ? bestFog : bestCloud;
    }

    public Map<Integer, Integer> getTaskAssignments() {
        return taskToNodeMap;
    }

    public double getTotalCost() {
        return totalCost;
    }
}