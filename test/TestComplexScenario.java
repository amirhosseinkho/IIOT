package test;

import core.Workflow;
import core.FogNode;
import simulation.WorkflowParser;
import simulation.NodeParser;
import algorithms.CEISAlgorithm;
import algorithms.HFCOAlgorithm;

import java.util.*;

public class TestComplexScenario {

    public static void main(String[] args) {
        try {
            Workflow workflow = WorkflowParser.parseFromFile("data/workflow.txt");
            List<FogNode> nodes = NodeParser.parseFromFile("data/nodes.txt");

            CEISAlgorithm ceis = new CEISAlgorithm(workflow, nodes);
            ceis.schedule();
            double ceisCost = ceis.getTotalCost();
            Map<Integer, Integer> ceisAssign = ceis.getTaskAssignments();

            workflow = WorkflowParser.parseFromFile("data/workflow.txt");
            HFCOAlgorithm hfco = new HFCOAlgorithm(workflow, nodes);
            hfco.schedule();
            double hfcoCost = hfco.getTotalCost();
            Map<Integer, Integer> hfcoAssign = hfco.getTaskAssignments();

            System.out.println(" CEIS هزینه کل: " + ceisCost);
            System.out.println(" HFCO هزینه کل: " + hfcoCost);

            System.out.println("\n CEIS نگاشت:");
            for (Map.Entry<Integer, Integer> e : ceisAssign.entrySet()) {
                System.out.println("• تسک " + e.getKey() + " → نود " + e.getValue());
            }

            System.out.println("\n HFCO نگاشت:");
            for (Map.Entry<Integer, Integer> e : hfcoAssign.entrySet()) {
                System.out.println("• تسک " + e.getKey() + " → نود " + e.getValue());
            }

        } catch (Exception e) {
            System.out.println(" خطا در تست سناریوی پیچیده: " + e.getMessage());
        }
    }
}
