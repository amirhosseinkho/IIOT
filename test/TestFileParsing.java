package test;

import core.FogNode;
import core.Workflow;
import simulation.WorkflowParser;
import simulation.NodeParser;

import java.util.List;

public class TestFileParsing {

    public static void main(String[] args) {
        try {
            Workflow workflow = WorkflowParser.parseFromFile("data/workflow.txt");
            List<FogNode> nodes = NodeParser.parseFromFile("data/nodes.txt");

            System.out.println(" تعداد وظایف: " + workflow.getAllTasks().size());
            System.out.println(" تعداد نودها: " + nodes.size());

            if (workflow.getAllTasks().size() > 0 && nodes.size() > 0) {
                System.out.println(" تست فایل‌ها موفق بود.");
            } else {
                System.out.println(" تست فایل‌ها شکست خورد.");
            }

        } catch (Exception e) {
            System.out.println(" خطا در تست فایل: " + e.getMessage());
        }
    }
}
