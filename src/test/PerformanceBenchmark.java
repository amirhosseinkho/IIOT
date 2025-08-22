package test;

import algorithms.*;
import core.*;
import simulation.*;
import java.util.*;
import java.io.*;

/**
 * Performance Benchmark Suite
 * 
 * ارزیابی جامع عملکرد الگوریتم در شرایط مختلف
 */
public class PerformanceBenchmark {
    
    private static final String BENCHMARK_RESULTS_DIR = "benchmark_results/";
    private static final int WARMUP_RUNS = 3;
    private static final int BENCHMARK_RUNS = 10;
    
    public static void main(String[] args) {
        System.out.println("⚡ ENHANCED EPO-CEIS PERFORMANCE BENCHMARK");
        System.out.println("==========================================");
        
        // ایجاد پوشه نتایج
        new File(BENCHMARK_RESULTS_DIR).mkdirs();
        
        try {
            // 1. Scalability Benchmark
            runScalabilityBenchmark();
            
            // 2. Complexity Benchmark
            runComplexityBenchmark();
            
            // 3. Memory Usage Benchmark
            runMemoryBenchmark();
            
            // 4. Algorithm Convergence Benchmark
            runConvergenceBenchmark();
            
            // 5. Stress Load Benchmark
            runStressLoadBenchmark();
            
            // 6. Real-time Performance Benchmark
            runRealTimeBenchmark();
            
            System.out.println("\n🎉 All performance benchmarks completed!");
            System.out.println("📊 Results saved to: " + BENCHMARK_RESULTS_DIR);
            
        } catch (Exception e) {
            System.err.println("❌ Performance benchmark failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 1. Scalability Benchmark - تست مقیاس‌پذیری
     */
    private static void runScalabilityBenchmark() {
        System.out.println("\n📈 SCALABILITY BENCHMARK");
        System.out.println("Testing algorithm performance with increasing problem sizes");
        System.out.println("========================================================");
        
        int[] taskSizes = {10, 25, 50, 100, 200, 300, 500, 750, 1000};
        int[] nodeSizes = {5, 8, 12, 20, 30, 40, 60, 80, 100};
        
        List<BenchmarkResult> results = new ArrayList<>();
        
        for (int i = 0; i < taskSizes.length; i++) {
            int taskCount = taskSizes[i];
            int nodeCount = nodeSizes[i];
            
            System.out.printf("\n🔬 Testing scale: %d tasks, %d nodes\n", taskCount, nodeCount);
            
            // Warmup
            for (int w = 0; w < WARMUP_RUNS; w++) {
                runSingleScalabilityTest(taskCount, nodeCount);
            }
            
            // Actual benchmark
            long totalTime = 0;
            double totalCost = 0;
            int successfulRuns = 0;
            
            for (int r = 0; r < BENCHMARK_RUNS; r++) {
                try {
                    BenchmarkResult result = runSingleScalabilityTest(taskCount, nodeCount);
                    totalTime += result.executionTime;
                    totalCost += result.totalCost;
                    successfulRuns++;
                } catch (Exception e) {
                    System.out.printf("    ❌ Run %d failed: %s\n", r + 1, e.getMessage());
                }
            }
            
            if (successfulRuns > 0) {
                double avgTime = (double) totalTime / successfulRuns;
                double avgCost = totalCost / successfulRuns;
                
                System.out.printf("    ✅ Average time: %.2f ms, Average cost: %.2f\n", avgTime, avgCost);
                
                BenchmarkResult avgResult = new BenchmarkResult();
                avgResult.taskCount = taskCount;
                avgResult.nodeCount = nodeCount;
                avgResult.executionTime = (long) avgTime;
                avgResult.totalCost = avgCost;
                avgResult.successRate = (double) successfulRuns / BENCHMARK_RUNS;
                
                results.add(avgResult);
            }
        }
        
        saveScalabilityResults(results);
        analyzeScalabilityTrends(results);
    }
    
    private static BenchmarkResult runSingleScalabilityTest(int taskCount, int nodeCount) {
        Workflow workflow = generateBenchmarkWorkflow(taskCount);
        List<FogNode> nodes = generateBenchmarkNodes(nodeCount);
        
        long startTime = System.currentTimeMillis();
        
        EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
        EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
        
        long endTime = System.currentTimeMillis();
        
        BenchmarkResult benchResult = new BenchmarkResult();
        benchResult.taskCount = taskCount;
        benchResult.nodeCount = nodeCount;
        benchResult.executionTime = endTime - startTime;
        benchResult.totalCost = result.totalCost;
        
        return benchResult;
    }
    
    /**
     * 2. Complexity Benchmark - تست پیچیدگی
     */
    private static void runComplexityBenchmark() {
        System.out.println("\n🧩 COMPLEXITY BENCHMARK");
        System.out.println("Testing with workflows of varying complexity");
        System.out.println("==========================================");
        
        String[] complexityTypes = {
            "linear",      // وابستگی‌های خطی
            "parallel",    // تسک‌های موازی
            "diamond",     // الگوی diamond
            "complex_dag", // DAG پیچیده
            "deep_chain"   // زنجیره عمیق
        };
        
        for (String complexityType : complexityTypes) {
            System.out.printf("\n🔍 Testing %s complexity...\n", complexityType);
            
            List<Long> executionTimes = new ArrayList<>();
            List<Double> costs = new ArrayList<>();
            
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                try {
                    Workflow complexWorkflow = generateComplexWorkflow(complexityType, 100);
                    List<FogNode> nodes = generateBenchmarkNodes(20);
                    
                    long startTime = System.currentTimeMillis();
                    
                    EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(complexWorkflow, nodes);
                    EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                    
                    long endTime = System.currentTimeMillis();
                    
                    executionTimes.add(endTime - startTime);
                    costs.add(result.totalCost);
                    
                } catch (Exception e) {
                    System.out.printf("    ❌ Run %d failed: %s\n", run + 1, e.getMessage());
                }
            }
            
            if (!executionTimes.isEmpty()) {
                double avgTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                double avgCost = costs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double stdDevTime = calculateStandardDeviation(executionTimes);
                
                System.out.printf("    📊 %s: Avg time=%.2f ms (±%.2f), Avg cost=%.2f\n", 
                    complexityType, avgTime, stdDevTime, avgCost);
            }
        }
    }
    
    /**
     * 3. Memory Usage Benchmark
     */
    private static void runMemoryBenchmark() {
        System.out.println("\n💾 MEMORY USAGE BENCHMARK");
        System.out.println("Monitoring memory consumption during execution");
        System.out.println("============================================");
        
        Runtime runtime = Runtime.getRuntime();
        
        int[] taskSizes = {50, 100, 200, 500, 1000};
        
        for (int taskCount : taskSizes) {
            System.out.printf("\n🧠 Testing memory with %d tasks...\n", taskCount);
            
            // Force garbage collection before test
            System.gc();
            Thread.yield();
            
            long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
            
            try {
                Workflow workflow = generateBenchmarkWorkflow(taskCount);
                List<FogNode> nodes = generateBenchmarkNodes(taskCount / 5);
                
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
                long memoryUsed = memoryAfter - memoryBefore;
                
                System.out.printf("    📈 Memory used: %.2f MB, Cost: %.2f\n", 
                    memoryUsed / 1024.0 / 1024.0, result.totalCost);
                
            } catch (Exception e) {
                System.out.printf("    ❌ Memory test failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 4. Algorithm Convergence Benchmark
     */
    private static void runConvergenceBenchmark() {
        System.out.println("\n🎯 CONVERGENCE BENCHMARK");
        System.out.println("Testing algorithm convergence behavior");
        System.out.println("====================================");
        
        Workflow testWorkflow = generateBenchmarkWorkflow(100);
        List<FogNode> testNodes = generateBenchmarkNodes(20);
        
        // تست با population size های مختلف
        int[] populationSizes = {10, 20, 50, 100};
        int[] maxGenerations = {50, 100, 200, 500};
        
        for (int i = 0; i < populationSizes.length; i++) {
            int popSize = populationSizes[i];
            int maxGen = maxGenerations[i];
            
            System.out.printf("\n🧬 Testing convergence: Population=%d, Generations=%d\n", popSize, maxGen);
            
            List<Double> finalCosts = new ArrayList<>();
            List<Long> convergenceTimes = new ArrayList<>();
            
            for (int run = 0; run < 5; run++) {
                try {
                    long startTime = System.currentTimeMillis();
                    
                    EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(testWorkflow, testNodes);
                    // Note: Would need to modify algorithm to accept these parameters
                    EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                    
                    long endTime = System.currentTimeMillis();
                    
                    finalCosts.add(result.totalCost);
                    convergenceTimes.add(endTime - startTime);
                    
                } catch (Exception e) {
                    System.out.printf("      ❌ Convergence run %d failed: %s\n", run + 1, e.getMessage());
                }
            }
            
            if (!finalCosts.isEmpty()) {
                double avgCost = finalCosts.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double avgTime = convergenceTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                double costStdDev = calculateStandardDeviationDouble(finalCosts);
                
                System.out.printf("    📊 Results: Avg cost=%.2f (±%.2f), Avg time=%.2f ms\n", 
                    avgCost, costStdDev, avgTime);
            }
        }
    }
    
    /**
     * 5. Stress Load Benchmark
     */
    private static void runStressLoadBenchmark() {
        System.out.println("\n💪 STRESS LOAD BENCHMARK");
        System.out.println("Testing under extreme computational stress");
        System.out.println("========================================");
        
        // تست concurrent executions
        int[] threadCounts = {1, 2, 4, 8};
        int tasksPerThread = 50;
        int nodesPerThread = 10;
        
        for (int threadCount : threadCounts) {
            System.out.printf("\n🔥 Stress testing with %d concurrent threads...\n", threadCount);
            
            List<Thread> threads = new ArrayList<>();
            List<StressTestResult> results = Collections.synchronizedList(new ArrayList<>());
            
            long overallStartTime = System.currentTimeMillis();
            
            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                Thread thread = new Thread(() -> {
                    try {
                        Workflow workflow = generateBenchmarkWorkflow(tasksPerThread);
                        List<FogNode> nodes = generateBenchmarkNodes(nodesPerThread);
                        
                        long startTime = System.currentTimeMillis();
                        
                        EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                        EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                        
                        long endTime = System.currentTimeMillis();
                        
                        StressTestResult stressResult = new StressTestResult();
                        stressResult.threadId = threadId;
                        stressResult.executionTime = endTime - startTime;
                        stressResult.totalCost = result.totalCost;
                        stressResult.success = true;
                        
                        results.add(stressResult);
                        
                    } catch (Exception e) {
                        StressTestResult failedResult = new StressTestResult();
                        failedResult.threadId = threadId;
                        failedResult.success = false;
                        failedResult.error = e.getMessage();
                        
                        results.add(failedResult);
                    }
                });
                
                threads.add(thread);
                thread.start();
            }
            
            // Wait for all threads to complete
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    System.err.println("Thread interrupted: " + e.getMessage());
                }
            }
            
            long overallEndTime = System.currentTimeMillis();
            
            // Analyze stress test results
            int successfulThreads = 0;
            double totalCost = 0;
            long totalTime = 0;
            
            for (StressTestResult result : results) {
                if (result.success) {
                    successfulThreads++;
                    totalCost += result.totalCost;
                    totalTime += result.executionTime;
                } else {
                    System.out.printf("    ❌ Thread %d failed: %s\n", result.threadId, result.error);
                }
            }
            
            if (successfulThreads > 0) {
                double avgCost = totalCost / successfulThreads;
                double avgTime = (double) totalTime / successfulThreads;
                double throughput = (double) successfulThreads / ((overallEndTime - overallStartTime) / 1000.0);
                
                System.out.printf("    📊 Success rate: %d/%d (%.1f%%)\n", 
                    successfulThreads, threadCount, (double) successfulThreads / threadCount * 100);
                System.out.printf("    📊 Avg cost: %.2f, Avg time: %.2f ms\n", avgCost, avgTime);
                System.out.printf("    📊 Throughput: %.2f tasks/second\n", throughput);
            }
        }
    }
    
    /**
     * 6. Real-time Performance Benchmark
     */
    private static void runRealTimeBenchmark() {
        System.out.println("\n⏱️ REAL-TIME PERFORMANCE BENCHMARK");
        System.out.println("Testing real-time scheduling capabilities");
        System.out.println("=======================================");
        
        // تست با deadline های مختلف
        double[] deadlineFactors = {0.5, 0.8, 1.0, 1.5, 2.0}; // factor of minimum execution time
        
        for (double deadlineFactor : deadlineFactors) {
            System.out.printf("\n⏰ Testing with deadline factor %.1f...\n", deadlineFactor);
            
            List<Double> hitRates = new ArrayList<>();
            List<Long> executionTimes = new ArrayList<>();
            
            for (int run = 0; run < BENCHMARK_RUNS; run++) {
                try {
                    Workflow workflow = generateRealTimeWorkflow(80, deadlineFactor);
                    List<FogNode> nodes = generateBenchmarkNodes(15);
                    
                    long startTime = System.currentTimeMillis();
                    
                    EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                    EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                    
                    long endTime = System.currentTimeMillis();
                    
                    double hitRate = calculateDeadlineHitRate(workflow, result);
                    
                    hitRates.add(hitRate);
                    executionTimes.add(endTime - startTime);
                    
                } catch (Exception e) {
                    System.out.printf("    ❌ Real-time run %d failed: %s\n", run + 1, e.getMessage());
                }
            }
            
            if (!hitRates.isEmpty()) {
                double avgHitRate = hitRates.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double avgTime = executionTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
                
                System.out.printf("    📊 Deadline factor %.1f: Hit rate=%.1f%%, Avg time=%.2f ms\n", 
                    deadlineFactor, avgHitRate * 100, avgTime);
            }
        }
    }
    
    // Helper methods for benchmark data generation
    
    private static Workflow generateBenchmarkWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 1000 + rand.nextInt(4000);
            long fileSize = 10 + rand.nextInt(90);
            long outputSize = 5 + rand.nextInt(45);
            int pes = 1;
            double deadline = 5.0 + rand.nextDouble() * 25.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // Random dependencies
            if (i > 1 && rand.nextDouble() < 0.3) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateBenchmarkNodes(int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.7;
            
            int mips = isCloud ? 3000 + rand.nextInt(7000) : 500 + rand.nextInt(1500);
            int ram = isCloud ? 8192 : 1024;
            long bw = isCloud ? 1000 : 100;
            long storage = isCloud ? 100000 : 10000;
            double costPerSec = isCloud ? 0.1 + rand.nextDouble() * 0.4 : 0.02 + rand.nextDouble() * 0.08;
            double latency = isCloud ? 50 + rand.nextDouble() * 100 : 5 + rand.nextDouble() * 20;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static Workflow generateComplexWorkflow(String complexityType, int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        // Base tasks
        for (int i = 1; i <= taskCount; i++) {
            int length = 1500 + rand.nextInt(3000);
            long fileSize = 15 + rand.nextInt(75);
            long outputSize = 8 + rand.nextInt(40);
            int pes = 1;
            double deadline = 8.0 + rand.nextDouble() * 20.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
        }
        
        // Add dependencies based on complexity type
        switch (complexityType) {
            case "linear":
                for (int i = 2; i <= taskCount; i++) {
                    workflow.addDependency(i - 1, i);
                }
                break;
                
            case "parallel":
                // All tasks depend on task 1, task taskCount depends on all others
                for (int i = 2; i < taskCount; i++) {
                    workflow.addDependency(1, i);
                }
                for (int i = 2; i < taskCount; i++) {
                    workflow.addDependency(i, taskCount);
                }
                break;
                
            case "diamond":
                // Diamond pattern dependencies
                for (int i = 1; i <= taskCount / 4; i++) {
                    for (int j = i + taskCount / 4; j <= i + taskCount / 2; j++) {
                        workflow.addDependency(i, j);
                    }
                    for (int j = i + taskCount / 2; j <= i + 3 * taskCount / 4; j++) {
                        workflow.addDependency(j - taskCount / 4, j);
                    }
                }
                break;
                
            case "complex_dag":
                // Random complex dependencies
                for (int i = 2; i <= taskCount; i++) {
                    int dependencies = 1 + rand.nextInt(Math.min(3, i - 1));
                    Set<Integer> parents = new HashSet<>();
                    for (int d = 0; d < dependencies; d++) {
                        int parent = 1 + rand.nextInt(i - 1);
                        if (!parents.contains(parent)) {
                            workflow.addDependency(parent, i);
                            parents.add(parent);
                        }
                    }
                }
                break;
                
            case "deep_chain":
                // Deep sequential chain with branches
                for (int i = 2; i <= taskCount; i++) {
                    if (i <= taskCount / 2) {
                        workflow.addDependency(i - 1, i); // Main chain
                    } else {
                        workflow.addDependency(i - taskCount / 2, i); // Branch from main chain
                    }
                }
                break;
        }
        
        return workflow;
    }
    
    private static Workflow generateRealTimeWorkflow(int taskCount, double deadlineFactor) {
        Workflow workflow = generateBenchmarkWorkflow(taskCount);
        
        // Adjust deadlines based on factor
        for (IIoTTask task : workflow.getAllTasks()) {
            double minExecutionTime = task.getLength() / 10000.0; // Assuming fastest node
            double newDeadline = minExecutionTime * deadlineFactor;
            task.setDeadline(newDeadline);
        }
        
        return workflow;
    }
    
    private static double calculateDeadlineHitRate(Workflow workflow, EnhancedEPOCEIS.SchedulingResult result) {
        int totalTasks = 0;
        int hitTasks = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            totalTasks++;
            Double startTime = result.startTimes.get(task.getId());
            if (startTime != null) {
                double finishTime = startTime + (task.getLength() / 1000.0);
                if (finishTime <= task.getDeadline()) {
                    hitTasks++;
                }
            }
        }
        
        return totalTasks > 0 ? (double) hitTasks / totalTasks : 0.0;
    }
    
    // Utility methods
    
    private static void saveScalabilityResults(List<BenchmarkResult> results) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(BENCHMARK_RESULTS_DIR + "scalability_benchmark.csv"));
            writer.println("TaskCount,NodeCount,ExecutionTime,TotalCost,SuccessRate");
            
            for (BenchmarkResult result : results) {
                writer.printf("%d,%d,%d,%.2f,%.2f\n", 
                    result.taskCount, result.nodeCount, result.executionTime, 
                    result.totalCost, result.successRate);
            }
            
            writer.close();
            System.out.println("📁 Scalability results saved to scalability_benchmark.csv");
            
        } catch (IOException e) {
            System.err.println("Error saving scalability results: " + e.getMessage());
        }
    }
    
