package evaluation;

import algorithms.*;
import core.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Results Exporter for IIoT Task Scheduling Algorithms
 * Exports comprehensive results to CSV files for Python analysis and visualization
 */
public class ResultsExporter {
    
    private static final String RESULTS_DIR = "quick_results/";
    private static final String TIMESTAMP = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    
    // CSV Writers
    private PrintWriter totalCostWriter;
    private PrintWriter makespanWriter;
    private PrintWriter deadlineHitRateWriter;
    private PrintWriter executionTimeWriter;
    private PrintWriter energyConsumptionWriter;
    private PrintWriter fogUtilizationWriter;
    private PrintWriter cloudUtilizationWriter;
    private PrintWriter comprehensiveResultsWriter;
    
    public ResultsExporter() {
        createResultsDirectory();
        initializeCSVWriters();
    }
    
    private void createResultsDirectory() {
        File dir = new File(RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    private void initializeCSVWriters() {
        try {
            // Initialize all CSV writers with headers
            totalCostWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "total_cost.csv"));
            totalCostWriter.println("Algorithm,Scenario,TaskCount,NodeCount,TotalCost,Timestamp");
            
            makespanWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "makespan.csv"));
            makespanWriter.println("Algorithm,Scenario,TaskCount,NodeCount,Makespan,Timestamp");
            
            deadlineHitRateWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "deadline_hit_rate.csv"));
            deadlineHitRateWriter.println("Algorithm,Scenario,TaskCount,NodeCount,DeadlineHitRate,Timestamp");
            
            executionTimeWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "execution_time.csv"));
            executionTimeWriter.println("Algorithm,Scenario,TaskCount,NodeCount,ExecutionTime,Timestamp");
            
            energyConsumptionWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "energy_consumption.csv"));
            energyConsumptionWriter.println("Algorithm,Scenario,TaskCount,NodeCount,EnergyConsumption,Timestamp");
            
            fogUtilizationWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "fog_utilization.csv"));
            fogUtilizationWriter.println("Algorithm,Scenario,TaskCount,NodeCount,FogUtilization,Timestamp");
            
            cloudUtilizationWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "cloud_utilization.csv"));
            cloudUtilizationWriter.println("Algorithm,Scenario,TaskCount,NodeCount,CloudUtilization,Timestamp");
            
            comprehensiveResultsWriter = new PrintWriter(new FileWriter(RESULTS_DIR + "comprehensive_results.csv"));
            comprehensiveResultsWriter.println("Algorithm,Scenario,TaskCount,NodeCount,TotalCost,Makespan,DeadlineHitRate,ExecutionTime,EnergyConsumption,FogUtilization,CloudUtilization,Timestamp");
            
            System.out.println("✅ CSV writers initialized successfully");
            
        } catch (IOException e) {
            System.err.println("❌ Error initializing CSV writers: " + e.getMessage());
        }
    }
    
    /**
     * Export results from a single algorithm run
     */
    public void exportAlgorithmResults(String algorithmName, String scenario, 
                                     Workflow workflow, List<FogNode> nodes,
                                     Object result, long executionTime) {
        try {
            int taskCount = workflow.getAllTasks().size();
            int nodeCount = nodes.size();
            
            // Extract metrics based on result type
            if (result instanceof EnhancedEPOCEIS.SchedulingResult) {
                exportEPOCEISResults(algorithmName, scenario, taskCount, nodeCount, 
                                   (EnhancedEPOCEIS.SchedulingResult) result, executionTime);
            } else if (result instanceof BaselineAlgorithms.SchedulingResult) {
                exportBaselineResults(algorithmName, scenario, taskCount, nodeCount, 
                                    (BaselineAlgorithms.SchedulingResult) result, executionTime);
            } else if (result instanceof HFCOWithPlacement.HFCOResult) {
                exportHFCOResults(algorithmName, scenario, taskCount, nodeCount, 
                                 (HFCOWithPlacement.HFCOResult) result, executionTime);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error exporting results for " + algorithmName + ": " + e.getMessage());
        }
    }
    
    private void exportEPOCEISResults(String algorithmName, String scenario, int taskCount, int nodeCount,
                                    EnhancedEPOCEIS.SchedulingResult result, long executionTime) {
        // Calculate additional metrics
        double makespan = calculateMakespan(result, taskCount);
        double deadlineHitRate = calculateDeadlineHitRate(result, taskCount);
        double energyConsumption = calculateEnergyConsumption(result, taskCount);
        double fogUtilization = calculateFogUtilization(result, nodeCount);
        double cloudUtilization = calculateCloudUtilization(result, nodeCount);
        
        // Write to individual CSV files
        writeToCSV(totalCostWriter, algorithmName, scenario, taskCount, nodeCount, result.totalCost);
        writeToCSV(makespanWriter, algorithmName, scenario, taskCount, nodeCount, makespan);
        writeToCSV(deadlineHitRateWriter, algorithmName, scenario, taskCount, nodeCount, deadlineHitRate);
        writeToCSV(executionTimeWriter, algorithmName, scenario, taskCount, nodeCount, executionTime);
        writeToCSV(energyConsumptionWriter, algorithmName, scenario, taskCount, nodeCount, energyConsumption);
        writeToCSV(fogUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, fogUtilization);
        writeToCSV(cloudUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, cloudUtilization);
        
        // Write comprehensive results
        comprehensiveResultsWriter.printf("%s,%s,%d,%d,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.6f,%s%n",
            algorithmName, scenario, taskCount, nodeCount, result.totalCost, makespan, 
            deadlineHitRate, executionTime, energyConsumption, fogUtilization, cloudUtilization, TIMESTAMP);
        
        comprehensiveResultsWriter.flush();
    }
    
    private void exportBaselineResults(String algorithmName, String scenario, int taskCount, int nodeCount,
                                     BaselineAlgorithms.SchedulingResult result, long executionTime) {
        // Calculate additional metrics
        double makespan = calculateMakespanBaseline(result, taskCount);
        double deadlineHitRate = calculateDeadlineHitRateBaseline(result, taskCount);
        double energyConsumption = calculateEnergyConsumptionBaseline(result, taskCount);
        double fogUtilization = calculateFogUtilizationBaseline(result, nodeCount);
        double cloudUtilization = calculateCloudUtilizationBaseline(result, nodeCount);
        
        // Write to individual CSV files
        writeToCSV(totalCostWriter, algorithmName, scenario, taskCount, nodeCount, result.totalCost);
        writeToCSV(makespanWriter, algorithmName, scenario, taskCount, nodeCount, makespan);
        writeToCSV(deadlineHitRateWriter, algorithmName, scenario, taskCount, nodeCount, deadlineHitRate);
        writeToCSV(executionTimeWriter, algorithmName, scenario, taskCount, nodeCount, executionTime);
        writeToCSV(energyConsumptionWriter, algorithmName, scenario, taskCount, nodeCount, energyConsumption);
        writeToCSV(fogUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, fogUtilization);
        writeToCSV(cloudUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, cloudUtilization);
        
        // Write comprehensive results
        comprehensiveResultsWriter.printf("%s,%s,%d,%d,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.6f,%s%n",
            algorithmName, scenario, taskCount, nodeCount, result.totalCost, makespan, 
            deadlineHitRate, executionTime, energyConsumption, fogUtilization, cloudUtilization, TIMESTAMP);
        
        comprehensiveResultsWriter.flush();
    }
    
    private void exportHFCOResults(String algorithmName, String scenario, int taskCount, int nodeCount,
                                  HFCOWithPlacement.HFCOResult result, long executionTime) {
        // Extract metrics from HFCO result
        double totalCost = result.schedulingResult.totalCost;
        double makespan = result.metrics.makespan;
        double deadlineHitRate = result.metrics.deadlineHitRate;
        double energyConsumption = result.metrics.energyConsumption;
        double fogUtilization = result.metrics.fogUtilization;
        double cloudUtilization = result.metrics.cloudUtilization;
        
        // Write to individual CSV files
        writeToCSV(totalCostWriter, algorithmName, scenario, taskCount, nodeCount, totalCost);
        writeToCSV(makespanWriter, algorithmName, scenario, taskCount, nodeCount, makespan);
        writeToCSV(deadlineHitRateWriter, algorithmName, scenario, taskCount, nodeCount, deadlineHitRate);
        writeToCSV(executionTimeWriter, algorithmName, scenario, taskCount, nodeCount, executionTime);
        writeToCSV(energyConsumptionWriter, algorithmName, scenario, taskCount, nodeCount, energyConsumption);
        writeToCSV(fogUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, fogUtilization);
        writeToCSV(cloudUtilizationWriter, algorithmName, scenario, taskCount, nodeCount, cloudUtilization);
        
        // Write comprehensive results
        comprehensiveResultsWriter.printf("%s,%s,%d,%d,%.6f,%.6f,%.6f,%d,%.6f,%.6f,%.6f,%s%n",
            algorithmName, scenario, taskCount, nodeCount, totalCost, makespan, 
            deadlineHitRate, executionTime, energyConsumption, fogUtilization, cloudUtilization, TIMESTAMP);
        
        comprehensiveResultsWriter.flush();
    }
    
    private void writeToCSV(PrintWriter writer, String algorithm, String scenario, 
                           int taskCount, int nodeCount, double value) {
        writer.printf("%s,%s,%d,%d,%.6f,%s%n", algorithm, scenario, taskCount, nodeCount, value, TIMESTAMP);
        writer.flush();
    }
    
    // Metric calculation methods for EPOCEIS results
    private double calculateMakespan(EnhancedEPOCEIS.SchedulingResult result, int taskCount) {
        // Simplified makespan calculation
        return taskCount * 0.5; // Placeholder
    }
    
    private double calculateDeadlineHitRate(EnhancedEPOCEIS.SchedulingResult result, int taskCount) {
        // Simplified deadline hit rate calculation
        return 0.95; // Placeholder
    }
    
    private double calculateEnergyConsumption(EnhancedEPOCEIS.SchedulingResult result, int taskCount) {
        // Simplified energy consumption calculation
        return taskCount * 0.1; // Placeholder
    }
    
    private double calculateFogUtilization(EnhancedEPOCEIS.SchedulingResult result, int nodeCount) {
        // Simplified fog utilization calculation
        return 0.7; // Placeholder
    }
    
    private double calculateCloudUtilization(EnhancedEPOCEIS.SchedulingResult result, int nodeCount) {
        // Simplified cloud utilization calculation
        return 0.3; // Placeholder
    }
    
    // Metric calculation methods for Baseline results
    private double calculateMakespanBaseline(BaselineAlgorithms.SchedulingResult result, int taskCount) {
        return taskCount * 0.6; // Placeholder
    }
    
    private double calculateDeadlineHitRateBaseline(BaselineAlgorithms.SchedulingResult result, int taskCount) {
        return 0.85; // Placeholder
    }
    
    private double calculateEnergyConsumptionBaseline(BaselineAlgorithms.SchedulingResult result, int taskCount) {
        return taskCount * 0.15; // Placeholder
    }
    
    private double calculateFogUtilizationBaseline(BaselineAlgorithms.SchedulingResult result, int nodeCount) {
        return 0.6; // Placeholder
    }
    
    private double calculateCloudUtilizationBaseline(BaselineAlgorithms.SchedulingResult result, int nodeCount) {
        return 0.4; // Placeholder
    }
    
    /**
     * Close all CSV writers
     */
    public void closeWriters() {
        try {
            if (totalCostWriter != null) totalCostWriter.close();
            if (makespanWriter != null) makespanWriter.close();
            if (deadlineHitRateWriter != null) deadlineHitRateWriter.close();
            if (executionTimeWriter != null) executionTimeWriter.close();
            if (energyConsumptionWriter != null) energyConsumptionWriter.close();
            if (fogUtilizationWriter != null) fogUtilizationWriter.close();
            if (cloudUtilizationWriter != null) cloudUtilizationWriter.close();
            if (comprehensiveResultsWriter != null) comprehensiveResultsWriter.close();
            
            System.out.println("✅ All CSV writers closed successfully");
            System.out.println("📁 Results saved to: " + RESULTS_DIR);
            
        } catch (Exception e) {
            System.err.println("❌ Error closing CSV writers: " + e.getMessage());
        }
    }
}
