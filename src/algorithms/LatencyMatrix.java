package utils;

import core.FogNode;
import java.util.List;

public class LatencyMatrix {
    public static double estimateAverageLatency(boolean[] placement, List<FogNode> sites) {
        double totalLatency = 0;
        int count = 0;

        for (int i = 0; i < placement.length; i++) {
            if (placement[i]) {
                totalLatency += sites.get(i).getLatencyMs();
                count++;
            }
        }

        return (count > 0) ? totalLatency / count : Double.MAX_VALUE;
    }
}
