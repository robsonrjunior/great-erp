package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CompanyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Company getCompanySample1() {
        return new Company()
            .id(1L)
            .legalName("legalName1")
            .tradeName("tradeName1")
            .cnpj("cnpj1")
            .stateRegistration("stateRegistration1")
            .email("email1")
            .phone("phone1");
    }

    public static Company getCompanySample2() {
        return new Company()
            .id(2L)
            .legalName("legalName2")
            .tradeName("tradeName2")
            .cnpj("cnpj2")
            .stateRegistration("stateRegistration2")
            .email("email2")
            .phone("phone2");
    }

    public static Company getCompanyRandomSampleGenerator() {
        return new Company()
            .id(longCount.incrementAndGet())
            .legalName(UUID.randomUUID().toString())
            .tradeName(UUID.randomUUID().toString())
            .cnpj(UUID.randomUUID().toString())
            .stateRegistration(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
