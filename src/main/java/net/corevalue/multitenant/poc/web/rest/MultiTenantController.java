package net.corevalue.multitenant.poc.web.rest;

import lombok.RequiredArgsConstructor;
import net.corevalue.multitenant.poc.services.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class MultiTenantController {
    private final ClientService clientService;

    @GetMapping("/countries")
    public ResponseEntity<?> countries() {
        return ResponseEntity.ok(clientService.getCountries());
    }

    @GetMapping("/countries/{countryCode}/customers")
    public ResponseEntity<?> customers(@PathVariable String countryCode) {
        return ResponseEntity.ok(clientService.getCustomerDetails(countryCode));
    }
}
