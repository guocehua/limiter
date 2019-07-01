package com.ibs.limiter;


import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.Uninterruptibles;


import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.max;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;


public class RedisRateLimiter {
    private double permitsPerMonth;
    private String key;
    private double permitsPerDay;
    private BucketService bucketService;
    private final Stopwatch stopwatch = Stopwatch.createStarted();
    private volatile Object mutexDoNotUseDirectly;

    public RedisRateLimiter(double permitsPerMonth, String key, double permitsPerDay, BucketService bucketService) {
        if(permitsPerMonth ==0.0){
            this.permitsPerMonth =1.0;
        }
        this.permitsPerMonth = permitsPerMonth;
        this.key = key;
        this.permitsPerDay = permitsPerDay;
        this.bucketService = bucketService;
    }
    RedisRateLimiter create(){
        setBucket();
        return this;
    }

    public void setPermitsPerMonth(double permitsPerMonth) {
        this.permitsPerMonth = permitsPerMonth;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPermitsPerDay(double permitsPerDay) {
        this.permitsPerDay = permitsPerDay;
    }

    public void setBucketService(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    void setBucket() {
        checkArgument(
                permitsPerDay > 0.0 && !Double.isNaN(permitsPerDay), "rate must be positive");
        synchronized (mutex()) {
            Bucket bucket = build(permitsPerDay, permitsPerMonth, stopwatch.elapsed(MICROSECONDS));
            bucketService.setBucket(key, bucket);
        }
    }

    Bucket build(double permitsPerSecond, double permitsPerMonth, long nowMicros) {
        Bucket bucket = new Bucket();
        bucket.resync(nowMicros);
        double stableIntervalMicros = SECONDS.toMicros(1L) / permitsPerSecond;
        bucket.setStableIntervalMicros(stableIntervalMicros);
        double oldMaxPermits = bucket.getMaxPermits();
        bucket.setMaxPermits(permitsPerMonth);
        if (oldMaxPermits == Double.POSITIVE_INFINITY) {
            // if we don't special-case this, we would get storedPermits == NaN, below
            bucket.setStoredPermits(bucket.getMaxPermits());
        } else {
            bucket.setRate(oldMaxPermits);
        }
        return bucket;
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public boolean tryAcquire(int permits) {
        return tryAcquire(permits, 0, MICROSECONDS);
    }

    public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
        long timeoutMicros = max(unit.toMicros(timeout), 0);
        checkPermits(permits);
        long microsToWait;
        synchronized (mutex()) {
            long nowMicros = stopwatch.elapsed(MICROSECONDS);
            if (!canAcquire(nowMicros, timeoutMicros)) {
                return false;
            } else {
                microsToWait = reserveAndGetWaitLength(permits, nowMicros);
            }
        }
        sleepMicrosUninterruptibly(microsToWait);
        return true;
    }

    private boolean canAcquire(long nowMicros, long timeoutMicros) {
        return getBucket().getNextFreeTicketMicros() - timeoutMicros <= nowMicros;
    }

    public double acquire() {
        return acquire(1);
    }

    public double acquire(int permits) {
        long microsToWait = reserve(permits);
        sleepMicrosUninterruptibly(microsToWait);
        return 1.0 * microsToWait / SECONDS.toMicros(1L);
    }

    Bucket getBucket() {
        return bucketService.getBucket(key);
    }

    final long reserve(int permits) {
        checkPermits(permits);
        synchronized (mutex()) {
            return reserveAndGetWaitLength(permits, stopwatch.elapsed(MICROSECONDS));
        }
    }

    final long reserveAndGetWaitLength(int permits, long nowMicros) {
        Bucket bucket=getBucket();
        long momentAvailable = bucket.reserveEarliestAvailable(permits, nowMicros);
        bucketService.setBucket(key,bucket);
        return max(momentAvailable - nowMicros, 0);
    }

    private void sleepMicrosUninterruptibly(long micros) {
        if (micros > 0) {
            Uninterruptibles.sleepUninterruptibly(micros, MICROSECONDS);
        }
    }

    private Object mutex() {
        Object mutex = mutexDoNotUseDirectly;
        if (mutex == null) {
            synchronized (this) {
                mutex = mutexDoNotUseDirectly;
                if (mutex == null) {
                    mutexDoNotUseDirectly = mutex = new Object();
                }
            }
        }
        return mutex;
    }

    private static void checkPermits(int permits) {
        checkArgument(permits > 0, "Requested permits (%s) must be positive", permits);
    }
}
