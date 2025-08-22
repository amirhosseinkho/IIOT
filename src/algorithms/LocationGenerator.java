package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationGenerator {

    public static List<Location> generateCandidateSites(int count) {
        List<Location> sites = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            double x = rand.nextDouble() * 100;  // محدوده مختصات فرضی
            double y = rand.nextDouble() * 100;
            double latencyMs = rand.nextDouble() * 10 + 1;  // تاخیر بین 1 تا 10 میلی‌ثانیه

            Location loc = new Location(i, x, y, latencyMs);
            sites.add(loc);
        }

        return sites;
    }
}
