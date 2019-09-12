package net.corevalue.multitenant.poc.config;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import lombok.extern.slf4j.Slf4j;
import net.corevalue.multitenant.poc.config.MultiTenantProperties.Tenant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Configuration
@EnableConfigurationProperties(MultiTenantProperties.class)
public class OrientDBConfiguration {

    @Bean
    OrientDB orientDB(MultiTenantProperties props) {
        return new OrientDB(String.format("remote:%s", props.getOrientDb().getHost()), OrientDBConfig.defaultConfig());
    }

    @Bean
    OrientDBConfig orientPoolConfig(MultiTenantProperties props) {
        return OrientDBConfig.builder()
                .addConfig(OGlobalConfiguration.DB_POOL_MIN, props.getOrientDb().getPool().getMin())
                .addConfig(OGlobalConfiguration.DB_POOL_MAX, props.getOrientDb().getPool().getMax())
                .build();
    }

    @Bean
    Map<String, Tenant> mapTenants(MultiTenantProperties props) {
        return props.getTenants().stream()
                .collect(Collectors.toMap(Tenant::getName, Function.identity()));
    }

    @Bean
    Map<String, ODatabasePool> orientPools(OrientDB orientDB,
                                           OrientDBConfig poolConfig,
                                           MultiTenantProperties props) {
        return props.getTenants().stream()
                .collect(Collectors.toMap(Tenant::getName, t -> new ODatabasePool(orientDB,
                        props.getOrientDb().getDb(), t.getName(), t.getPwd(), poolConfig)));
    }

}
