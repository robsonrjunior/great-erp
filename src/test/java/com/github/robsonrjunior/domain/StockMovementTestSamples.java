package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static StockMovement getStockMovementSample1() {
        return new StockMovement().id(1L).referenceNumber("referenceNumber1").notes("notes1");
    }

    public static StockMovement getStockMovementSample2() {
        return new StockMovement().id(2L).referenceNumber("referenceNumber2").notes("notes2");
    }

    public static StockMovement getStockMovementRandomSampleGenerator() {
        return new StockMovement()
            .id(longCount.incrementAndGet())
            .referenceNumber(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
