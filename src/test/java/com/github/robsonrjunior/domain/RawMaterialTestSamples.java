package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RawMaterialTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static RawMaterial getRawMaterialSample1() {
        return new RawMaterial().id(1L).name("name1").sku("sku1").unitDecimalPlaces(1);
    }

    public static RawMaterial getRawMaterialSample2() {
        return new RawMaterial().id(2L).name("name2").sku("sku2").unitDecimalPlaces(2);
    }

    public static RawMaterial getRawMaterialRandomSampleGenerator() {
        return new RawMaterial()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .sku(UUID.randomUUID().toString())
            .unitDecimalPlaces(intCount.incrementAndGet());
    }
}
