package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static State getStateSample1() {
        return new State().id(1L).name("name1").code("code1");
    }

    public static State getStateSample2() {
        return new State().id(2L).name("name2").code("code2");
    }

    public static State getStateRandomSampleGenerator() {
        return new State().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).code(UUID.randomUUID().toString());
    }
}
