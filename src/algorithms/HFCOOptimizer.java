package algorithms;

import core.FogNode;
import core.IIoTTask;
import core.Workflow;

import java.util.*;

public class HFCOOptimizer {

    private List<Location> candidateSites;   // لیست مکان‌های کاندید
    private int budget = 5;                  // حداکثر تعداد نود
    private int populationSize = 50;
    private int maxGenerations = 100;
    private Random rand = new Random();

    public HFCOOptimizer(List<Location> candidateSites) {
        this.candidateSites = candidateSites;
    }

    public List<FogNode> optimisePlacement(Workflow workflow) {
        List<boolean[]> population = initialisePopulation();
        boolean[] bestPlacement = null;
        double bestFitness = Double.MAX_VALUE;

        for (int gen = 0; gen < maxGenerations; gen++) {
            List<boolean[]> newPop = new ArrayList<>();

            for (boolean[] indiv : population) {
                boolean[] mutated = mutate(indiv);
                if (fitness(mutated, workflow) < fitness(indiv, workflow)) {
                    newPop.add(mutated);
                } else {
                    newPop.add(indiv);
                }
            }

            boolean[] genBest = getBest(newPop, workflow);
            double genFit = fitness(genBest, workflow);

            if (genFit < bestFitness) {
                bestFitness = genFit;
                bestPlacement = genBest;
            }

            population = newPop;
        }

        return deployFogNodes(bestPlacement);
    }

    private List<boolean[]> initialisePopulation() {
        List<boolean[]> pop = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            boolean[] bitVector = new boolean[candidateSites.size()];
            int count = 0;
            while (count < budget) {
                int idx = rand.nextInt(candidateSites.size());
                if (!bitVector[idx]) {
                    bitVector[idx] = true;
                    count++;
                }
            }
            pop.add(bitVector);
        }
        return pop;
    }

    private boolean[] mutate(boolean[] original) {
        boolean[] copy = Arrays.copyOf(original, original.length);

        int onCount = 0;
        for (boolean b : copy) if (b) onCount++;

        // سوییچ یک بیت
        int idx = rand.nextInt(copy.length);
        copy[idx] = !copy[idx];

        // اصلاح برای حفظ محدودیت بودجه
        if (onCount > budget) {
            for (int i = 0; i < copy.length && onCount > budget; i++) {
                if (copy[i]) {
                    copy[i] = false;
                    onCount--;
                }
            }
        }

        return copy;
    }

    private double fitness(boolean[] placement, Workflow workflow) {
        List<FogNode> deployed = deployFogNodes(placement);
        EPOBasedCEIS ceis = new EPOBasedCEIS(workflow, deployed);
        Chromosome solution = ceis.run();

        double cost = solution.getFitness();
        double latency = averageLatency(solution, workflow, deployed);

        return 0.5 * cost + 0.5 * latency;  // 🎯 ترکیب وزنی
    }

    private boolean[] getBest(List<boolean[]> pop, Workflow workflow) {
        return Collections.min(pop, Comparator.comparingDouble(p -> fitness(p, workflow)));
    }

    private List<FogNode> deployFogNodes(boolean[] placement) {
        List<FogNode> result = new ArrayList<>();
        for (int i = 0; i < placement.length; i++) {
            if (placement[i]) {
                Location loc = candidateSites.get(i);
                FogNode node = new FogNode(loc.getId(), 5000, 0.002, 5, 5);  // فرضی
                node.setPosition(loc.getX(), loc.getY());

                result.add(node);
            }
        }
        return result;
    }

    private double averageLatency(Chromosome chrom, Workflow workflow, List<FogNode> nodes) {
        double totalLatency = 0.0;
        int taskCount = 0;

        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            int nodeId = chrom.getDevice(taskId);
            if (nodeId >= 0 && nodeId < nodes.size()) {
                FogNode node = nodes.get(nodeId);
                totalLatency += node.getLatencyMs(); // فرض بر اینه که latencyMs در FogNode هست
                taskCount++;
            }
        }

        return taskCount == 0 ? 0.0 : totalLatency / taskCount;
    }

}
