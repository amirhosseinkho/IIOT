package simulation;

import algorithms.*;
import core.FogNode;
import core.IIoTTask;
import core.Workflow;

import java.util.ArrayList;
import java.util.List;

public class MainHFCO {

    public static void main(String[] args) throws Exception {
        // مرحله ۱: خواندن ورودی‌ها
        String workflowPath = "input/workflow.txt";
        String nodesPath = "input/nodes.txt";

        Workflow workflow = WorkflowParser.parseFromFile(workflowPath);
        List<FogNode> cloudNodes = NodeParser.parseFromFile(nodesPath);

        // مرحله ۲: تولید سایت‌های کاندید (۲۰ مکان فرضی)
        List<Location> candidateSites = LocationGenerator.generateCandidateSites(20);

        // مرحله ۳: اجرای HFCO برای انتخاب بهترین siteهای fog
        HFCOOptimizer hfco = new HFCOOptimizer(candidateSites);
        List<FogNode> selectedFogNodes = hfco.optimisePlacement(workflow);

        // ترکیب نودهای cloud و fog
        List<FogNode> allNodes = new ArrayList<>();
        allNodes.addAll(cloudNodes);
        allNodes.addAll(selectedFogNodes);

        // مرحله ۴: اجرای CEIS
        EPOBasedCEIS scheduler = new EPOBasedCEIS(workflow, allNodes);
        Chromosome bestSchedule = scheduler.run();

        // مرحله ۵: گزارش
        System.out.println("✅ HFCO + CEIS scheduling complete.");
        System.out.println("Total cost: " + bestSchedule.getFitness());
        System.out.println("Energy consumed: " + bestSchedule.getEnergy() + " J");
        System.out.println("Missed deadlines: " + bestSchedule.getMissedDeadlines());

        for (IIoTTask task : workflow.getAllTasks()) {
            System.out.printf("Task %d ➜ Start: %.2f, Finish: %.2f, On Node: %d%n",
                    task.getId(), task.getStartTime(), task.getFinishTime(), bestSchedule.getDevice(task.getId()));
        }
    }
}
