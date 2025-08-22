package algorithms;

import core.FogNode;
import core.IIoTTask;
import core.Workflow;

import java.util.*;

/**
 * Unified Chromosome Implementation for IIoT Task Scheduling
 * Supports both array-based (index) and map-based (ID) representations
 * Designed according to the Enhanced EPO-CEIS project specifications
 */
public class UnifiedChromosome implements Comparable<UnifiedChromosome> {
    
    // Primary representation: Map-based for flexibility (taskId -> nodeId, taskId -> startTime)
    private Map<Integer, Integer> taskToNodeMap;      // taskId -> nodeId
    private Map<Integer, Double> taskStartTimes;      // taskId -> startTime
    
    // Secondary representation: Array-based for efficiency (optional)
    private int[] deviceAssignments;                  // device index for each task (by task index)
    private double[] startSlots;                      // start time for each task (by task index)
    
    // Fitness and metrics
    private double fitness = Double.MAX_VALUE;
    private double energy = 0.0;
    private int missedDeadlines = 0;
    private double makespan = 0.0;
    private double totalCost = 0.0;
    
    // Metadata
    private boolean useArrayRepresentation = false;
    private List<Integer> taskIdToIndex;              // Mapping from taskId to array index
    private List<Integer> indexToTaskId;              // Mapping from array index to taskId
    
    /**
     * Constructor for map-based representation (preferred for EPO-CEIS)
     */
    public UnifiedChromosome() {
        this.taskToNodeMap = new HashMap<>();
        this.taskStartTimes = new HashMap<>();
        this.useArrayRepresentation = false;
    }
    
    /**
     * Constructor for array-based representation (legacy compatibility)
     */
    public UnifiedChromosome(int numTasks) {
        this.deviceAssignments = new int[numTasks];
        this.startSlots = new double[numTasks];
        this.taskToNodeMap = new HashMap<>();
        this.taskStartTimes = new HashMap<>();
        this.useArrayRepresentation = true;
        this.fitness = Double.MAX_VALUE;
    }
    
    /**
     * Copy constructor
     */
    public UnifiedChromosome(UnifiedChromosome other) {
        this.taskToNodeMap = new HashMap<>(other.taskToNodeMap);
        this.taskStartTimes = new HashMap<>(other.taskStartTimes);
        this.fitness = other.fitness;
        this.energy = other.energy;
        this.missedDeadlines = other.missedDeadlines;
        this.makespan = other.makespan;
        this.totalCost = other.totalCost;
        this.useArrayRepresentation = other.useArrayRepresentation;
        
        if (other.deviceAssignments != null) {
            this.deviceAssignments = Arrays.copyOf(other.deviceAssignments, other.deviceAssignments.length);
        }
        if (other.startSlots != null) {
            this.startSlots = Arrays.copyOf(other.startSlots, other.startSlots.length);
        }
        if (other.taskIdToIndex != null) {
            this.taskIdToIndex = new ArrayList<>(other.taskIdToIndex);
        }
        if (other.indexToTaskId != null) {
            this.indexToTaskId = new ArrayList<>(other.indexToTaskId);
        }
    }
    
    /**
     * Initialize mapping between task IDs and array indices
     */
    public void initializeMapping(Workflow workflow) {
        List<IIoTTask> tasks = new ArrayList<>(workflow.getAllTasks());
        tasks.sort(Comparator.comparingInt(IIoTTask::getId)); // Sort by ID for consistency
        
        this.taskIdToIndex = new ArrayList<>();
        this.indexToTaskId = new ArrayList<>();
        
        for (int i = 0; i < tasks.size(); i++) {
            int taskId = tasks.get(i).getId();
            this.taskIdToIndex.add(taskId);
            this.indexToTaskId.add(taskId);
        }
        
        // Synchronize representations if using arrays
        if (useArrayRepresentation) {
            syncArrayToMap();
        }
    }
    
    /**
     * Set task assignment using task ID and node ID
     */
    public void setTaskAssignment(int taskId, int nodeId, double startTime) {
        this.taskToNodeMap.put(taskId, nodeId);
        this.taskStartTimes.put(taskId, startTime);
        
        // Update array representation if active
        if (useArrayRepresentation && taskIdToIndex != null) {
            int index = taskIdToIndex.indexOf(taskId);
            if (index >= 0 && index < deviceAssignments.length) {
                deviceAssignments[index] = nodeId;
                startSlots[index] = startTime;
            }
        }
    }
    
    /**
     * Set assignment using array index (legacy compatibility)
     */
    public void setAssignment(int taskIndex, int deviceIndex, double startSlot) {
        if (useArrayRepresentation && taskIndex < deviceAssignments.length) {
            deviceAssignments[taskIndex] = deviceIndex;
            startSlots[taskIndex] = startSlot;
            
            // Update map representation
            if (indexToTaskId != null && taskIndex < indexToTaskId.size()) {
                int taskId = indexToTaskId.get(taskIndex);
                taskToNodeMap.put(taskId, deviceIndex);
                taskStartTimes.put(taskId, startSlot);
            }
        }
    }
    
