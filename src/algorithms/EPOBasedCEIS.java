package algorithms;

import core.IIoTTask;
import core.FogNode;
import core.Workflow;
import utils.CostCalculator;

import java.util.*;
import java.util.stream.Collectors;

public class EPOBasedCEIS {

    private Workflow workflow;
    private List<FogNode> nodes;
    private int populationSize = 100;
    private int maxGenerations = 200;
    private double penaltyM = 1000.0;
    private static final Random rand = new Random(); // خارج از متدها

    public EPOBasedCEIS(Workflow workflow, List<FogNode> nodes) {
        this.workflow = workflow;
        this.nodes = nodes;
    }

    public Chromosome run() {
        List<Chromosome> population = initializePopulation();
        Chromosome globalBest = getBest(population);

        for (int gen = 0; gen < maxGenerations; gen++) {
            List<Chromosome> nextGen = new ArrayList<>();

            for (Chromosome individual : population) {
                boolean explore = rand.nextDouble() < 0.5;

                Chromosome offspring;
                if (explore) {
                    if (rand.nextDouble() < 0.5) {
                        offspring = randomJump(individual);
                    } else {
                        offspring = socialForage(individual, population);
                    }
                } else {
                    offspring = sprint(individual);
                }

                repair(offspring);

                // ❗ این خط رو اضافه کن:
                if (fitness(offspring) < fitness(individual)) {
                    nextGen.add(offspring);
                } else {
                    nextGen.add(individual);
                }
            }



            population = nextGen;
            Chromosome bestInGen = getBest(population);
            if (fitness(bestInGen) < fitness(globalBest)) {
                globalBest = bestInGen;
            }

            // 🔁 Elite Local Hill-Climb
            List<Chromosome> elites = getTopK(population, 5);
            for (Chromosome elite : elites) {
                Chromosome swapped = swapTwoTasks(elite);
                repair(swapped);
                if (fitness(swapped) < fitness(elite)) {
                    replaceInPopulation(population, elite, swapped);
                }
            }
        }

        return globalBest;
    }


    private List<Chromosome> initializePopulation() {
        List<Chromosome> pop = new ArrayList<>();

        int half = populationSize / 2;
        for (int i = 0; i < half; i++) {
            Chromosome orig = Chromosome.random(workflow, nodes);
            repair(orig);
            pop.add(orig);

            Chromosome opp = new Chromosome(orig.getDeviceAssignments().length);
            for (int t = 0; t < orig.getDeviceAssignments().length; t++) {
                int maxDeviceIndex = nodes.size() - 1;
                int oppDevice = maxDeviceIndex - orig.getDeviceForTask(t);
                int oppStart = 50 - orig.getStartSlotForTask(t); // فرضاً بازه زمان‌بندی 0 تا 50

                opp.setAssignment(t, oppDevice, Math.max(0, oppStart));
            }
            repair(opp);
            pop.add(opp);
        }

        return pop;
    }


