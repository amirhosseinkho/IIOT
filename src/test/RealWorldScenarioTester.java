package test;

import algorithms.*;
import core.*;
import simulation.*;
import java.util.*;
import java.io.*;

/**
 * Real-World Scenario Tester
 * 
 * تست سناریوهای واقعی صنعتی و کاربردی
 */
public class RealWorldScenarioTester {
    
    public static void main(String[] args) {
        System.out.println("🏭 REAL-WORLD INDUSTRIAL SCENARIOS TEST");
        System.out.println("=======================================");
        
        try {
            // 1. سناریو کارخانه هوشمند
            runSmartFactoryScenario();
            
            // 2. سناریو مراقبت بهداشتی
            runHealthcareMonitoringScenario();
            
            // 3. سناریو شهر هوشمند
            runSmartCityScenario();
            
            // 4. سناریو کشاورزی هوشمند
            runSmartAgricultureScenario();
            
            // 5. سناریو حمل و نقل هوشمند
            runSmartTransportationScenario();
            
            // 6. سناریو امنیت و نظارت
            runSecuritySurveillanceScenario();
            
            System.out.println("\n🎉 All real-world scenarios completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Real-world testing failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 1. سناریو کارخانه هوشمند - Smart Manufacturing
     */
    private static void runSmartFactoryScenario() {
        System.out.println("\n🏭 SMART FACTORY SCENARIO");
        System.out.println("Quality control, predictive maintenance, production optimization");
        System.out.println("=============================================================");
        
        // ایجاد workflow مخصوص کارخانه
        Workflow factoryWorkflow = createSmartFactoryWorkflow();
        List<FogNode> factoryNodes = createFactoryInfrastructure();
        
        System.out.printf("📊 Factory Setup: %d processes, %d edge devices\n", 
            factoryWorkflow.getAllTasks().size(), factoryNodes.size());
        
        try {
            EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(factoryWorkflow, factoryNodes);
            
            long startTime = System.currentTimeMillis();
            EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
            long duration = System.currentTimeMillis() - startTime;
            
            analyzeFactoryResults(result, factoryWorkflow, factoryNodes, duration);
            
        } catch (Exception e) {
            System.out.printf("❌ Factory scenario failed: %s\n", e.getMessage());
        }
    }
    
    private static Workflow createSmartFactoryWorkflow() {
        Workflow workflow = new Workflow();
        Random rand = new Random(12345); // seed for reproducibility
        
        // Production line tasks
        addFactoryTask(workflow, 1, "sensor_data_collection", 500, 10, 5, 2.0, rand);
        addFactoryTask(workflow, 2, "quality_inspection", 2000, 50, 20, 5.0, rand);
        addFactoryTask(workflow, 3, "defect_detection_ai", 8000, 100, 30, 3.0, rand);
        addFactoryTask(workflow, 4, "predictive_maintenance", 5000, 200, 50, 10.0, rand);
        addFactoryTask(workflow, 5, "production_optimization", 6000, 150, 80, 8.0, rand);
        addFactoryTask(workflow, 6, "inventory_management", 3000, 80, 40, 15.0, rand);
        addFactoryTask(workflow, 7, "energy_optimization", 4000, 120, 60, 12.0, rand);
        addFactoryTask(workflow, 8, "safety_monitoring", 1500, 30, 15, 1.0, rand);
        addFactoryTask(workflow, 9, "supply_chain_sync", 2500, 300, 100, 20.0, rand);
        addFactoryTask(workflow, 10, "real_time_dashboard", 1000, 20, 10, 0.5, rand);
        
        // Dependencies - production pipeline
        workflow.addDependency(1, 2); // sensor data → quality inspection
        workflow.addDependency(2, 3); // quality → defect detection
        workflow.addDependency(1, 4); // sensor data → predictive maintenance
        workflow.addDependency(3, 5); // defect detection → production optimization
        workflow.addDependency(4, 5); // maintenance → production optimization
        workflow.addDependency(5, 6); // production → inventory
        workflow.addDependency(1, 7); // sensor data → energy optimization
        workflow.addDependency(1, 8); // sensor data → safety monitoring
        workflow.addDependency(6, 9); // inventory → supply chain
        workflow.addDependency(8, 10); // safety → dashboard
        workflow.addDependency(3, 10); // defect detection → dashboard
        
        return workflow;
    }
    
    private static void addFactoryTask(Workflow workflow, int id, String name, int computeIntensity, 
                                       long dataSize, long outputSize, double deadline, Random rand) {
        int length = computeIntensity + rand.nextInt(computeIntensity / 2);
        long fileSize = dataSize + rand.nextInt((int)dataSize / 2);
        long output = outputSize + rand.nextInt((int)outputSize / 2);
        
        IIoTTask task = new IIoTTask(id, length, fileSize, output, 1, deadline);
        workflow.addTask(task);
    }
    
    private static List<FogNode> createFactoryInfrastructure() {
        List<FogNode> nodes = new ArrayList<>();
        
        // Edge devices (production line)
        nodes.add(new FogNode(1, 800, 1024, 100, 5000, false, 0.02, 2.0));   // PLC controller
        nodes.add(new FogNode(2, 1200, 2048, 200, 10000, false, 0.03, 3.0)); // Vision system
        nodes.add(new FogNode(3, 600, 512, 50, 2000, false, 0.015, 1.5));    // Sensor gateway
        nodes.add(new FogNode(4, 1500, 4096, 300, 15000, false, 0.04, 4.0)); // Edge AI unit
        
        // Fog nodes (factory floor)
        nodes.add(new FogNode(5, 3000, 8192, 500, 50000, false, 0.08, 8.0));  // Factory server
        nodes.add(new FogNode(6, 2500, 6144, 400, 40000, false, 0.06, 6.0));  // Quality control
        nodes.add(new FogNode(7, 2000, 4096, 300, 30000, false, 0.05, 5.0));  // Maintenance hub
        
        // Cloud (data center)
        nodes.add(new FogNode(8, 8000, 32768, 2000, 500000, true, 0.15, 50.0));  // Main cloud
        nodes.add(new FogNode(9, 6000, 16384, 1500, 300000, true, 0.12, 45.0));  // Backup cloud
        
        return nodes;
    }
    
    /**
     * 2. سناریو مراقبت بهداشتی - Healthcare Monitoring
     */
    private static void runHealthcareMonitoringScenario() {
        System.out.println("\n🏥 HEALTHCARE MONITORING SCENARIO");
        System.out.println("Patient monitoring, emergency detection, telemedicine");
        System.out.println("===================================================");
        
        Workflow healthWorkflow = createHealthcareWorkflow();
        List<FogNode> healthNodes = createHealthcareInfrastructure();
        
        System.out.printf("📊 Healthcare Setup: %d monitoring tasks, %d devices\n", 
            healthWorkflow.getAllTasks().size(), healthNodes.size());
        
        try {
            EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(healthWorkflow, healthNodes);
            
            long startTime = System.currentTimeMillis();
            EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
            long duration = System.currentTimeMillis() - startTime;
            
            analyzeHealthcareResults(result, healthWorkflow, healthNodes, duration);
            
        } catch (Exception e) {
            System.out.printf("❌ Healthcare scenario failed: %s\n", e.getMessage());
        }
    }
    
    private static Workflow createHealthcareWorkflow() {
        Workflow workflow = new Workflow();
        Random rand = new Random(23456);
        
        // Critical care tasks with tight deadlines
        addHealthTask(workflow, 1, "vital_signs_monitoring", 300, 5, 2, 0.5, rand);
        addHealthTask(workflow, 2, "ecg_analysis", 1500, 20, 5, 1.0, rand);
        addHealthTask(workflow, 3, "emergency_detection", 2000, 15, 8, 0.8, rand);
        addHealthTask(workflow, 4, "patient_risk_assessment", 4000, 50, 25, 3.0, rand);
        addHealthTask(workflow, 5, "medication_monitoring", 800, 10, 3, 2.0, rand);
        addHealthTask(workflow, 6, "image_processing_xray", 10000, 500, 100, 10.0, rand);
        addHealthTask(workflow, 7, "telemedicine_session", 2500, 1000, 800, 5.0, rand);
        addHealthTask(workflow, 8, "health_analytics", 6000, 200, 50, 15.0, rand);
        addHealthTask(workflow, 9, "alert_notification", 200, 2, 1, 0.2, rand);
        addHealthTask(workflow, 10, "data_encryption", 1000, 100, 100, 1.5, rand);
        
        // Dependencies for healthcare workflow
        workflow.addDependency(1, 2); // vitals → ECG
        workflow.addDependency(2, 3); // ECG → emergency detection
        workflow.addDependency(1, 4); // vitals → risk assessment
        workflow.addDependency(3, 9); // emergency → alert
        workflow.addDependency(4, 5); // risk → medication
        workflow.addDependency(6, 8); // X-ray → analytics
        workflow.addDependency(8, 10); // analytics → encryption
        workflow.addDependency(7, 10); // telemedicine → encryption
        
        return workflow;
    }
    
    private static void addHealthTask(Workflow workflow, int id, String name, int computeIntensity,
                                      long dataSize, long outputSize, double deadline, Random rand) {
        int length = computeIntensity + rand.nextInt(computeIntensity / 3);
        long fileSize = dataSize + rand.nextInt((int)Math.max(1, dataSize / 3));
        long output = outputSize + rand.nextInt((int)Math.max(1, outputSize / 3));
        
        IIoTTask task = new IIoTTask(id, length, fileSize, output, 1, deadline);
        workflow.addTask(task);
    }
    
    private static List<FogNode> createHealthcareInfrastructure() {
        List<FogNode> nodes = new ArrayList<>();
        
        // Wearable devices
        nodes.add(new FogNode(1, 200, 256, 20, 500, false, 0.005, 1.0));    // Smartwatch
        nodes.add(new FogNode(2, 150, 128, 15, 300, false, 0.003, 0.8));    // Heart monitor
        nodes.add(new FogNode(3, 100, 64, 10, 100, false, 0.002, 0.5));     // Sensor patch
        
        // Medical devices
        nodes.add(new FogNode(4, 1000, 2048, 100, 8000, false, 0.02, 2.0)); // Bedside monitor
        nodes.add(new FogNode(5, 2000, 4096, 200, 20000, false, 0.04, 3.0)); // X-ray machine
        nodes.add(new FogNode(6, 1500, 3072, 150, 15000, false, 0.03, 2.5)); // Ultrasound
        
        // Hospital fog nodes
        nodes.add(new FogNode(7, 4000, 8192, 800, 100000, false, 0.08, 5.0)); // Ward server
        nodes.add(new FogNode(8, 5000, 16384, 1000, 200000, false, 0.1, 8.0)); // ICU server
        
        // Cloud services
        nodes.add(new FogNode(9, 10000, 32768, 3000, 1000000, true, 0.2, 40.0)); // Health cloud
        nodes.add(new FogNode(10, 8000, 24576, 2500, 800000, true, 0.18, 35.0)); // Backup cloud
        
        return nodes;
    }
    
    /**
     * 3. سناریو شهر هوشمند - Smart City
     */
    private static void runSmartCityScenario() {
        System.out.println("\n🌆 SMART CITY SCENARIO");
        System.out.println("Traffic management, environmental monitoring, public safety");
        System.out.println("========================================================");
        
        Workflow cityWorkflow = createSmartCityWorkflow();
        List<FogNode> cityNodes = createCityInfrastructure();
        
        System.out.printf("📊 Smart City Setup: %d city services, %d infrastructure nodes\n", 
            cityWorkflow.getAllTasks().size(), cityNodes.size());
        
        try {
            EnhancedEPOCEIS algorithm = new EnhancedEPOCEIS(cityWorkflow, cityNodes);
            
            long startTime = System.currentTimeMillis();
            EnhancedEPOCEIS.SchedulingResult result = algorithm.schedule();
            long duration = System.currentTimeMillis() - startTime;
            
            analyzeCityResults(result, cityWorkflow, cityNodes, duration);
            
        } catch (Exception e) {
            System.out.printf("❌ Smart city scenario failed: %s\n", e.getMessage());
        }
    }
    
    private static Workflow createSmartCityWorkflow() {
        Workflow workflow = new Workflow();
        Random rand = new Random(34567);
        
        // City service tasks
        addCityTask(workflow, 1, "traffic_flow_analysis", 3000, 200, 50, 5.0, rand);
        addCityTask(workflow, 2, "air_quality_monitoring", 1000, 30, 15, 8.0, rand);
        addCityTask(workflow, 3, "noise_level_detection", 800, 25, 10, 10.0, rand);
        addCityTask(workflow, 4, "parking_optimization", 2000, 100, 30, 15.0, rand);
        addCityTask(workflow, 5, "energy_grid_management", 5000, 300, 100, 12.0, rand);
        addCityTask(workflow, 6, "waste_collection_routing", 2500, 150, 40, 20.0, rand);
        addCityTask(workflow, 7, "public_safety_analytics", 4000, 500, 200, 3.0, rand);
        addCityTask(workflow, 8, "emergency_response", 1500, 80, 20, 1.0, rand);
        addCityTask(workflow, 9, "citizen_app_services", 1200, 60, 40, 6.0, rand);
        addCityTask(workflow, 10, "infrastructure_monitoring", 3500, 250, 80, 25.0, rand);
        addCityTask(workflow, 11, "weather_prediction", 7000, 400, 150, 30.0, rand);
        addCityTask(workflow, 12, "city_dashboard_update", 800, 180, 180, 4.0, rand);
        
        // City workflow dependencies
        workflow.addDependency(1, 4); // traffic → parking
        workflow.addDependency(1, 8); // traffic → emergency response
        workflow.addDependency(2, 5); // air quality → energy grid
        workflow.addDependency(3, 7); // noise → safety analytics
        workflow.addDependency(7, 8); // safety → emergency
        workflow.addDependency(10, 5); // infrastructure → energy
        workflow.addDependency(11, 2); // weather → air quality
        workflow.addDependency(5, 12); // energy → dashboard
        workflow.addDependency(8, 12); // emergency → dashboard
        workflow.addDependency(9, 12); // citizen services → dashboard
        
        return workflow;
    }
    
    private static void addCityTask(Workflow workflow, int id, String name, int computeIntensity,
                                    long dataSize, long outputSize, double deadline, Random rand) {
        int length = computeIntensity + rand.nextInt(computeIntensity / 4);
        long fileSize = dataSize + rand.nextInt((int)Math.max(1, dataSize / 4));
        long output = outputSize + rand.nextInt((int)Math.max(1, outputSize / 4));
        
        IIoTTask task = new IIoTTask(id, length, fileSize, output, 1, deadline);
        workflow.addTask(task);
    }
    
    private static List<FogNode> createCityInfrastructure() {
        List<FogNode> nodes = new ArrayList<>();
        
        // Street-level sensors
        nodes.add(new FogNode(1, 300, 512, 50, 1000, false, 0.01, 2.0));    // Traffic sensor
        nodes.add(new FogNode(2, 250, 256, 30, 500, false, 0.008, 1.5));    // Air quality sensor
        nodes.add(new FogNode(3, 200, 128, 20, 300, false, 0.006, 1.0));    // Noise sensor
        nodes.add(new FogNode(4, 400, 1024, 80, 2000, false, 0.015, 3.0));  // Smart streetlight
        
        // District fog nodes
        nodes.add(new FogNode(5, 2000, 4096, 400, 30000, false, 0.05, 8.0)); // District hub
        nodes.add(new FogNode(6, 2500, 6144, 500, 40000, false, 0.06, 10.0)); // Safety center
        nodes.add(new FogNode(7, 1800, 3072, 350, 25000, false, 0.04, 6.0)); // Traffic control
        nodes.add(new FogNode(8, 3000, 8192, 600, 50000, false, 0.07, 12.0)); // Emergency services
        
        // City-level cloud
        nodes.add(new FogNode(9, 12000, 65536, 5000, 2000000, true, 0.25, 60.0)); // City data center
        nodes.add(new FogNode(10, 10000, 49152, 4000, 1500000, true, 0.22, 55.0)); // Backup center
        nodes.add(new FogNode(11, 8000, 32768, 3000, 1000000, true, 0.18, 45.0)); // Analytics cloud
        
        return nodes;
    }
    
    // Analysis methods
    
    private static void analyzeFactoryResults(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, 
                                              List<FogNode> nodes, long duration) {
        System.out.println("\n📈 Factory Performance Analysis:");
        
        double productionEfficiency = calculateProductionEfficiency(result, workflow, nodes);
        double safetyScore = calculateSafetyScore(result, workflow);
        double energyEfficiency = calculateEnergyEfficiency(result, nodes);
        
        System.out.printf("  🏭 Production Efficiency: %.1f%%\n", productionEfficiency * 100);
        System.out.printf("  🛡️ Safety Score: %.1f%%\n", safetyScore * 100);
        System.out.printf("  ⚡ Energy Efficiency: %.1f%%\n", energyEfficiency * 100);
        System.out.printf("  💰 Total Cost: $%.2f\n", result.totalCost);
        System.out.printf("  ⏱️ Execution Time: %d ms\n", duration);
        
        analyzeTaskDistributionByType(result, nodes, "Factory");
    }
    
    private static void analyzeHealthcareResults(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, 
                                                 List<FogNode> nodes, long duration) {
        System.out.println("\n📈 Healthcare Performance Analysis:");
        
        double criticalResponseTime = calculateCriticalResponseTime(result, workflow);
        double patientSafetyScore = calculatePatientSafety(result, workflow);
        double dataPrivacyScore = calculateDataPrivacy(result, nodes);
        
        System.out.printf("  🚨 Critical Response Time: %.2f seconds\n", criticalResponseTime);
        System.out.printf("  👥 Patient Safety Score: %.1f%%\n", patientSafetyScore * 100);
        System.out.printf("  🔒 Data Privacy Score: %.1f%%\n", dataPrivacyScore * 100);
        System.out.printf("  💰 Total Cost: $%.2f\n", result.totalCost);
        System.out.printf("  ⏱️ Execution Time: %d ms\n", duration);
        
        analyzeTaskDistributionByType(result, nodes, "Healthcare");
    }
    
    private static void analyzeCityResults(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow, 
                                           List<FogNode> nodes, long duration) {
        System.out.println("\n📈 Smart City Performance Analysis:");
        
        double citizenSatisfaction = calculateCitizenSatisfaction(result, workflow);
        double infrastructureEfficiency = calculateInfrastructureEfficiency(result, nodes);
        double emergencyReadiness = calculateEmergencyReadiness(result, workflow);
        
        System.out.printf("  😊 Citizen Satisfaction: %.1f%%\n", citizenSatisfaction * 100);
        System.out.printf("  🏗️ Infrastructure Efficiency: %.1f%%\n", infrastructureEfficiency * 100);
        System.out.printf("  🚨 Emergency Readiness: %.1f%%\n", emergencyReadiness * 100);
        System.out.printf("  💰 Total Cost: $%.2f\n", result.totalCost);
        System.out.printf("  ⏱️ Execution Time: %d ms\n", duration);
        
        analyzeTaskDistributionByType(result, nodes, "Smart City");
    }
    
    // Helper calculation methods
    
    private static double calculateProductionEfficiency(EnhancedEPOCEIS.SchedulingResult result, 
                                                         Workflow workflow, List<FogNode> nodes) {
        // محاسبه بر اساس deadline compliance و resource utilization
        double deadlineCompliance = calculateDeadlineCompliance(result, workflow);
        double resourceUtilization = calculateResourceUtilization(result, nodes);
        
        return (deadlineCompliance + resourceUtilization) / 2.0;
    }
    
    private static double calculateSafetyScore(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        // فرض: تسک‌های safety باید روی edge اجرا شوند
        int safetyTasks = 0;
        int edgeExecutedSafety = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            if (task.getId() == 8) { // safety monitoring task
                safetyTasks++;
                Integer nodeId = result.taskAssignments.get(task.getId());
                if (nodeId != null && nodeId <= 7) { // edge/fog nodes
                    edgeExecutedSafety++;
                }
            }
        }
        
        return safetyTasks > 0 ? (double) edgeExecutedSafety / safetyTasks : 1.0;
    }
    
    private static double calculateEnergyEfficiency(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        // محاسبه بر اساس استفاده از nodes کم مصرف
        double totalCost = result.totalCost;
        double maxPossibleCost = nodes.stream().mapToDouble(n -> n.getCostPerSec() * 10).sum();
        
        return 1.0 - (totalCost / Math.max(maxPossibleCost, 1.0));
    }
    
    private static double calculateCriticalResponseTime(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        // محاسبه زمان پاسخ برای تسک‌های critical
        double totalResponseTime = 0.0;
        int criticalTasks = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            if (task.getDeadline() <= 1.0) { // critical tasks
                Double startTime = result.startTimes.get(task.getId());
                if (startTime != null) {
                    totalResponseTime += startTime + (task.getLength() / 1000.0);
                    criticalTasks++;
                }
            }
        }
        
        return criticalTasks > 0 ? totalResponseTime / criticalTasks : 0.0;
    }
    
    private static double calculatePatientSafety(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        return calculateDeadlineCompliance(result, workflow);
    }
    
    private static double calculateDataPrivacy(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        // فرض: fog nodes امن‌تر از cloud هستند
        int totalTasks = result.taskAssignments.size();
        int privateTasks = 0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        for (Integer nodeId : result.taskAssignments.values()) {
            FogNode node = nodeMap.get(nodeId);
            if (node != null && !node.isCloud()) {
                privateTasks++;
            }
        }
        
        return totalTasks > 0 ? (double) privateTasks / totalTasks : 1.0;
    }
    
    private static double calculateCitizenSatisfaction(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        // بر اساس کیفیت سرویس‌های شهری
        return calculateDeadlineCompliance(result, workflow);
    }
    
    private static double calculateInfrastructureEfficiency(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        return calculateResourceUtilization(result, nodes);
    }
    
    private static double calculateEmergencyReadiness(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        // تسک‌های emergency باید سریع اجرا شوند
        int emergencyTasks = 0;
        int fastExecuted = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            if (task.getDeadline() <= 3.0) { // emergency tasks
                emergencyTasks++;
                Double startTime = result.startTimes.get(task.getId());
                if (startTime != null && startTime <= task.getDeadline() * 0.5) {
                    fastExecuted++;
                }
            }
        }
        
        return emergencyTasks > 0 ? (double) fastExecuted / emergencyTasks : 1.0;
    }
    
