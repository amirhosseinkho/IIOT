package test;

import core.Workflow;
import core.FogNode;
import simulation.WorkflowParser;
import simulation.NodeParser;
import algorithms.CEISAlgorithm;
import algorithms.HFCOAlgorithm;

import java.util.*;

public class TestComparison {

    public static void main(String[] args) {
        try {
            Workflow workflow = WorkflowParser.parseFromFile("data/workflow.txt");
            List<FogNode> nodes = NodeParser.parseFromFile("data/nodes.txt");

            CEISAlgorithm ceis = new CEISAlgorithm(workflow, nodes);
            ceis.schedule();
            double cost1 = ceis.getTotalCost();

            // دوباره workflow رو بخونیم چون قبلی مصرف شده
            workflow = WorkflowParser.parseFromFile("data/workflow.txt");
            HFCOAlgorithm hfco = new HFCOAlgorithm(workflow, nodes);
            hfco.schedule();
            double cost2 = hfco.getTotalCost();

            System.out.println(" CEIS هزینه: " + cost1);
            System.out.println(" HFCO هزینه: " + cost2);

            if (cost2 <= cost1) {
                System.out.println(" HFCO بهینه‌تر یا مساوی CEIS است.");
            } else {
                System.out.println(" HFCO هزینه بیشتری دارد (ممکن است به دلیل delay یا تخصیص باشد).");
            }

        } catch (Exception e) {
            System.out.println(" خطا در مقایسه الگوریتم‌ها: " + e.getMessage());
        }
    }
}