    /**
     * Get node ID for task ID
     */
    public Integer getNodeForTask(int taskId) {
        return taskToNodeMap.get(taskId);
    }
    
    /**
     * Get start time for task ID
     */
    public Double getStartTimeForTask(int taskId) {
        return taskStartTimes.get(taskId);
    }
    
    /**
     * Get device assignment by array index (legacy compatibility)
     */
    public int getDevice(int taskIndex) {
        if (useArrayRepresentation && taskIndex < deviceAssignments.length) {
            return deviceAssignments[taskIndex];
        }
        return -1;
    }
    
    /**
     * Get start slot by array index (legacy compatibility)
     */
    public double getStartSlot(int taskIndex) {
        if (useArrayRepresentation && taskIndex < startSlots.length) {
            return startSlots[taskIndex];
        }
        return -1;
    }
    
    /**
     * Synchronize array representation to map
     */
    private void syncArrayToMap() {
        if (!useArrayRepresentation || indexToTaskId == null) return;
        
        for (int i = 0; i < Math.min(deviceAssignments.length, indexToTaskId.size()); i++) {
            int taskId = indexToTaskId.get(i);
            taskToNodeMap.put(taskId, deviceAssignments[i]);
            taskStartTimes.put(taskId, startSlots[i]);
        }
    }
    
    /**
     * Synchronize map representation to array
     */
    private void syncMapToArray() {
        if (!useArrayRepresentation || taskIdToIndex == null) return;
        
        for (Map.Entry<Integer, Integer> entry : taskToNodeMap.entrySet()) {
            int taskId = entry.getKey();
            int index = taskIdToIndex.indexOf(taskId);
            if (index >= 0 && index < deviceAssignments.length) {
                deviceAssignments[index] = entry.getValue();
                Double startTime = taskStartTimes.get(taskId);
                if (startTime != null) {
                    startSlots[index] = startTime;
                }
            }
        }
    }
    
    // Getters and Setters
    public Map<Integer, Integer> getTaskToNodeMap() {
        return taskToNodeMap;
    }
    
    public Map<Integer, Double> getTaskStartTimes() {
        return taskStartTimes;
    }
    
    public int[] getDeviceAssignments() {
        if (useArrayRepresentation) {
            syncMapToArray();
        }
        return deviceAssignments;
    }
    
    public double[] getStartSlots() {
        if (useArrayRepresentation) {
            syncMapToArray();
        }
        return startSlots;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
    
    public double getEnergy() {
        return energy;
    }
    
    public void setEnergy(double energy) {
        this.energy = energy;
    }
    
    public int getMissedDeadlines() {
        return missedDeadlines;
    }
    
    public void setMissedDeadlines(int missedDeadlines) {
        this.missedDeadlines = missedDeadlines;
    }
    
    public double getMakespan() {
        return makespan;
    }
    
    public void setMakespan(double makespan) {
        this.makespan = makespan;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    /**
     * Create a deep copy of this chromosome
     */
    public UnifiedChromosome copy() {
        return new UnifiedChromosome(this);
    }
    
    /**
     * Generate random chromosome for given workflow and nodes
     */
    public static UnifiedChromosome random(Workflow workflow, List<FogNode> nodes) {
        UnifiedChromosome chromosome = new UnifiedChromosome();
        Random random = new Random();
        
        for (IIoTTask task : workflow.getAllTasks()) {
            int taskId = task.getId();
            FogNode randomNode = nodes.get(random.nextInt(nodes.size()));
            double randomStartTime = random.nextDouble() * 50.0; // 0-50 seconds
            
            chromosome.setTaskAssignment(taskId, randomNode.getId(), randomStartTime);
        }
        
        return chromosome;
    }
    
    /**
     * Validate chromosome consistency
     */
    public boolean isValid(Workflow workflow) {
        // Check if all tasks have assignments
        for (IIoTTask task : workflow.getAllTasks()) {
            if (!taskToNodeMap.containsKey(task.getId()) || 
                !taskStartTimes.containsKey(task.getId())) {
                return false;
            }
        }
        
        // Check for valid start times (non-negative)
        for (Double startTime : taskStartTimes.values()) {
            if (startTime == null || startTime < 0) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public int compareTo(UnifiedChromosome other) {
        return Double.compare(this.fitness, other.fitness);
    }
    
    @Override
    public String toString() {
        return "UnifiedChromosome{" +
                "tasks=" + taskToNodeMap.size() +
                ", fitness=" + String.format("%.2f", fitness) +
                ", energy=" + String.format("%.2f", energy) +
                ", missedDeadlines=" + missedDeadlines +
                ", makespan=" + String.format("%.2f", makespan) +
                ", totalCost=" + String.format("%.2f", totalCost) +
                '}';
    }
}