    private static double calculateDeadlineCompliance(EnhancedEPOCEIS.SchedulingResult result, Workflow workflow) {
        int totalTasks = 0;
        int compliantTasks = 0;
        
        for (IIoTTask task : workflow.getAllTasks()) {
            totalTasks++;
            Double startTime = result.startTimes.get(task.getId());
            if (startTime != null) {
                double finishTime = startTime + (task.getLength() / 1000.0);
                if (finishTime <= task.getDeadline()) {
                    compliantTasks++;
                }
            }
        }
        
        return totalTasks > 0 ? (double) compliantTasks / totalTasks : 0.0;
    }
    
    private static double calculateResourceUtilization(EnhancedEPOCEIS.SchedulingResult result, List<FogNode> nodes) {
        Set<Integer> usedNodes = new HashSet<>(result.taskAssignments.values());
        return (double) usedNodes.size() / Math.max(nodes.size(), 1);
    }
    
    private static void analyzeTaskDistributionByType(EnhancedEPOCEIS.SchedulingResult result, 
                                                      List<FogNode> nodes, String scenarioType) {
        int edgeTasks = 0, fogTasks = 0, cloudTasks = 0;
        
        Map<Integer, FogNode> nodeMap = new HashMap<>();
        for (FogNode node : nodes) {
            nodeMap.put(node.getId(), node);
        }
        
        for (Integer nodeId : result.taskAssignments.values()) {
            FogNode node = nodeMap.get(nodeId);
            if (node != null) {
                if (node.isCloud()) {
                    cloudTasks++;
                } else if (node.getMips() > 1500) {
                    fogTasks++;
                } else {
                    edgeTasks++;
                }
            }
        }
        
        System.out.printf("  📍 %s Task Distribution: Edge=%d, Fog=%d, Cloud=%d\n", 
            scenarioType, edgeTasks, fogTasks, cloudTasks);
    }
    
    // Missing scenario implementations
    private static void runSmartAgricultureScenario() {
        System.out.println("\n🌾 SMART AGRICULTURE SCENARIO");
        System.out.println("Crop monitoring, irrigation control, yield prediction");
        System.out.println("==================================================");
        System.out.println("  🚧 Implementation placeholder - extend as needed");
    }
    
    private static void runSmartTransportationScenario() {
        System.out.println("\n🚗 SMART TRANSPORTATION SCENARIO"); 
        System.out.println("Autonomous vehicles, route optimization, fleet management");
        System.out.println("=======================================================");
        System.out.println("  🚧 Implementation placeholder - extend as needed");
    }
    
    private static void runSecuritySurveillanceScenario() {
        System.out.println("\n🔒 SECURITY SURVEILLANCE SCENARIO");
        System.out.println("Video analytics, intrusion detection, threat assessment");
        System.out.println("======================================================");
        System.out.println("  🚧 Implementation placeholder - extend as needed");
    }
}
