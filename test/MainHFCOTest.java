package test;

import core.*;
import algorithms.*;
import simulation.WorkflowParser;

import java.util.*;

public class MainHFCOTest {

    public static void main(String[] args) throws Exception {
        //  ساخت workflow
        Workflow wf = WorkflowParser.parseFromFile("C:\\Users\\OMEN\\Desktop\\IIoT-Scheduler\\data\\scenario_1\\workflow.txt");

        // 📍 تولید مکان‌های کاندید
        List<Location> candidateSites = generateCandidateSites(20);

        // 🔁 اجرای HFCO
        HFCOOptimizer optimizer = new HFCOOptimizer(candidateSites);
        List<FogNode> fogNodes = optimizer.optimisePlacement(wf);

        // ☁️ افزودن نود کلاد
        FogNode cloud = new FogNode(99, 20000, 16000, 10000, 100000, true, 0.004);
        cloud.setLatencyMs(80);
        cloud.setPower(25);
        fogNodes.add(cloud);

        // 🧬 اجرای CEIS
        EPOBasedCEIS ceis = new EPOBasedCEIS(wf, fogNodes);
        Chromosome best = ceis.run();

        System.out.println("Best Cost: " + best.getFitness());
        System.out.println("Missed Deadlines: " + best.getMissedDeadlines());
        System.out.println("Total Energy: " + best.getEnergy());
    }

    // 📍 تابع تولید مکان‌ها
    public static List<Location> generateCandidateSites(int count) {
        List<Location> sites = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            double x = rand.nextDouble() * 100;
            double y = rand.nextDouble() * 100;
            double latencyMs = rand.nextDouble() * 10 + 1; // بین 1 تا 10 میلی‌ثانیه

            sites.add(new Location(i, x, y, latencyMs));
        }
        return sites;
    }

}
