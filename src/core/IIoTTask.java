package core;

import java.util.ArrayList;
import java.util.List;

public class IIoTTask {
    private int id;
    private int length;           // MI
    private long fileSize;
    private long outputSize;
    private int pesNumber;

    private double deadline;      // Δᵥ
    private double energyConsumption = 0.0;

    private double startTime = -1;
    private double finishTime = -1;

    private List<Integer> parents = new ArrayList<>();
    private List<Integer> children = new ArrayList<>();

    public IIoTTask(int id, int length, long fileSize, long outputSize, int pesNumber, double deadline) {
        this.id = id;
        this.length = length;
        this.fileSize = fileSize;
        this.outputSize = outputSize;
        this.pesNumber = pesNumber;
        this.deadline = deadline;
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getOutputSize() {
        return outputSize;
    }

    public int getPesNumber() {
        return pesNumber;
    }

    public double getDeadline() {
        return deadline;
    }

    public double getStartTime() {
        return startTime;
    }

    public double getFinishTime() {
        return finishTime;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public List<Integer> getParents() {
        return parents;
    }

    public List<Integer> getChildren() {
        return children;
    }

    // --- Setters ---
    public void setDeadline(double deadline) {
        this.deadline = deadline;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public void setFinishTime(double finishTime) {
        this.finishTime = finishTime;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public void addParent(int parentId) {
        if (!parents.contains(parentId)) {
            parents.add(parentId);
        }
    }

    public void addChild(int childId) {
        if (!children.contains(childId)) {
            children.add(childId);
        }
    }

    public void reset() {
        this.startTime = -1;
        this.finishTime = -1;
        this.energyConsumption = 0.0;
    }

    @Override
    public String toString() {
        return "IIoTTask{" +
                "id=" + id +
                ", length=" + length +
                ", fileSize=" + fileSize +
                ", outputSize=" + outputSize +
                ", pesNumber=" + pesNumber +
                ", deadline=" + deadline +
                ", energyConsumption=" + energyConsumption +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", parents=" + parents +
                ", children=" + children +
                '}';
    }
}
