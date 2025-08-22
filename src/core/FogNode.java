package core;

public class FogNode {
    private int id;
    private int mips;
    private int ram;
    private long bw;
    private long storage;
    private boolean isCloud; // true = Cloud, false = Fog
    private double costPerSec;
    private double latencyMs;         // برای محاسبه delay دقیق
    private double x, y;              // برای HFCO جایابی گره‌ها
    private double energyPerSec;      // اختیاری برای حالت green-aware

    // اصلی برای سازگاری با فایل nodes.txt
    public FogNode(int id, int mips, int ram, long bw, long storage, boolean isCloud, double costPerSec) {
        this.id = id;
        this.mips = mips;
        this.ram = ram;
        this.bw = bw;
        this.storage = storage;
        this.isCloud = isCloud;
        this.costPerSec = costPerSec;
        this.latencyMs = 0.0;
        this.x = 0.0;
        this.y = 0.0;
        this.energyPerSec = 0.0;
    }

    // اضافه برای تست‌های latency-aware
    public FogNode(int id, int mips, int ram, long bw, long storage, boolean isCloud, double costPerSec, double latencyMs) {
        this(id, mips, ram, bw, storage, isCloud, costPerSec);
        this.latencyMs = latencyMs;
    }

    // Constructor ساده‌تر برای راحتی ساخت در HFCO یا تست
    public FogNode(int id, int mips, double costPerSec, int bw, int storage) {
        this(id, mips, 1024, bw, storage, false, costPerSec);  // فرض ram=1024MB, isCloud=false
    }




    // Getters
    public int getId() {
        return id;
    }

    public int getMips() {
        return mips;
    }

    public int getRam() {
        return ram;
    }

    public long getBw() {
        return bw;
    }

    public long getStorage() {
        return storage;
    }

    public boolean isCloud() {
        return isCloud;
    }

    public double getCostPerSec() {
        return costPerSec;
    }

    public double getLatencyMs() {
        return latencyMs;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getEnergyPerSec() {
        return energyPerSec;
    }

    // Setters
    public void setLatencyMs(double latencyMs) {
        this.latencyMs = latencyMs;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setEnergyPerSec(double energyPerSec) {
        this.energyPerSec = energyPerSec;
    }



    private double power; // in watts

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "FogNode{" +
                "id=" + id +
                ", mips=" + mips +
                ", ram=" + ram +
                ", bw=" + bw +
                ", storage=" + storage +
                ", isCloud=" + isCloud +
                ", costPerSec=" + costPerSec +
                ", latencyMs=" + latencyMs +
                ", x=" + x +
                ", y=" + y +
                ", energyPerSec=" + energyPerSec +
                '}';
    }
}
