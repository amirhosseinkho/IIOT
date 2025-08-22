package test;

import algorithms.*;
import core.*;
import simulation.*;
import evaluation.*;

import java.util.*;
import java.io.*;

/**
 * Advanced Scenario Tester - Stress Testing for IIoT Scheduling Algorithms
 * 
 * مجموعه‌ای از تست‌های چالشی برای ارزیابی جامع الگوریتم‌ها در شرایط سخت
 */
public class AdvancedScenarioTester {
    
    private static final String RESULTS_DIR = "stress_test_results/";
    private static final int STRESS_RUNS = 10; // تعداد اجرای هر تست
    
    public static void main(String[] args) {
        System.out.println("🔥🔥🔥 ADVANCED STRESS TESTING FRAMEWORK 🔥🔥🔥");
        System.out.println("========================================================");
        System.out.println("Testing Enhanced EPO-CEIS under EXTREME conditions");
        System.out.println("========================================================\n");
        
        // ایجاد پوشه نتایج
        new File(RESULTS_DIR).mkdirs();
        
        try {
            // اجرای تمام سناریوهای چالشی
            runAllStressTests();
            
            System.out.println("\n🎉 All stress tests completed!");
            System.out.println("📊 Results saved to: " + RESULTS_DIR);
            
        } catch (Exception e) {
            System.err.println("❌ Stress testing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void runAllStressTests() {
        System.out.println("🚀 Starting comprehensive stress testing...\n");
        
        // 1. تست مقیاس‌پذیری
        runScalabilityStressTest();
        
        // 2. تست deadline های غیرممکن
        runImpossibleDeadlineTest();
        
        // 3. تست عدم تعادل منابع
        runResourceImbalanceTest();
        
        // 4. تست شبکه‌های پر تأخیر
        runHighLatencyNetworkTest();
        
        // 5. تست محدودیت شدید انرژی
        runEnergyConstrainedTest();
        
        // 6. تست توپولوژی پیچیده
        runComplexTopologyTest();
        
        // 7. تست dynamic workload
        runDynamicWorkloadTest();
        
        // 8. تست fault tolerance
        runFaultToleranceTest();
        
        // 9. تست multi-objective conflicts
        runConflictingObjectivesTest();
        
        // 10. تست extreme heterogeneity
        runExtremeHeterogeneityTest();
    }
    
    /**
     * 1. تست مقیاس‌پذیری - از 10 تا 5000 تسک
     */
    private static void runScalabilityStressTest() {
        System.out.println("📈 SCALABILITY STRESS TEST");
        System.out.println("Testing algorithm performance with increasing workload sizes");
        System.out.println("==========================================================");
        
        int[] taskCounts = {10, 50, 100, 200, 500, 1000, 2000, 3000, 5000};
        
        for (int taskCount : taskCounts) {
            System.out.printf("\n🔬 Testing with %d tasks...\n", taskCount);
            
            // ایجاد workflow بزرگ
            Workflow largeWorkflow = generateLargeWorkflow(taskCount);
            List<FogNode> nodes = generateScalableNodes(taskCount);
            
            long startTime = System.currentTimeMillis();
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(largeWorkflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                long executionTime = System.currentTimeMillis() - startTime;
                
                System.out.printf("  ✅ Success: Cost=%.2f, Time=%dms, Assignments=%d\n", 
                    result.totalCost, executionTime, result.taskAssignments.size());
                
                // ذخیره نتایج
                saveScalabilityResult(taskCount, result, executionTime);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 2. تست deadline های غیرممکن
     */
    private static void runImpossibleDeadlineTest() {
        System.out.println("\n⏰ IMPOSSIBLE DEADLINE STRESS TEST");
        System.out.println("Testing with extremely tight and impossible deadlines");
        System.out.println("=====================================================");
        
        double[] deadlineFactors = {0.1, 0.3, 0.5, 0.7, 1.0, 1.5}; // factor of minimum possible time
        
        for (double factor : deadlineFactors) {
            System.out.printf("\n🎯 Testing with deadline factor %.1f...\n", factor);
            
            Workflow workflow = generateTightDeadlineWorkflow(50, factor);
            List<FogNode> nodes = generateMixedNodes(15);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                // محاسبه deadline violations
                int violations = calculateDeadlineViolations(workflow, result);
                double hitRate = 1.0 - (double) violations / workflow.getAllTasks().size();
                
                System.out.printf("  📊 Factor %.1f: Cost=%.2f, Hit Rate=%.1f%%, Violations=%d\n", 
                    factor, result.totalCost, hitRate * 100, violations);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 3. تست عدم تعادل شدید منابع
     */
    private static void runResourceImbalanceTest() {
        System.out.println("\n⚖️ RESOURCE IMBALANCE STRESS TEST");
        System.out.println("Testing with severely imbalanced fog-cloud resources");
        System.out.println("===================================================");
        
        String[] scenarios = {
            "fog_dominant",    // 90% fog nodes, 10% cloud
            "cloud_dominant",  // 10% fog nodes, 90% cloud
            "few_powerful",    // کم تعداد ولی قدرتمند
            "many_weak",       // زیاد ولی ضعیف
            "extreme_hetero"   // تفاوت شدید در قدرت
        };
        
        for (String scenario : scenarios) {
            System.out.printf("\n🏗️ Testing %s scenario...\n", scenario);
            
            Workflow workflow = generateMediumWorkflow(100);
            List<FogNode> nodes = generateImbalancedNodes(scenario, 20);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                // تحلیل توزیع تسک‌ها
                analyzeTaskDistribution(result, nodes, scenario);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 4. تست شبکه‌های پر تأخیر
     */
    private static void runHighLatencyNetworkTest() {
        System.out.println("\n🌐 HIGH LATENCY NETWORK STRESS TEST");
        System.out.println("Testing with extremely high network latencies");
        System.out.println("===========================================");
        
        double[] latencyMultipliers = {1.0, 5.0, 10.0, 20.0, 50.0, 100.0};
        
        for (double multiplier : latencyMultipliers) {
            System.out.printf("\n📡 Testing with latency multiplier %.1fx...\n", multiplier);
            
            Workflow workflow = generateDataIntensiveWorkflow(80);
            List<FogNode> nodes = generateHighLatencyNodes(12, multiplier);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                double avgLatency = calculateAverageLatency(result, workflow, nodes);
                
                System.out.printf("  📊 Latency %.1fx: Cost=%.2f, Avg Latency=%.2fs\n", 
                    multiplier, result.totalCost, avgLatency);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 5. تست محدودیت شدید انرژی
     */
    private static void runEnergyConstrainedTest() {
        System.out.println("\n🔋 ENERGY CONSTRAINED STRESS TEST");
        System.out.println("Testing with severe energy limitations");
        System.out.println("====================================");
        
        double[] energyBudgets = {100.0, 200.0, 500.0, 1000.0, 2000.0}; // Wh
        
        for (double budget : energyBudgets) {
            System.out.printf("\n⚡ Testing with energy budget %.0f Wh...\n", budget);
            
            Workflow workflow = generateEnergyIntensiveWorkflow(60);
            List<FogNode> nodes = generateEnergyAwareNodes(10);
            
            try {
                // اضافه کردن محدودیت انرژی به الگوریتم
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                double totalEnergy = calculateTotalEnergyConsumption(result, workflow, nodes);
                
                System.out.printf("  🔋 Budget %.0f Wh: Cost=%.2f, Energy Used=%.2f Wh, %s\n", 
                    budget, result.totalCost, totalEnergy, 
                    totalEnergy <= budget ? "✅ Within Budget" : "❌ Over Budget");
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 6. تست توپولوژی پیچیده
     */
    private static void runComplexTopologyTest() {
        System.out.println("\n🕸️ COMPLEX TOPOLOGY STRESS TEST");
        System.out.println("Testing with complex network topologies");
        System.out.println("=====================================");
        
        String[] topologies = {
            "star",           // تمام fog nodes به یک cloud متصل
            "mesh",           // ارتباط کامل
            "tree",           // ساختار درختی
            "ring",           // ساختار حلقوی
            "random",         // اتصالات تصادفی
            "hierarchical"    // سلسله مراتبی چندسطحه
        };
        
        for (String topology : topologies) {
            System.out.printf("\n🌐 Testing %s topology...\n", topology);
            
            Workflow workflow = generateComplexWorkflow(70);
            List<FogNode> nodes = generateTopologyBasedNodes(topology, 15);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                analyzeTopologyPerformance(result, nodes, topology);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 7. تست dynamic workload
     */
    private static void runDynamicWorkloadTest() {
        System.out.println("\n🔄 DYNAMIC WORKLOAD STRESS TEST");
        System.out.println("Testing with time-varying workloads");
        System.out.println("==================================");
        
        int timeSteps = 10;
        
        for (int step = 0; step < timeSteps; step++) {
            System.out.printf("\n⏱️ Time step %d/%d...\n", step + 1, timeSteps);
            
            // workload که با زمان تغییر می‌کند
            Workflow dynamicWorkflow = generateDynamicWorkflow(step, 50 + step * 10);
            List<FogNode> nodes = generateDynamicNodes(step, 12);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(dynamicWorkflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                System.out.printf("  📊 Step %d: Tasks=%d, Cost=%.2f\n", 
                    step + 1, dynamicWorkflow.getAllTasks().size(), result.totalCost);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 8. تست fault tolerance
     */
    private static void runFaultToleranceTest() {
        System.out.println("\n🛠️ FAULT TOLERANCE STRESS TEST");
        System.out.println("Testing with node failures and recovery");
        System.out.println("======================================");
        
        double[] failureRates = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5}; // درصد node هایی که fail می‌شوند
        
        for (double failureRate : failureRates) {
            System.out.printf("\n💥 Testing with %.0f%% node failure rate...\n", failureRate * 100);
            
            Workflow workflow = generateFaultTolerantWorkflow(90);
            List<FogNode> originalNodes = generateReliableNodes(20);
            List<FogNode> workingNodes = simulateNodeFailures(originalNodes, failureRate);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, workingNodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                double adaptationScore = calculateAdaptationScore(result, originalNodes, workingNodes);
                
                System.out.printf("  🔧 Failure %.0f%%: Working Nodes=%d/%d, Cost=%.2f, Adaptation=%.2f\n", 
                    failureRate * 100, workingNodes.size(), originalNodes.size(), 
                    result.totalCost, adaptationScore);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 9. تست multi-objective conflicts
     */
    private static void runConflictingObjectivesTest() {
        System.out.println("\n⚔️ CONFLICTING OBJECTIVES STRESS TEST");
        System.out.println("Testing with severely conflicting optimization goals");
        System.out.println("==================================================");
        
        String[] conflictScenarios = {
            "cost_vs_latency",     // کم‌ترین هزینه در برابر کم‌ترین تأخیر
            "energy_vs_speed",     // کم‌ترین انرژی در برابر سریع‌ترین اجرا
            "reliability_vs_cost", // بالاترین قابلیت اطمینان در برابر کم‌ترین هزینه
            "load_balance_vs_opt", // توزیع متعادل در برابر بهینه‌سازی
            "all_conflicts"        // تمام اهداف در تضاد
        };
        
        for (String scenario : conflictScenarios) {
            System.out.printf("\n⚖️ Testing %s conflict...\n", scenario);
            
            Workflow workflow = generateConflictingWorkflow(scenario, 80);
            List<FogNode> nodes = generateConflictingNodes(scenario, 15);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                analyzeObjectiveConflicts(result, workflow, nodes, scenario);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    /**
     * 10. تست extreme heterogeneity
     */
    private static void runExtremeHeterogeneityTest() {
        System.out.println("\n🌈 EXTREME HETEROGENEITY STRESS TEST");
        System.out.println("Testing with extremely diverse nodes and tasks");
        System.out.println("============================================");
        
        int[] diversityLevels = {2, 5, 10, 20, 50}; // تعداد انواع مختلف nodes
        
        for (int diversity : diversityLevels) {
            System.out.printf("\n🎨 Testing with %d different node types...\n", diversity);
            
            Workflow workflow = generateHeterogeneousWorkflow(100);
            List<FogNode> nodes = generateExtremelyDiverseNodes(diversity, 25);
            
            try {
                EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(workflow, nodes);
                EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
                
                double diversityUtilization = calculateDiversityUtilization(result, nodes);
                
                System.out.printf("  🌈 Diversity %d: Cost=%.2f, Utilization=%.2f%%\n", 
                    diversity, result.totalCost, diversityUtilization * 100);
                
            } catch (Exception e) {
                System.out.printf("  ❌ Failed: %s\n", e.getMessage());
            }
        }
    }
    
    // Helper methods for generating test scenarios
    
    private static Workflow generateLargeWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            // تسک‌ها با workload و deadline متنوع
            int length = 1000 + rand.nextInt(9000); // 1K-10K MI
            long fileSize = 1 + rand.nextInt(100); // 1-100 MB
            long outputSize = 1 + rand.nextInt(50); // 1-50 MB
            int pes = 1;
            double deadline = 10.0 + rand.nextDouble() * 40.0; // 10-50 seconds
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // اضافه کردن وابستگی‌های تصادفی
            if (i > 1 && rand.nextDouble() < 0.3) { // 30% احتمال وابستگی
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateScalableNodes(int taskCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        // تعداد nodes متناسب با تعداد tasks
        int nodeCount = Math.max(5, taskCount / 20); // حداقل 5، حداکثر taskCount/20
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.7; // 30% cloud nodes
            
            int mips = isCloud ? 3000 + rand.nextInt(7000) : 500 + rand.nextInt(1500);
            int ram = isCloud ? 8192 : 1024 + rand.nextInt(3072);
            long bw = isCloud ? 1000 : 100 + rand.nextInt(400);
            long storage = isCloud ? 100000 : 10000 + rand.nextInt(40000);
            double costPerSec = isCloud ? 0.1 + rand.nextDouble() * 0.4 : 0.02 + rand.nextDouble() * 0.08;
            double latency = isCloud ? 50 + rand.nextDouble() * 100 : 5 + rand.nextDouble() * 20;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static Workflow generateTightDeadlineWorkflow(int taskCount, double deadlineFactor) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 2000 + rand.nextInt(8000);
            long fileSize = 10 + rand.nextInt(90);
            long outputSize = 5 + rand.nextInt(45);
            int pes = 1;
            
            // deadline بر اساس حداقل زمان ممکن اجرا
            double minExecTime = length / 10000.0; // فرض سریع‌ترین node
            double deadline = minExecTime * deadlineFactor;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // وابستگی‌های بیشتر برای افزایش پیچیدگی
            if (i > 1 && rand.nextDouble() < 0.4) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateMixedNodes(int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.6;
            
            // توزیع نامتجانس قدرت
            int mips, ram;
            long bw, storage;
            double costPerSec, latency;
            
            if (isCloud) {
                mips = 5000 + rand.nextInt(15000);
                ram = 16384;
                bw = 2000;
                storage = 200000;
                costPerSec = 0.2 + rand.nextDouble() * 0.6;
                latency = 80 + rand.nextDouble() * 120;
            } else {
                mips = 200 + rand.nextInt(1000); // fog nodes ضعیف‌تر
                ram = 512 + rand.nextInt(1536);
                bw = 50 + rand.nextInt(200);
                storage = 5000 + rand.nextInt(15000);
                costPerSec = 0.01 + rand.nextDouble() * 0.05;
                latency = 2 + rand.nextDouble() * 15;
            }
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static Workflow generateMediumWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 1500 + rand.nextInt(3000);
            long fileSize = 5 + rand.nextInt(50);
            long outputSize = 2 + rand.nextInt(25);
            int pes = 1;
            double deadline = 15.0 + rand.nextDouble() * 25.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.25) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateImbalancedNodes(String scenario, int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        switch (scenario) {
            case "fog_dominant":
                // 90% fog, 10% cloud
                for (int i = 1; i <= nodeCount; i++) {
                    boolean isCloud = i > nodeCount * 0.9;
                    createNodeWithScenario(nodes, i, isCloud, rand, "normal");
                }
                break;
                
            case "cloud_dominant":
                // 10% fog, 90% cloud
                for (int i = 1; i <= nodeCount; i++) {
                    boolean isCloud = i > nodeCount * 0.1;
                    createNodeWithScenario(nodes, i, isCloud, rand, "normal");
                }
                break;
                
            case "few_powerful":
                // کم تعداد ولی بسیار قدرتمند
                for (int i = 1; i <= nodeCount; i++) {
                    boolean isCloud = rand.nextBoolean();
                    createNodeWithScenario(nodes, i, isCloud, rand, "powerful");
                }
                break;
                
            case "many_weak":
                // زیاد ولی همه ضعیف
                for (int i = 1; i <= nodeCount; i++) {
                    boolean isCloud = rand.nextBoolean();
                    createNodeWithScenario(nodes, i, isCloud, rand, "weak");
                }
                break;
                
            case "extreme_hetero":
                // تفاوت شدید در قدرت
                for (int i = 1; i <= nodeCount; i++) {
                    boolean isCloud = rand.nextBoolean();
                    String type = rand.nextBoolean() ? "super_powerful" : "very_weak";
                    createNodeWithScenario(nodes, i, isCloud, rand, type);
                }
                break;
        }
        
        return nodes;
    }
    
    private static void createNodeWithScenario(List<FogNode> nodes, int id, boolean isCloud, Random rand, String type) {
        int mips, ram;
        long bw, storage;
        double costPerSec, latency;
        
        switch (type) {
            case "powerful":
                mips = isCloud ? 15000 + rand.nextInt(20000) : 2000 + rand.nextInt(5000);
                ram = isCloud ? 32768 : 8192;
                bw = isCloud ? 5000 : 1000;
                storage = isCloud ? 500000 : 100000;
                costPerSec = isCloud ? 0.5 + rand.nextDouble() : 0.1 + rand.nextDouble() * 0.2;
                latency = isCloud ? 30 + rand.nextDouble() * 50 : 1 + rand.nextDouble() * 5;
                break;
                
            case "weak":
                mips = isCloud ? 1000 + rand.nextInt(2000) : 100 + rand.nextInt(300);
                ram = isCloud ? 2048 : 256;
                bw = isCloud ? 100 : 10;
                storage = isCloud ? 10000 : 1000;
                costPerSec = isCloud ? 0.01 + rand.nextDouble() * 0.02 : 0.005 + rand.nextDouble() * 0.01;
                latency = isCloud ? 200 + rand.nextDouble() * 300 : 50 + rand.nextDouble() * 100;
                break;
                
            case "super_powerful":
                mips = 50000 + rand.nextInt(50000);
                ram = 65536;
                bw = 10000;
                storage = 1000000;
                costPerSec = 1.0 + rand.nextDouble() * 2.0;
                latency = 10 + rand.nextDouble() * 20;
                break;
                
            case "very_weak":
                mips = 50 + rand.nextInt(100);
                ram = 128;
                bw = 5;
                storage = 500;
                costPerSec = 0.001 + rand.nextDouble() * 0.002;
                latency = 500 + rand.nextDouble() * 1000;
                break;
                
            default: // normal
                mips = isCloud ? 3000 + rand.nextInt(7000) : 500 + rand.nextInt(1500);
                ram = isCloud ? 8192 : 1024;
                bw = isCloud ? 1000 : 100;
                storage = isCloud ? 100000 : 10000;
                costPerSec = isCloud ? 0.1 + rand.nextDouble() * 0.4 : 0.02 + rand.nextDouble() * 0.08;
                latency = isCloud ? 50 + rand.nextDouble() * 100 : 5 + rand.nextDouble() * 20;
                break;
        }
        
        FogNode node = new FogNode(id, mips, ram, bw, storage, isCloud, costPerSec, latency);
        nodes.add(node);
    }
    
    private static Workflow generateDataIntensiveWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 1000 + rand.nextInt(3000);
            // داده‌های بسیار زیاد
            long fileSize = 100 + rand.nextInt(900); // 100MB - 1GB
            long outputSize = 50 + rand.nextInt(450); // 50MB - 500MB
            int pes = 1;
            double deadline = 20.0 + rand.nextDouble() * 30.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // وابستگی‌های زیاد برای انتقال داده بیشتر
            if (i > 1 && rand.nextDouble() < 0.5) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateHighLatencyNodes(int nodeCount, double latencyMultiplier) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.5;
            
            int mips = isCloud ? 4000 + rand.nextInt(6000) : 600 + rand.nextInt(1400);
            int ram = isCloud ? 8192 : 1024;
            long bw = isCloud ? 500 : 50; // bandwidth کم برای تست
            long storage = isCloud ? 100000 : 10000;
            double costPerSec = isCloud ? 0.15 : 0.03;
            
            // latency بسیار زیاد
            double baseLatency = isCloud ? 100 : 20;
            double latency = baseLatency * latencyMultiplier;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static Workflow generateEnergyIntensiveWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            // تسک‌های سنگین که انرژی زیاد مصرف می‌کنند
            int length = 5000 + rand.nextInt(15000); // heavy computation
            long fileSize = 20 + rand.nextInt(80);
            long outputSize = 10 + rand.nextInt(40);
            int pes = 1;
            double deadline = 30.0 + rand.nextDouble() * 60.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.3) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateEnergyAwareNodes(int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.6;
            
            int mips = isCloud ? 3000 + rand.nextInt(7000) : 500 + rand.nextInt(1500);
            int ram = isCloud ? 8192 : 1024;
            long bw = isCloud ? 1000 : 100;
            long storage = isCloud ? 100000 : 10000;
            double costPerSec = isCloud ? 0.1 + rand.nextDouble() * 0.4 : 0.02 + rand.nextDouble() * 0.08;
            double latency = isCloud ? 50 + rand.nextDouble() * 100 : 5 + rand.nextDouble() * 20;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            
            // تنظیم مصرف انرژی
            double energyPerSec = isCloud ? 200 + rand.nextDouble() * 300 : 50 + rand.nextDouble() * 150;
            node.setEnergyPerSec(energyPerSec);
            
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static void saveScalabilityResult(int taskCount, EnhancedEPOCEIS.SchedulingResult result, long executionTime) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(RESULTS_DIR + "scalability_results.csv", true));
            writer.printf("%d,%.2f,%d,%d\n", taskCount, result.totalCost, executionTime, result.taskAssignments.size());
            writer.close();
        } catch (IOException e) {
            System.err.println("Error saving scalability result: " + e.getMessage());
        }
    }
    
    private static int calculateDeadlineViolations(Workflow workflow, EnhancedEPOCEIS.SchedulingResult result) {
        int violations = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            if (result.startTimes.containsKey(task.getId())) {
                double startTime = result.startTimes.get(task.getId());
                double finishTime = startTime + task.getLength() / 1000.0; // approximate execution time
                
                if (finishTime > task.getDeadline()) {
                    violations++;
                }
            }
        }
        
        return violations;
    }
    
    private static void analyzeTaskDistribution(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes, String scenario) {
        // تحلیل نحوه توزیع تسک‌ها روی nodes مختلف
        Map<Integer, Integer> nodeTaskCount = new HashMap<>();
        
        for (Integer nodeId : result.taskAssignments.values()) {
            nodeTaskCount.merge(nodeId, 1, Integer::sum);
        }
        
        System.out.printf("    Task Distribution in %s:\n", scenario);
        for (FogNode node : nodes) {
            int taskCount = nodeTaskCount.getOrDefault(node.getId(), 0);
            System.out.printf("      Node %d (%s): %d tasks\n", 
                node.getId(), node.isCloud() ? "Cloud" : "Fog", taskCount);
        }
    }
    
    private static double calculateAverageLatency(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, List<FogNode> nodes) {
        double totalLatency = 0.0;
        int taskCount = 0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        for (Map.Entry<Integer, Integer> entry : result.taskAssignments.entrySet()) {
            FogNode assignedNode = nodeMap.get(entry.getValue());
            if (assignedNode != null) {
                totalLatency += assignedNode.getLatencyMs();
                taskCount++;
            }
        }
        
        return taskCount > 0 ? totalLatency / taskCount : 0.0;
    }
    
    private static double calculateTotalEnergyConsumption(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, List<FogNode> nodes) {
        double totalEnergy = 0.0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        for (IIoTTask task : workflow.getAllTasks()) {
            Integer nodeId = result.taskAssignments.get(task.getId());
            if (nodeId != null) {
                FogNode node = nodeMap.get(nodeId);
                if (node != null) {
                    double executionTime = (double) task.getLength() / node.getMips();
                    // فرض: مصرف انرژی متناسب با زمان اجرا و قدرت پردازشگر
                    double energy = executionTime * node.getMips() * 0.001; // تقریبی
                    totalEnergy += energy;
                }
            }
        }
        
        return totalEnergy;
    }
    
    // Helper methods for remaining test scenarios
    
    private static Workflow generateComplexWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 2000 + rand.nextInt(4000);
            long fileSize = 10 + rand.nextInt(70);
            long outputSize = 5 + rand.nextInt(35);
            int pes = 1;
            double deadline = 25.0 + rand.nextDouble() * 35.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            // ایجاد وابستگی‌های پیچیده
            if (i > 1) {
                int dependencyCount = 1 + rand.nextInt(Math.min(3, i - 1));
                Set<Integer> parents = new HashSet<>();
                
                for (int j = 0; j < dependencyCount; j++) {
                    int parentId = 1 + rand.nextInt(i - 1);
                    if (!parents.contains(parentId)) {
                        workflow.addDependency(parentId, i);
                        parents.add(parentId);
                    }
                }
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateTopologyBasedNodes(String topology, int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = determineCloudByTopology(topology, i, nodeCount);
            
            int mips = isCloud ? 4000 + rand.nextInt(6000) : 600 + rand.nextInt(1400);
            int ram = isCloud ? 8192 : 1024;
            long bw = calculateBandwidthByTopology(topology, i, nodeCount, rand);
            long storage = isCloud ? 100000 : 10000;
            double costPerSec = isCloud ? 0.12 : 0.025;
            double latency = calculateLatencyByTopology(topology, i, nodeCount, rand);
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static boolean determineCloudByTopology(String topology, int nodeId, int totalNodes) {
        switch (topology) {
            case "star":
                return nodeId == 1; // مرکز ستاره cloud است
            case "hierarchical":
                return nodeId <= totalNodes / 4; // 25% cloud nodes در بالای سلسله مراتب
            default:
                return nodeId > totalNodes * 0.7; // 30% cloud nodes
        }
    }
    
    private static long calculateBandwidthByTopology(String topology, int nodeId, int totalNodes, Random rand) {
        switch (topology) {
            case "mesh":
                return 1000 + rand.nextInt(1000); // bandwidth زیاد برای mesh
            case "ring":
                return 200 + rand.nextInt(300); // bandwidth متوسط
            case "tree":
                return nodeId <= totalNodes / 2 ? 800 : 300; // والدین bandwidth بیشتر
            default:
                return 100 + rand.nextInt(400);
        }
    }
    
    private static double calculateLatencyByTopology(String topology, int nodeId, int totalNodes, Random rand) {
        switch (topology) {
            case "star":
                return nodeId == 1 ? 10 : 30 + rand.nextDouble() * 50; // مرکز latency کم
            case "mesh":
                return 5 + rand.nextDouble() * 15; // latency کم برای mesh
            case "ring":
                return 20 + rand.nextDouble() * 60; // latency متغیر
            default:
                return 15 + rand.nextDouble() * 45;
        }
    }
    
    private static void analyzeTopologyPerformance(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes, String topology) {
        int fogTasks = 0, cloudTasks = 0;
        double totalLatency = 0.0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        for (Integer nodeId : result.taskAssignments.values()) {
            FogNode node = nodeMap.get(nodeId);
            if (node != null) {
                if (node.isCloud()) {
                    cloudTasks++;
                } else {
                    fogTasks++;
                }
                totalLatency += node.getLatencyMs();
            }
        }
        
        double avgLatency = result.taskAssignments.size() > 0 ? 
            totalLatency / result.taskAssignments.size() : 0.0;
        
        System.out.printf("    %s Topology Analysis:\n", topology);
        System.out.printf("      Fog tasks: %d, Cloud tasks: %d\n", fogTasks, cloudTasks);
        System.out.printf("      Average latency: %.2f ms\n", avgLatency);
        System.out.printf("      Total cost: %.2f\n", result.totalCost);
    }
    
    private static Workflow generateDynamicWorkflow(int timeStep, int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random(timeStep * 1000); // seed برای تکرارپذیری
        
        // تغییر الگوی workload بر اساس time step
        double loadFactor = 1.0 + 0.5 * Math.sin(timeStep * Math.PI / 5); // چرخه‌ای
        
        for (int i = 1; i <= taskCount; i++) {
            int length = (int)(1500 * loadFactor) + rand.nextInt(2000);
            long fileSize = 8 + rand.nextInt(40);
            long outputSize = 4 + rand.nextInt(20);
            int pes = 1;
            
            // deadline سخت‌تر در time steps بالا
            double deadlineBase = 20.0 - timeStep * 0.5;
            double deadline = Math.max(5.0, deadlineBase + rand.nextDouble() * 20.0);
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.3) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateDynamicNodes(int timeStep, int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random(timeStep * 2000);
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.6;
            
            // ظرفیت nodes با زمان تغییر می‌کند (شبیه‌سازی load balancing)
            double capacityFactor = 0.7 + 0.6 * Math.cos(timeStep * Math.PI / 3);
            
            int mips = (int)((isCloud ? 4000 : 800) * capacityFactor) + rand.nextInt(2000);
            int ram = isCloud ? 8192 : 1024;
            long bw = isCloud ? 1000 : 150;
            long storage = isCloud ? 100000 : 10000;
            
            // هزینه با زمان تغییر می‌کند (peak hours)
            double costMultiplier = 1.0 + 0.3 * Math.sin(timeStep * Math.PI / 4);
            double costPerSec = (isCloud ? 0.1 : 0.02) * costMultiplier;
            
            double latency = isCloud ? 60 + rand.nextDouble() * 80 : 8 + rand.nextDouble() * 25;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    // Helper methods for fault tolerance test
    
    private static Workflow generateFaultTolerantWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length = 1800 + rand.nextInt(3200);
            long fileSize = 12 + rand.nextInt(48);
            long outputSize = 6 + rand.nextInt(24);
            int pes = 1;
            double deadline = 18.0 + rand.nextDouble() * 32.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.35) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateReliableNodes(int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.65;
            
            int mips = isCloud ? 3500 + rand.nextInt(6500) : 550 + rand.nextInt(1450);
            int ram = isCloud ? 8192 : 1024;
            long bw = isCloud ? 1200 : 120;
            long storage = isCloud ? 120000 : 12000;
            double costPerSec = isCloud ? 0.11 : 0.022;
            double latency = isCloud ? 55 + rand.nextDouble() * 95 : 6 + rand.nextDouble() * 22;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static List<FogNode> simulateNodeFailures(List<FogNode> originalNodes, double failureRate) {
        List<FogNode> workingNodes = new ArrayList<>();
        Random rand = new Random();
        
        for (FogNode node : originalNodes) {
            if (rand.nextDouble() > failureRate) { // node کار می‌کند
                workingNodes.add(node);
            }
        }
        
        // اطمینان از وجود حداقل یک node
        if (workingNodes.isEmpty() && !originalNodes.isEmpty()) {
            workingNodes.add(originalNodes.get(0));
        }
        
        return workingNodes;
    }
    
    private static double calculateAdaptationScore(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> originalNodes, List<FogNode> workingNodes) {
        double originalCapacity = originalNodes.stream().mapToInt(FogNode::getMips).sum();
        double workingCapacity = workingNodes.stream().mapToInt(FogNode::getMips).sum();
        
        double capacityRatio = workingCapacity / originalCapacity;
        
        // امتیاز adaptation بر اساس کیفیت استفاده از منابع باقیمانده
        return capacityRatio * (1.0 - result.totalCost / 1000.0); // فرض: حداکثر cost 1000
    }
    
    // Helper methods for conflicting objectives test
    
    private static Workflow generateConflictingWorkflow(String scenario, int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            int length, pes;
            long fileSize, outputSize;
            double deadline;
            
            switch (scenario) {
                case "cost_vs_latency":
                    // تسک‌هایی که نیاز به سرعت دارند ولی باید ارزان باشند
                    length = 3000 + rand.nextInt(7000); // heavy tasks
                    fileSize = 50 + rand.nextInt(150);
                    outputSize = 25 + rand.nextInt(75);
                    deadline = 8.0 + rand.nextDouble() * 12.0; // tight deadlines
                    break;
                    
                case "energy_vs_speed":
                    // تسک‌هایی که سریع باید اجرا شوند ولی انرژی کم مصرف کنند
                    length = 4000 + rand.nextInt(8000);
                    fileSize = 30 + rand.nextInt(100);
                    outputSize = 15 + rand.nextInt(50);
                    deadline = 5.0 + rand.nextDouble() * 10.0; // very tight
                    break;
                    
                case "reliability_vs_cost":
                    // تسک‌های مهم که باید قابل اطمینان باشند ولی ارزان
                    length = 2000 + rand.nextInt(4000);
                    fileSize = 40 + rand.nextInt(120);
                    outputSize = 20 + rand.nextInt(60);
                    deadline = 15.0 + rand.nextDouble() * 25.0;
                    break;
                    
                default:
                    length = 2500 + rand.nextInt(5000);
                    fileSize = 20 + rand.nextInt(80);
                    outputSize = 10 + rand.nextInt(40);
                    deadline = 12.0 + rand.nextDouble() * 18.0;
                    break;
            }
            
            pes = 1;
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.4) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateConflictingNodes(String scenario, int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            boolean isCloud = i > nodeCount * 0.6;
            
            int mips;
            int ram;
            long bw, storage;
            double costPerSec, latency;
            
            switch (scenario) {
                case "cost_vs_latency":
                    // nodes ارزان ولی latency زیاد یا گران ولی سریع
                    if (rand.nextBoolean()) {
                        // cheap but slow
                        mips = isCloud ? 1000 + rand.nextInt(2000) : 200 + rand.nextInt(500);
                        costPerSec = 0.01 + rand.nextDouble() * 0.03;
                        latency = 100 + rand.nextDouble() * 200;
                    } else {
                        // fast but expensive
                        mips = isCloud ? 8000 + rand.nextInt(12000) : 1500 + rand.nextInt(2500);
                        costPerSec = 0.3 + rand.nextDouble() * 0.7;
                        latency = 5 + rand.nextDouble() * 15;
                    }
                    break;
                    
                case "energy_vs_speed":
                    // سریع ولی پر مصرف یا کند ولی کم مصرف
                    if (rand.nextBoolean()) {
                        // fast but power hungry
                        mips = isCloud ? 10000 + rand.nextInt(15000) : 2000 + rand.nextInt(3000);
                        costPerSec = 0.2 + rand.nextDouble() * 0.5;
                        latency = 2 + rand.nextDouble() * 8;
                    } else {
                        // slow but energy efficient
                        mips = isCloud ? 1500 + rand.nextInt(2500) : 300 + rand.nextInt(700);
                        costPerSec = 0.02 + rand.nextDouble() * 0.05;
                        latency = 50 + rand.nextDouble() * 100;
                    }
                    break;
                    
                default:
                    mips = isCloud ? 3000 + rand.nextInt(7000) : 500 + rand.nextInt(1500);
                    costPerSec = isCloud ? 0.1 + rand.nextDouble() * 0.4 : 0.02 + rand.nextDouble() * 0.08;
                    latency = isCloud ? 50 + rand.nextDouble() * 100 : 5 + rand.nextDouble() * 20;
                    break;
            }
            
            ram = isCloud ? 8192 : 1024;
            bw = isCloud ? 1000 : 100;
            storage = isCloud ? 100000 : 10000;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static void analyzeObjectiveConflicts(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, List<FogNode> nodes, String scenario) {
        double totalCost = result.totalCost;
        double avgLatency = calculateAverageLatency(result, workflow, nodes);
        double totalEnergy = calculateTotalEnergyConsumption(result, workflow, nodes);
        
        System.out.printf("    %s Conflict Analysis:\n", scenario);
        System.out.printf("      Total Cost: %.2f\n", totalCost);
        System.out.printf("      Average Latency: %.2f ms\n", avgLatency);
        System.out.printf("      Energy Consumption: %.2f Wh\n", totalEnergy);
        
        // تحلیل trade-off
        double costLatencyRatio = totalCost / Math.max(avgLatency, 1.0);
        double energySpeedRatio = totalEnergy / Math.max(avgLatency, 1.0);
        
        System.out.printf("      Cost/Latency Ratio: %.4f\n", costLatencyRatio);
        System.out.printf("      Energy/Speed Ratio: %.4f\n", energySpeedRatio);
    }
    
    // Helper methods for extreme heterogeneity test
    
    private static Workflow generateHeterogeneousWorkflow(int taskCount) {
        Workflow workflow = new Workflow();
        Random rand = new Random();
        
        for (int i = 1; i <= taskCount; i++) {
            // تسک‌های بسیار متنوع
            int complexity = rand.nextInt(5); // 5 سطح پیچیدگی
            
            int length = 500 + complexity * 2000 + rand.nextInt(1000);
            long fileSize = 5 + complexity * 20 + rand.nextInt(30);
            long outputSize = 2 + complexity * 10 + rand.nextInt(15);
            int pes = 1;
            double deadline = 10.0 + complexity * 8.0 + rand.nextDouble() * 20.0;
            
            IIoTTask task = new IIoTTask(i, length, fileSize, outputSize, pes, deadline);
            workflow.addTask(task);
            
            if (i > 1 && rand.nextDouble() < 0.3) {
                int parentId = 1 + rand.nextInt(i - 1);
                workflow.addDependency(parentId, i);
            }
        }
        
        return workflow;
    }
    
    private static List<FogNode> generateExtremelyDiverseNodes(int diversityLevel, int nodeCount) {
        List<FogNode> nodes = new ArrayList<>();
        Random rand = new Random();
        
        for (int i = 1; i <= nodeCount; i++) {
            // انتخاب نوع node از انواع مختلف
            int nodeType = rand.nextInt(diversityLevel);
            boolean isCloud = rand.nextBoolean();
            
            // تعریف خصوصیات بر اساس نوع
            int baseMips = 100 + nodeType * 500;
            int mips = baseMips + rand.nextInt(baseMips);
            
            int baseRam = 256 + nodeType * 512;
            int ram = isCloud ? baseRam * 4 : baseRam;
            
            long baseBw = 10 + nodeType * 50;
            long bw = isCloud ? baseBw * 10 : baseBw;
            
            long baseStorage = 1000 + nodeType * 5000;
            long storage = isCloud ? baseStorage * 20 : baseStorage;
            
            double baseCost = 0.001 + nodeType * 0.02;
            double costPerSec = baseCost + rand.nextDouble() * baseCost;
            
            double baseLatency = 1 + nodeType * 10;
            double latency = baseLatency + rand.nextDouble() * baseLatency;
            
            FogNode node = new FogNode(i, mips, ram, bw, storage, isCloud, costPerSec, latency);
            nodes.add(node);
        }
        
        return nodes;
    }
    
    private static double calculateDiversityUtilization(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        Set<Integer> usedNodes = new HashSet<>(result.taskAssignments.values());
        
        // محاسبه تنوع nodes استفاده شده
        if (usedNodes.isEmpty()) return 0.0;
        
        double totalCapacity = 0.0;
        double usedCapacity = 0.0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
            totalCapacity += node.getMips();
        }
        
        for (Integer nodeId : usedNodes) {
            FogNode node = nodeMap.get(nodeId);
            if (node != null) {
                usedCapacity += node.getMips();
            }
        }
        
        return usedCapacity / totalCapacity;
    }
}
