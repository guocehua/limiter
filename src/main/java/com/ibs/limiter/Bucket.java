package com.ibs.limiter;


import com.google.common.math.LongMath;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;



public class Bucket implements Serializable {
    private double storedPermits;
    private double maxPermits;
    private double stableIntervalMicros;
    private long nextFreeTicketMicros =0L;
    private double dayPermits;
    private double monthPermits;
    private int day;
    private int month;

//    public long expires() {
//        long now = System.currentTimeMillis();
//        return 2 * TimeUnit.MINUTES.toSeconds(1)
//                + TimeUnit.MILLISECONDS.toSeconds(Math.max(nextFreeTicketMicros, now) - now);
//    }
    final long reserveEarliestAvailable(int requiredPermits, long nowMicros) {
        resync(nowMicros);
        long returnValue = nextFreeTicketMicros;
        double storedPermitsToSpend = min(requiredPermits, this.storedPermits);
        double freshPermits = requiredPermits - storedPermitsToSpend;
        long waitMicros = (long) (freshPermits * stableIntervalMicros);
        this.nextFreeTicketMicros = LongMath.saturatedAdd(nextFreeTicketMicros, waitMicros);
        this.storedPermits -= storedPermitsToSpend;
        return returnValue;
    }
    void resync(long nowMicros) {
        // if nextFreeTicket is in the past, resync to now
        if (nowMicros > nextFreeTicketMicros) {
            double newPermits = (nowMicros - nextFreeTicketMicros) / stableIntervalMicros;
            storedPermits = min(maxPermits, storedPermits + newPermits);
            nextFreeTicketMicros = nowMicros;
        }
    }
    public double  setRate(double oldMaxPermits){
        return storedPermits =
                (oldMaxPermits == 0.0)
                        ? 0.0 // initial state
                        : storedPermits * maxPermits / oldMaxPermits;
    }
    public double getStoredPermits() {
        return storedPermits;
    }

    public void setStoredPermits(double storedPermits) {
        this.storedPermits = storedPermits;
    }

    public double getMaxPermits() {
        return maxPermits;
    }

    public void setMaxPermits(double maxPermits) {
        this.maxPermits = maxPermits;
    }

    public double getStableIntervalMicros() {
        return stableIntervalMicros;
    }

    public void setStableIntervalMicros(double stableIntervalMicros) {
        this.stableIntervalMicros = stableIntervalMicros;
    }

    public long getNextFreeTicketMicros() {
        return nextFreeTicketMicros;
    }

    public void setNextFreeTicketMicros(long nextFreeTicketMicros) {
        this.nextFreeTicketMicros = nextFreeTicketMicros;
    }
}
