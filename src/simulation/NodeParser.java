package simulation;

import core.FogNode;

import java.io.*;
import java.util.*;

public class NodeParser {

    public static List<FogNode> parseFromFile(String filePath) throws IOException {
        List<FogNode> nodes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#") || line.isEmpty()) continue;

            String[] parts = line.split(",");

            if (parts[0].equalsIgnoreCase("NODE")) {
                int id = Integer.parseInt(parts[1]);
                int mips = Integer.parseInt(parts[2]);
                int ram = Integer.parseInt(parts[3]);
                long bw = Long.parseLong(parts[4]);
                long storage = Long.parseLong(parts[5]);
                boolean isCloud = Boolean.parseBoolean(parts[6]);
                double costPerSec = Double.parseDouble(parts[7]);

                double latencyMs = parts.length > 8 ? Double.parseDouble(parts[8]) : 0.0;
                double x = parts.length > 9 ? Double.parseDouble(parts[9]) : 0.0;
                double y = parts.length > 10 ? Double.parseDouble(parts[10]) : 0.0;
                double energyPerSec = parts.length > 11 ? Double.parseDouble(parts[11]) : 0.0;

                FogNode node = new FogNode(id, mips, ram, bw, storage, isCloud, costPerSec);
                node.setLatencyMs(latencyMs);
                node.setPosition(x, y);
                node.setEnergyPerSec(energyPerSec);

                nodes.add(node);
            }
        }

        reader.close();
        return nodes;
    }
}
