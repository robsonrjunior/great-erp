package com.github.robsonrjunior;

import com.github.robsonrjunior.config.AsyncSyncConfiguration;
import com.github.robsonrjunior.config.DatabaseTestcontainer;
import com.github.robsonrjunior.config.ElasticsearchTestConfiguration;
import com.github.robsonrjunior.config.ElasticsearchTestContainer;
import com.github.robsonrjunior.config.JacksonConfiguration;
import com.github.robsonrjunior.config.RedisTestContainer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        GreatErpApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.github.robsonrjunior.config.JacksonHibernateConfiguration.class,
        ElasticsearchTestConfiguration.class,
    }
)
@ImportTestcontainers({ { DatabaseTestcontainer.class, ElasticsearchTestContainer.class }, RedisTestContainer.class })
public @interface IntegrationTest {}
