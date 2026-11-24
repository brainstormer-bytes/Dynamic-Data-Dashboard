package com.axlero.dashboard.Criteria_API.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {

    public record Decision(boolean allowed, long retryAfterSeconds) {
        static Decision permit() { return new Decision(true, 0); }
    }

    private record Bucket(double tokens, long lastNanos, long periodNanos) {}

    private static final int PURGE_MASK = 1023;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final AtomicInteger calls = new AtomicInteger();
    private final boolean enabled;

    public RateLimiter(@Value("${app.rate-limit.enabled}") boolean enabled) {
        this.enabled = enabled;
    }

    public Decision tryConsume(String key, int capacity, Duration refillPeriod) {
        return evaluate(key, capacity, refillPeriod, true);
    }

    public Decision peek(String key, int capacity, Duration refillPeriod) {
        return evaluate(key, capacity, refillPeriod, false);
    }

    public void reset(String key) { buckets.remove(key); }

    private Decision evaluate(String key, int capacity, Duration refillPeriod, boolean consume) {
        if (!enabled) return Decision.permit();
        purgeSometimes();

        Decision[] result = new Decision[1];
        buckets.compute(key, (k, old) -> {
            if (old == null && !consume) { result[0] = Decision.permit(); return null; }
            long now = System.nanoTime();
            long periodNanos = refillPeriod.toNanos();
            double ratePerNano = capacity / (double) periodNanos;
            double tokens = old == null
                    ? capacity
                    : Math.min(capacity, old.tokens() + (now - old.lastNanos()) * ratePerNano);

            if (tokens >= 1.0) {
                result[0] = Decision.permit();
                return new Bucket(consume ? tokens - 1.0 : tokens, now, periodNanos);
            }
            long waitSeconds = (long) Math.ceil((1.0 - tokens) / ratePerNano / 1_000_000_000d);
            result[0] = new Decision(false, Math.max(1, waitSeconds));
            return new Bucket(tokens, now, periodNanos);
        });
        return result[0];
    }

    private void purgeSometimes() {
        if ((calls.incrementAndGet() & PURGE_MASK) != 0) return;
        long now = System.nanoTime();
        buckets.entrySet().removeIf(e -> now - e.getValue().lastNanos() >= e.getValue().periodNanos());
    }
}