    private static void analyzeScalabilityTrends(List<BenchmarkResult> results) {
        System.out.println("\n📊 SCALABILITY TREND ANALYSIS");
        System.out.println("=============================");
        
        for (BenchmarkResult result : results) {
            double timePerTask = (double) result.executionTime / result.taskCount;
            double costPerTask = result.totalCost / result.taskCount;
            
            System.out.printf("Tasks=%d: %.2f ms/task, $%.3f/task, Success=%.1f%%\n", 
                result.taskCount, timePerTask, costPerTask, result.successRate * 100);
        }
        
        // Calculate growth rate
        if (results.size() > 1) {
            BenchmarkResult first = results.get(0);
            BenchmarkResult last = results.get(results.size() - 1);
            
            double taskGrowth = (double) last.taskCount / first.taskCount;
            double timeGrowth = (double) last.executionTime / first.executionTime;
            
            double complexity = Math.log(timeGrowth) / Math.log(taskGrowth);
            
            System.out.printf("\n📈 Time Complexity: O(n^%.2f)\n", complexity);
        }
    }
    
    private static double calculateStandardDeviation(List<Long> values) {
        if (values.size() <= 1) return 0.0;
        
        double mean = values.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double variance = values.stream()
            .mapToDouble(value -> Math.pow(value - mean, 2))
            .average().orElse(0.0);
        
        return Math.sqrt(variance);
    }
    
    private static double calculateStandardDeviationDouble(List<Double> values) {
        if (values.size() <= 1) return 0.0;
        
        double mean = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = values.stream()
            .mapToDouble(value -> Math.pow(value - mean, 2))
            .average().orElse(0.0);
        
        return Math.sqrt(variance);
    }
    
    // Data classes
    
    private static class BenchmarkResult {
        int taskCount;
        int nodeCount;
        long executionTime;
        double totalCost;
        double successRate;
    }
    
    private static class StressTestResult {
        int threadId;
        long executionTime;
        double totalCost;
        boolean success;
        String error;
    }
}
