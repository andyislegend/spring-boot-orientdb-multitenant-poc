package net.corevalue.multitenant.poc.services;

import java.util.Map;
import java.util.Set;

public interface ClientService {
    Set<Map<String, String>> getCountries();
    Set<Map<String, String>> getCustomerDetails(String countryCode);
}
