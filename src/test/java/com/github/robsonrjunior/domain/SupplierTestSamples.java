package com.github.robsonrjunior.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SupplierTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static Supplier getSupplierSample1() {
        return new Supplier().id(1L).legalName("legalName1").tradeName("tradeName1").taxId("taxId1").email("email1").phone("phone1");
    }

    public static Supplier getSupplierSample2() {
        return new Supplier().id(2L).legalName("legalName2").tradeName("tradeName2").taxId("taxId2").email("email2").phone("phone2");
    }

    public static Supplier getSupplierRandomSampleGenerator() {
        return new Supplier()
            .id(longCount.incrementAndGet())
            .legalName(UUID.randomUUID().toString())
            .tradeName(UUID.randomUUID().toString())
            .taxId(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