    private Chromosome mutate(Chromosome original) {
        Chromosome copy = original.copy();
        List<IIoTTask> tasks = new ArrayList<>(workflow.getAllTasks());
        int taskId = rand.nextInt(tasks.size());
        IIoTTask task = tasks.get(taskId);

        int currentDevice = original.getDevice(taskId);
        int currentStart = original.getStartSlot(taskId);

        FogNode currentNode = nodes.get(currentDevice);
        double execTime = CostCalculator.calculateExecutionTime(task, currentNode);
        double transferDelay = CostCalculator.calculateTransferDelay(task, currentNode);
        double finish = currentStart + execTime + transferDelay;

        boolean isLate = finish > task.getDeadline();
        boolean isHighEnergy = currentNode.getPower() > 20; // مثلاً فرضی

        int newDevice = currentDevice;
        int newStart = currentStart;

        // 🎯 شرایط adaptive
        if (isLate) {
            // سعی کن زمان شروع رو کاهش بدی
            newStart = Math.max(0, currentStart - rand.nextInt(5));
        }

        if (isHighEnergy) {
            // بگرد دنبال node با مصرف پایین‌تر
            List<Integer> lowEnergyNodes = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i).getPower() < currentNode.getPower()) {
                    lowEnergyNodes.add(i);
                }
            }

            if (!lowEnergyNodes.isEmpty()) {
                newDevice = lowEnergyNodes.get(rand.nextInt(lowEnergyNodes.size()));
            }
        }

        // احتمال کمی برای mutation تصادفی جهت تنوع
        if (rand.nextDouble() < 0.1) {
            newDevice = rand.nextInt(nodes.size());
            newStart = rand.nextInt(50);
        }

        copy.setAssignment(taskId, newDevice, newStart);
        return copy;
    }


    private void repair(Chromosome chrom) {
        List<IIoTTask> sortedTasks = topologicalSort();

        for (IIoTTask task : sortedTasks) {
            int taskId = task.getId();
            int currentNodeIndex = chrom.getDevice(taskId);
            int currentStartSlot = chrom.getStartSlot(taskId);

            FogNode currentNode = nodes.get(currentNodeIndex);
            double execTime = CostCalculator.calculateExecutionTime(task, currentNode);
            double transfer = CostCalculator.calculateTransferDelay(task, currentNode);
            double latency = currentNode.getLatencyMs() / 1000.0;
            double finishTime = currentStartSlot + execTime + transfer + latency;

            // اگر deadline رعایت نشده
            if (finishTime > task.getDeadline()) {
                double bestCost = Double.MAX_VALUE;
                int bestNodeIndex = currentNodeIndex;
                int bestStart = currentStartSlot;

                for (int i = 0; i < nodes.size(); i++) {
                    FogNode altNode = nodes.get(i);
                    double et = CostCalculator.calculateExecutionTime(task, altNode);
                    double td = CostCalculator.calculateTransferDelay(task, altNode);
                    double lt = altNode.getLatencyMs() / 1000.0;
                    double totalTime = et + td + lt;

                    int newStartSlot = 0; // ⏱️ میشه هوشمند کرد بعداً

                    if (newStartSlot + totalTime <= task.getDeadline()) {
                        double cost = totalTime * altNode.getCostPerSec();
                        if (cost < bestCost) {
                            bestCost = cost;
                            bestNodeIndex = i;
                            bestStart = newStartSlot;
                        }
                    }
                }

                // اگر نودی پیدا شد که deadline رو رعایت می‌کرد
                if (bestCost < Double.MAX_VALUE) {
                    chrom.setAssignment(taskId, bestNodeIndex, bestStart);
                }
            }
        }
    }


    private double fitness(Chromosome chrom) {
        double totalCost = 0;
        double totalEnergy = 0;
        int missedDeadlines = 0;

        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            int nodeIndex = chrom.getDevice(taskId);
            int startSlot = chrom.getStartSlot(taskId);

            if (nodeIndex < 0 || nodeIndex >= nodes.size()) return Double.MAX_VALUE;
            FogNode node = nodes.get(nodeIndex);

            double execTime = CostCalculator.calculateExecutionTime(task, node);
            if (execTime <= 0) return Double.MAX_VALUE;

            double transferDelay = CostCalculator.calculateTransferDelay(task, node);
            double finish = startSlot + execTime + transferDelay;

            if (Double.isNaN(finish) || Double.isInfinite(finish)) return Double.MAX_VALUE;

            task.setStartTime(startSlot);
            task.setFinishTime(finish);

            totalCost += node.getCostPerSec() * execTime;

            double taskEnergy = CostCalculator.calculateEnergyConsumption(task, node);
            totalEnergy += taskEnergy;
            task.setEnergyConsumption(taskEnergy); // (اختیاری برای تحلیل)

            if (finish > task.getDeadline()) {
                missedDeadlines++;
                totalCost += penaltyM * (finish - task.getDeadline());
            }
        }

        chrom.setEnergy(totalEnergy);
        chrom.setMissedDeadlines(missedDeadlines);
        chrom.setFitness(totalCost);  // 👈 این خط رو **در انتهای متد** بذار

        return totalCost;
    }

    private Chromosome sprint(Chromosome original) {
        Chromosome copy = original.copy();
        int taskId = rand.nextInt(workflow.getAllTasks().size());

        int currentDevice = copy.getDevice(taskId);
        int neighborDevice = (currentDevice + 1) % nodes.size(); // رفتن به همسایه

        copy.setDeviceForTask(taskId, neighborDevice);
        return copy;
    }

    private Chromosome swapTwoTasks(Chromosome c) {
        Chromosome copy = c.copy();
        int size = copy.getDeviceAssignments().length;

        int i = rand.nextInt(size);
        int j;
        do {
            j = rand.nextInt(size);
        } while (i == j); // دو task متفاوت

        // فقط device و start رو عوض می‌کنیم
        int devI = copy.getDevice(i);
        int devJ = copy.getDevice(j);
        int startI = copy.getStartSlot(i);
        int startJ = copy.getStartSlot(j);

        copy.setAssignment(i, devJ, startJ);
        copy.setAssignment(j, devI, startI);

        return copy;
    }

    private List<Chromosome> getTopK(List<Chromosome> pop, int k) {
        return pop.stream()
                .sorted(Comparator.comparingDouble(this::fitness))
                .limit(k)
                .collect(Collectors.toList());
    }

    private void replaceInPopulation(List<Chromosome> pop, Chromosome oldOne, Chromosome newOne) {
        int idx = pop.indexOf(oldOne);
        if (idx != -1) {
            pop.set(idx, newOne);
        }
    }


    private Chromosome randomJump(Chromosome c) {
        Chromosome copy = c.copy();
        int size = copy.getDeviceAssignments().length;

        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() < 0.2) { // فقط برخی ژن‌ها تغییر کنن
                int randomDevice = rand.nextInt(nodes.size());
                int randomStart = rand.nextInt(50); // بازه زمان
                copy.setAssignment(i, randomDevice, randomStart);
            }
        }
        return copy;
    }

    private Chromosome socialForage(Chromosome c, List<Chromosome> pop) {
        Chromosome copy = c.copy();
        int size = copy.getDeviceAssignments().length;

        // محاسبه میانگین کروموزوم‌ها (centroid)
        double[] avgDevice = new double[size];
        double[] avgStart = new double[size];
        for (Chromosome ch : pop) {
            for (int i = 0; i < size; i++) {
                avgDevice[i] += ch.getDevice(i);
                avgStart[i] += ch.getStartSlot(i);
            }
        }
        for (int i = 0; i < size; i++) {
            avgDevice[i] /= pop.size();
            avgStart[i] /= pop.size();
        }

        // حرکت به سمت میانگین
        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() < 0.3) {
                int newDev = (int) Math.round((copy.getDevice(i) + avgDevice[i]) / 2.0);
                int newStart = (int) Math.round((copy.getStartSlot(i) + avgStart[i]) / 2.0);

                newDev = Math.min(nodes.size() - 1, Math.max(0, newDev));
                newStart = Math.min(50, Math.max(0, newStart));

                copy.setAssignment(i, newDev, newStart);
            }
        }

        return copy;
    }

    private List<IIoTTask> topologicalSort() {
        Map<Integer, List<Integer>> graph = workflow.getDependencyMap();
        Map<Integer, Integer> indegree = new HashMap<>();
        List<IIoTTask> tasks = new ArrayList<>(workflow.getAllTasks());

        for (IIoTTask task : tasks) {
            indegree.put(task.getId(), 0);
        }

        // محاسبه درجه ورودی
        for (List<Integer> deps : graph.values()) {
            for (int v : deps) {
                indegree.put(v, indegree.get(v) + 1);
            }
        }

        Queue<IIoTTask> queue = new LinkedList<>();
        for (IIoTTask task : tasks) {
            if (indegree.get(task.getId()) == 0) {
                queue.offer(task);
            }
        }

        List<IIoTTask> sorted = new ArrayList<>();
        while (!queue.isEmpty()) {
            IIoTTask u = queue.poll();
            sorted.add(u);

            List<Integer> children = graph.getOrDefault(u.getId(), new ArrayList<>());
            for (int childId : children) {
                indegree.put(childId, indegree.get(childId) - 1);
                if (indegree.get(childId) == 0) {
                    queue.offer(workflow.getTaskById(childId)); // فرض می‌کنیم workflow.getTaskById(id) وجود دارد
                }
            }
        }

        return sorted;
    }

    private FogNode findNodeById(int id) {
        for (FogNode n : nodes) if (n.getId() == id) return n;
        return null;
    }

    private Chromosome getBest(List<Chromosome> pop) {
        return Collections.min(pop, Comparator.comparingDouble(this::fitness));
    }

    public double getFitness(Chromosome chrom) {
        return fitness(chrom);
    }

    // ⏳ Chromosome class needs to be defined separately
}
