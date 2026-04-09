package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PersonTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Person getPersonSample1() {
        return new Person().id(1L).fullName("fullName1").cpf("cpf1").email("email1").phone("phone1");
    }

    public static Person getPersonSample2() {
        return new Person().id(2L).fullName("fullName2").cpf("cpf2").email("email2").phone("phone2");
    }

    public static Person getPersonRandomSampleGenerator() {
        return new Person()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .cpf(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
