package net.corevalue.multitenant.poc.services;

import java.util.List;
import java.util.Map;

public interface ClientService {
    List<Map<String, String>> getCountries();
    List<Map<String, String>> getCustomerDetails(String countryCode);
}
