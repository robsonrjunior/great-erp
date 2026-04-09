package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SaleItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static SaleItem getSaleItemSample1() {
        return new SaleItem().id(1L);
    }

    public static SaleItem getSaleItemSample2() {
        return new SaleItem().id(2L);
    }

    public static SaleItem getSaleItemRandomSampleGenerator() {
        return new SaleItem().id(longCount.incrementAndGet());
    }
}
