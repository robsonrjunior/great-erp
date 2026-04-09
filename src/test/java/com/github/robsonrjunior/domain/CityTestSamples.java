package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static City getCitySample1() {
        return new City().id(1L).name("name1");
    }

    public static City getCitySample2() {
        return new City().id(2L).name("name2");
    }

    public static City getCityRandomSampleGenerator() {
        return new City().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
