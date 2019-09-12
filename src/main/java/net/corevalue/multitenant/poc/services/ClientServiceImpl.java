package net.corevalue.multitenant.poc.services;

import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.corevalue.multitenant.poc.context.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final Map<String, ODatabasePool> orientPools;

    @Value("${multi-tenant.poc.queries.countries}")
    private String countriesQuery;

    @Value("${multi-tenant.poc.queries.customers-by-country}")
    private String customersQuery;

    public List<Map<String, String>> getCountries() {
        List<Map<String, String>> countries = new ArrayList<>();
        String tenant = TenantContext.getCurrentTenant();
        Optional.ofNullable(orientPools.get(tenant)).ifPresent(pool -> {
            try (ODatabaseSession db = pool.acquire()) {
                countries.addAll(db.command(countriesQuery)
                        .stream().map(r -> r.getPropertyNames().stream()
                                .collect(toMap(Function.identity(), v -> r.getProperty(v).toString())))
                        .collect(toList()));
            }
        });
        return countries;
    }

    @Override
    public List<Map<String, String>> getCustomerDetails(String countryCode) {
        List<Map<String, String>> customers = new ArrayList<>();
        String tenant = TenantContext.getCurrentTenant();
        Optional.ofNullable(orientPools.get(tenant)).ifPresent(pool -> {
            try (ODatabaseSession db = pool.acquire()) {
                customers.addAll(db.command(customersQuery, Collections.singletonMap("countryCode", countryCode))
                        .stream().map(r -> r.getPropertyNames().stream()
                                .collect(toMap(Function.identity(), v -> r.getProperty(v).toString())))
                        .collect(toList()));
            }
        });
        return customers;
    }
}
