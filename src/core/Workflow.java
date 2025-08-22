package core;

import java.util.*;

public class Workflow {
    private Map<Integer, IIoTTask> taskMap;                  // نگهداری تمام تسک‌ها
    private Map<Integer, List<Integer>> dependencyGraph;     // گراف وابستگی‌ها (from → to)
    private Map<Integer, List<Integer>> dependencyMap;

    public Workflow() {
        taskMap = new HashMap<>();
        dependencyGraph = new HashMap<>();
        dependencyMap = new HashMap<>();
    }

    public void addTask(IIoTTask task) {
        taskMap.put(task.getId(), task);
        dependencyGraph.putIfAbsent(task.getId(), new ArrayList<>());
    }

    public void addDependency(int fromTaskId, int toTaskId) {
        dependencyGraph.putIfAbsent(fromTaskId, new ArrayList<>());
        dependencyGraph.get(fromTaskId).add(toTaskId);
        
        // Update dependencyMap (reverse mapping: to → from)
        dependencyMap.putIfAbsent(toTaskId, new ArrayList<>());
        dependencyMap.get(toTaskId).add(fromTaskId);

        // به‌روزرسانی گره‌ها هم
        IIoTTask from = taskMap.get(fromTaskId);
        IIoTTask to = taskMap.get(toTaskId);
        if (from != null && to != null) {
            from.addChild(toTaskId);
            to.addParent(fromTaskId);
        }
    }

    public IIoTTask getTask(int id) {
        return taskMap.get(id);
    }

    public List<Integer> getDependencies(int taskId) {
        return taskMap.containsKey(taskId) ? taskMap.get(taskId).getParents() : new ArrayList<>();
    }

    public Collection<IIoTTask> getAllTasks() {
        return taskMap.values();
    }

    public Map<Integer, List<Integer>> getDependencyGraph() {
        return dependencyGraph;
    }


    public List<IIoTTask> getExecutableTasks(Set<Integer> completedTaskIds) {
        List<IIoTTask> readyTasks = new ArrayList<>();

        for (Integer taskId : taskMap.keySet()) {
            List<Integer> deps = getDependencies(taskId);
            if (completedTaskIds.containsAll(deps)) {
                readyTasks.add(taskMap.get(taskId));
            }
        }

        return readyTasks;
    }


    // برمی‌گردونه: taskID → لیستی از taskهایی که به آن وابسته‌اند
    public Map<Integer, List<Integer>> getDependencyMap() {
        if (dependencyMap == null) {
            dependencyMap = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : dependencyGraph.entrySet()) {
                int from = entry.getKey();
                for (int to : entry.getValue()) {
                    dependencyMap.putIfAbsent(to, new ArrayList<>());
                    dependencyMap.get(to).add(from);
                }
            }
        }
        return dependencyMap;
    }


    // برمی‌گردونه task با ID مشخص
    public IIoTTask getTaskById(int id) {
        return taskMap.get(id);
    }


    // گرفتن والدهای یک task خاص
    public List<IIoTTask> getParents(IIoTTask task) {
        List<IIoTTask> parents = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : dependencyMap.entrySet()) {
            if (entry.getValue().contains(task.getId())) {
                IIoTTask parent = getTaskById(entry.getKey());
                if (parent != null) parents.add(parent);
            }
        }
        return parents;
    }


    // 🔹 مرتب‌سازی توپولوژیک برای CEIS/HFCO
    public List<IIoTTask> getTopologicallySortedTasks() {
        List<IIoTTask> sorted = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Set<Integer> visiting = new HashSet<>();

        for (Integer taskId : taskMap.keySet()) {
            dfs(taskId, visited, visiting, sorted);
        }

        Collections.reverse(sorted);
        return sorted;
    }

    private void dfs(int taskId, Set<Integer> visited, Set<Integer> visiting, List<IIoTTask> sorted) {
        if (visited.contains(taskId)) return;
        if (visiting.contains(taskId)) throw new RuntimeException("Cycle detected in task DAG");

        visiting.add(taskId);
        for (int dep : getDependencies(taskId)) {
            dfs(dep, visited, visiting, sorted);
        }
        visiting.remove(taskId);
        visited.add(taskId);
        sorted.add(getTask(taskId));
    }

    // 🔹 تعیین earliest valid start time با توجه به والدها و زمان آزاد گره
    public double getReadyTime(IIoTTask task, FogNode node, Map<Integer, Double> nodeAvailableTime) {
        double maxParentFinish = 0.0;

        for (int parentId : task.getParents()) {
            IIoTTask parent = getTask(parentId);
            if (parent != null && parent.getFinishTime() > maxParentFinish) {
                maxParentFinish = parent.getFinishTime();
            }
        }

        double availableTime = nodeAvailableTime.getOrDefault(node.getId(), 0.0);
        return Math.max(maxParentFinish, availableTime);
    }

    @Override
    public String toString() {
        return "Workflow{" +
                "taskMap=" + taskMap.keySet() +
                ", dependencyGraph=" + dependencyGraph +
                '}';
    }
}
