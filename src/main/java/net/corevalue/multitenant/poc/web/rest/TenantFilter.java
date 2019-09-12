package net.corevalue.multitenant.poc.web.rest;

import lombok.extern.slf4j.Slf4j;
import net.corevalue.multitenant.poc.context.TenantContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class TenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String tenantHeader = request.getHeader("X-TenantID");
        if (tenantHeader != null && !tenantHeader.isEmpty()) {
            TenantContext.setCurrentTenant(tenantHeader);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"No tenant header (X-TenantID) supplied\"}");
            response.getWriter().flush();
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
