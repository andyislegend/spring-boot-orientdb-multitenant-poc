package net.corevalue.multitenant.poc.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "multi-tenant.poc")
class MultiTenantProperties {

    private List<Tenant> tenants = new ArrayList<>();
    private OrientDb orientDb;

    @Getter @Setter
    static class Tenant {
        private String name;
        private String pwd;
    }

    @Getter @Setter
    static class OrientDb {
        private String host;
        private String db;
        private OrientDbPool pool;

        @Getter @Setter
        static class OrientDbPool {
            private int min;
            private int max;

        }
    }
}
