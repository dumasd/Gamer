package com.thinkerwolf.gamer.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Stopwatch {

    private long startNanos;

    private long endNanos;

    private long spendNanos;

    public void start() {
        this.startNanos = System.nanoTime();
    }

    public void stop() {
        this.endNanos = System.nanoTime();
        this.spendNanos = endNanos - startNanos;
    }

    public long getNanos() {
        return spendNanos;
    }

    public double getMillis() {
        return scale(getNanos() / 1000000d, 5);
    }

    public double getSeconds() {
        return scale((double) getNanos() / 1000000000d, 3);
    }

    private static double scale(double d, int scale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
