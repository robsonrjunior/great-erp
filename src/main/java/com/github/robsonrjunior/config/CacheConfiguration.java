package com.github.robsonrjunior.config;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(jHipsterProperties.getCache().getRedis().getServer()[0]);

        Config config = new Config();
        // Fix Hibernate lazy initialization https://github.com/jhipster/generator-jhipster/issues/22889
        config.setCodec(new org.redisson.codec.SerializationCodec());
        if (jHipsterProperties.getCache().getRedis().isCluster()) {
            ClusterServersConfig clusterServersConfig = config
                .useClusterServers()
                .setMasterConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setMasterConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .addNodeAddress(jHipsterProperties.getCache().getRedis().getServer());

            if (redisUri.getUserInfo() != null) {
                clusterServersConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        } else {
            SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(jHipsterProperties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(jHipsterProperties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(jHipsterProperties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(jHipsterProperties.getCache().getRedis().getServer()[0]);

            if (redisUri.getUserInfo() != null) {
                singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
            }
        }
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
            CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.github.robsonrjunior.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Tenant.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Country.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Country.class.getName() + ".stateses", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.State.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.State.class.getName() + ".citieses", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.City.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Supplier.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Supplier.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Supplier.class.getName() + ".cities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Customer.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Customer.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Customer.class.getName() + ".cities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Person.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Person.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Person.class.getName() + ".cities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Company.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Company.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Company.class.getName() + ".cities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Product.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Product.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.RawMaterial.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.RawMaterial.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.RawMaterial.class.getName() + ".primarySuppliers", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Warehouse.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Warehouse.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Warehouse.class.getName() + ".cities", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.StockMovement.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.StockMovement.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.StockMovement.class.getName() + ".warehouses", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.StockMovement.class.getName() + ".products", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.StockMovement.class.getName() + ".rawMaterials", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Sale.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Sale.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Sale.class.getName() + ".warehouses", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.Sale.class.getName() + ".customers", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.SaleItem.class.getName(), jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.SaleItem.class.getName() + ".tenants", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.SaleItem.class.getName() + ".sales", jcacheConfiguration);
            createCache(cm, com.github.robsonrjunior.domain.SaleItem.class.getName() + ".products", jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(
        javax.cache.CacheManager cm,
        String cacheName,
        javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
