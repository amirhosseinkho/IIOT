package test;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import algorithms.CEISAlgorithm;

import java.util.*;

public class TestCEISAlgorithm {

    public static void main(String[] args) {
        // ساخت یک workflow ساده برای تست
        Workflow workflow = new Workflow();
        workflow.addTask(new IIoTTask(0, 3000, 200, 200, 1, 0.5));
        workflow.addTask(new IIoTTask(1, 1500, 100, 100, 1, 0.5));
        workflow.addDependency(0, 1); // task 1 بعد از task 0

        // تعریف دو نود با مشخصات متفاوت
        List<FogNode> nodes = new ArrayList<>();
        nodes.add(new FogNode(0, 1000, 1024, 1000, 5000, false, 0.01));  // Fog
        nodes.add(new FogNode(1, 2000, 2048, 2000, 10000, true, 0.025)); // Cloud

        // اجرای الگوریتم CEIS
        CEISAlgorithm ceis = new CEISAlgorithm(workflow, nodes);
        ceis.schedule();

        // دریافت نگاشت‌ها و هزینه نهایی
        Map<Integer, Integer> assignments = ceis.getTaskAssignments();
        double totalCost = ceis.getTotalCost();

        // چاپ خروجی
        System.out.println(" تست CEIS:");
        for (Map.Entry<Integer, Integer> entry : assignments.entrySet()) {
            System.out.println("وظیفه " + entry.getKey() + " روی نود " + entry.getValue());
        }
        System.out.println(" هزینه کل اجرا: " + totalCost);

        // بررسی ساده صحت تست
        if (assignments.size() == 2 && totalCost > 0) {
            System.out.println("✅ تست با موفقیت انجام شد.");
        } else {
            System.out.println(" تست شکست خورد.");
        }
    }
}
