package com.kay.pfie.common.web;

import com.kay.pfie.auth.AuthPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException, jakarta.servlet.ServletException {
        long startNs = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startNs) / 1_000_000;
            String userId = resolveUserId();
            MDC.put("method", request.getMethod());
            MDC.put("path", request.getRequestURI());
            MDC.put("status", Integer.toString(response.getStatus()));
            MDC.put("durationMs", Long.toString(durationMs));
            MDC.put("userId", userId);
            log.info("request completed");
            MDC.remove("method");
            MDC.remove("path");
            MDC.remove("status");
            MDC.remove("durationMs");
            MDC.remove("userId");
        }
    }

    private String resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal principal)) {
            return "-";
        }
        return principal.userId().toString();
    }
}
