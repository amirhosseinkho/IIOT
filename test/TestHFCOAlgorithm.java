package test;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import algorithms.HFCOAlgorithm;

import java.util.*;

public class TestHFCOAlgorithm {

    public static void main(String[] args) {
        Workflow workflow = new Workflow();

        workflow.addTask(new IIoTTask(0, 1000, 100, 100, 1, 0.5));
        workflow.addTask(new IIoTTask(1, 2000, 100, 100, 1, 0.5));
        workflow.addDependency(0, 1);

        List<FogNode> nodes = new ArrayList<>();
        nodes.add(new FogNode(0, 800, 1024, 1000, 10000, false, 0.005));  // Fog
        nodes.add(new FogNode(1, 2000, 4096, 2000, 20000, true, 0.02));  // Cloud

        HFCOAlgorithm algorithm = new HFCOAlgorithm(workflow, nodes);
        algorithm.schedule();

        Map<Integer, Integer> assignments = algorithm.getTaskAssignments();
        double cost = algorithm.getTotalCost();

        System.out.println(" تست HFCO:");
        for (Map.Entry<Integer, Integer> entry : assignments.entrySet()) {
            System.out.println("وظیفه " + entry.getKey() + " روی نود " + entry.getValue());
        }
        System.out.println(" هزینه کل: " + cost);

        if (assignments.size() == 2 && cost > 0) {
            System.out.println(" تست با موفقیت انجام شد.");
        } else {
            System.out.println(" تست شکست خورد.");
        }
    }
}
