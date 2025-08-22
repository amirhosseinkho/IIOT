package utils;

import core.IIoTTask;
import core.FogNode;

public class DelayCalculator {

    /**
     * محاسبه تأخیر انتقال داده بین وظیفه و نود
     * شامل زمان تأخیر شبکه (latency) و زمان انتقال بر اساس پهنای باند
     *
     * @param task وظیفه
     * @param node نود پردازشی (Fog یا Cloud)
     * @return تأخیر کل بر حسب ثانیه
     */
    public static double calculateTransferDelay(IIoTTask task, FogNode node) {
        double fileSizeMB = task.getFileSize(); // فرض: واحد MB
        double bwMBps = node.getBw();           // فرض: واحد MB/s

        double bandwidthDelay = (bwMBps > 0) ? (fileSizeMB / bwMBps) : Double.MAX_VALUE;
        double latencyDelay = node.getLatencyMs() / 1000.0;

        return bandwidthDelay + latencyDelay;
    }
}
