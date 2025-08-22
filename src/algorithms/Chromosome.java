package algorithms;

import core.FogNode;
import core.IIoTTask;
import core.Workflow;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
    private int[] deviceAssignments;  // device index (not ID) for each task
    private int[] startSlots;         // start time slot for each task
    private double fitness;
    private double energy;
    private int missedDeadlines;

    public Chromosome(int numTasks) {
        this.deviceAssignments = new int[numTasks];
        this.startSlots = new int[numTasks];
        this.fitness = Double.MAX_VALUE;
    }

    public Chromosome copy() {
        Chromosome clone = new Chromosome(this.deviceAssignments.length);
        System.arraycopy(this.deviceAssignments, 0, clone.deviceAssignments, 0, this.deviceAssignments.length);
        System.arraycopy(this.startSlots, 0, clone.startSlots, 0, this.startSlots.length);
        clone.fitness = this.fitness;
        return clone;
    }

    public void setAssignment(int taskIndex, int deviceIndex, int startSlot) {
        this.deviceAssignments[taskIndex] = deviceIndex;
        this.startSlots[taskIndex] = startSlot;
    }


    public int getDevice(int taskIndex) {
        return this.deviceAssignments[taskIndex];
    }

    public int getStartSlot(int taskIndex) {
        return this.startSlots[taskIndex];
    }

    public void setDeviceForTask(int taskIndex, int deviceId) {
        this.deviceAssignments[taskIndex] = deviceId;
    }

    public void setStartSlotForTask(int taskIndex, int startSlot) {
        this.startSlots[taskIndex] = startSlot;
    }

    public int getDeviceForTask(int taskIndex) {
        return this.deviceAssignments[taskIndex];
    }

    public int getStartSlotForTask(int taskIndex) {
        return this.startSlots[taskIndex];
    }

    public int[] getDeviceAssignments() {
        return deviceAssignments;
    }

    public int[] getStartSlots() {
        return startSlots;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setEnergy(double e) {
        this.energy = e;
    }

    public double getEnergy() {
        return this.energy;
    }

    public void setMissedDeadlines(int n) {
        this.missedDeadlines = n;
    }

    public int getMissedDeadlines() {
        return this.missedDeadlines;
    }
    public static Chromosome random(Workflow workflow, List<FogNode> nodes) {
        int numTasks = workflow.getAllTasks().size();
        Chromosome c = new Chromosome(numTasks);
        Random rand = new Random();

        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            FogNode node = nodes.get(rand.nextInt(nodes.size()));
            int startSlot = rand.nextInt(10);
            c.setDeviceForTask(taskId, node.getId()); // ✅ ID ذخیره می‌شه
            c.setStartSlotForTask(taskId, startSlot);
        }

        return c;
    }



    @Override
    public int compareTo(Chromosome o) {
        return Double.compare(this.fitness, o.fitness);
    }
}