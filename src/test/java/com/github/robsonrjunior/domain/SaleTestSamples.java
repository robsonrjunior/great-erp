package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SaleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Sale getSaleSample1() {
        return new Sale().id(1L).saleNumber("saleNumber1").notes("notes1");
    }

    public static Sale getSaleSample2() {
        return new Sale().id(2L).saleNumber("saleNumber2").notes("notes2");
    }

    public static Sale getSaleRandomSampleGenerator() {
        return new Sale().id(longCount.incrementAndGet()).saleNumber(UUID.randomUUID().toString()).notes(UUID.randomUUID().toString());
    }
}
