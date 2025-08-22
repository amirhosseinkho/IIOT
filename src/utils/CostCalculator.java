package utils;

import core.IIoTTask;
import core.FogNode;

public class CostCalculator {

    public static double calculateExecutionTime(IIoTTask task, FogNode node) {
        return task.getLength() / (double) node.getMips();
    }

    public static double calculateTransferDelay(IIoTTask task, FogNode node) {
        double fileSizeMB = task.getFileSize();  // MB
        double bwMBps = node.getBw();            // MB/s

        if (bwMBps == 0) return Double.MAX_VALUE;
        return fileSizeMB / bwMBps;
    }

    public static double calculateEnergyConsumption(IIoTTask task, FogNode node) {
        double execTime = calculateExecutionTime(task, node); // ثانیه
        double power = node.getEnergyPerSec(); // وات
        return execTime * power; // ژول
    }





    public static double calculateTotalCost(IIoTTask task, FogNode node, double startTime, double penaltyM) {
        double execTime = calculateExecutionTime(task, node);
        double transferDelay = calculateTransferDelay(task, node);
        double latency = node.getLatencyMs() / 1000.0;

        double finishTime = startTime + execTime + transferDelay + latency;
        double duration = execTime + transferDelay + latency;

        double cost = duration * node.getCostPerSec();

        // جریمه عبور از deadline
        double penalty = 0.0;
        if (finishTime > task.getDeadline()) {
            penalty = (finishTime - task.getDeadline()) * penaltyM;
        }

        return cost + penalty;
    }
}
