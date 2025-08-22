package algorithms;

public class Location {
    private int id;
    private double x, y;
    private double latencyMs;

    public Location(int id, double x, double y, double latencyMs) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.latencyMs = latencyMs;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLatencyMs() {
        return latencyMs;
    }
}
