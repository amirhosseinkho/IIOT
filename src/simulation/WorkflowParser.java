package simulation;

import core.IIoTTask;
import core.Workflow;

import java.io.*;
import java.util.*;

public class WorkflowParser {

    public static Workflow parseFromFile(String filePath) throws IOException {
        Workflow workflow = new Workflow();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("#") || line.isEmpty()) continue;

            String[] parts = line.split(",");

            if (parts[0].equalsIgnoreCase("TASK")) {
                int id = Integer.parseInt(parts[1]);
                int length = Integer.parseInt(parts[2]);
                long fileSize = Long.parseLong(parts[3]);
                long outputSize = Long.parseLong(parts[4]);
                int pes = Integer.parseInt(parts[5]);
                double cost = Double.parseDouble(parts[6]);
                double deadline;
                if (parts.length > 7) {
                    deadline = Double.parseDouble(parts[7]);
                } else {
                    // یک تخمین ساده با slack 30 درصد بیشتر از طول کار
                    deadline = length / 1000.0 + 3.0;  // فرض اینکه mips حدوداً 1000
                }

                IIoTTask task = new IIoTTask(id, length, fileSize, outputSize, pes, cost);
                task.setDeadline(deadline);
                workflow.addTask(task);
            } else if (parts[0].equalsIgnoreCase("DEP")) {
                int from = Integer.parseInt(parts[1]);
                int to = Integer.parseInt(parts[2]);

                workflow.addDependency(from, to);
            }
        }

        reader.close();
        return workflow;
    }
}
