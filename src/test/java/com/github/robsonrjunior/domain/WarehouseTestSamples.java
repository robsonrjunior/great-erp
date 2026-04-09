package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WarehouseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Warehouse getWarehouseSample1() {
        return new Warehouse().id(1L).name("name1").code("code1");
    }

    public static Warehouse getWarehouseSample2() {
        return new Warehouse().id(2L).name("name2").code("code2");
    }

    public static Warehouse getWarehouseRandomSampleGenerator() {
        return new Warehouse().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
