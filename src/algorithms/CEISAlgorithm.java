package algorithms;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;

import java.util.*;

public class CEISAlgorithm {

    private Workflow workflow;
    private List<FogNode> fogNodes;
    private Map<Integer, Integer> taskToNodeMap;
    private double totalCost = 0.0;
    private static final double PENALTY_M = 1000.0; // مقدار پیش‌فرض برای جریمه نقض ددلاین

    public CEISAlgorithm(Workflow workflow, List<FogNode> fogNodes) {
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
            FogNode bestNode = null;
            double bestStartTime = -1;
            double bestCost = Double.MAX_VALUE;

            for (FogNode node : fogNodes) {
                double readyTime = workflow.getReadyTime(task, node, nodeAvailableTime);
                double cost = CostCalculator.calculateTotalCost(task, node, readyTime, PENALTY_M);

                if (cost < bestCost) {
                    bestCost = cost;
                    bestNode = node;
                    bestStartTime = readyTime;
                }
            }

            // ثبت تخصیص و زمان‌بندی نهایی
            taskToNodeMap.put(task.getId(), bestNode.getId());

            double execTime = CostCalculator.calculateExecutionTime(task, bestNode);
            double transferDelay = CostCalculator.calculateTransferDelay(task, bestNode) + bestNode.getLatencyMs() / 1000.0;
            double finishTime = bestStartTime + execTime + transferDelay;

            task.setStartTime(bestStartTime);
            task.setFinishTime(finishTime);

            nodeAvailableTime.put(bestNode.getId(), finishTime);
            totalCost += bestCost;
        }
    }

    public Map<Integer, Integer> getTaskAssignments() {
        return taskToNodeMap;
    }

    public double getTotalCost() {
        return totalCost;
    }
}