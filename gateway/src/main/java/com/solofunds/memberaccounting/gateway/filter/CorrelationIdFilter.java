package com.solofunds.memberaccounting.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.UUID;

import static com.solofunds.memberaccounting.messaging.messenger.config.CorrelationIdConfig.*;

@Component
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {
    private final String responseHeader;
    private final String mdcKey;
    private final String requestHeader;

    public CorrelationIdFilter() {
        responseHeader = DEFAULT_HEADER_TOKEN;
        mdcKey = DEFAULT_MDC_UUID_TOKEN_KEY;
        requestHeader = DEFAULT_HEADER_TOKEN;
    }

    public CorrelationIdFilter(final String responseHeader, final String mdcTokenKey, final String requestHeader) {
        this.responseHeader = responseHeader;
        this.mdcKey = mdcTokenKey;
        this.requestHeader = requestHeader;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws java.io.IOException, ServletException {
        try {

            final String token = extractToken(request);
            MDC.put(mdcKey, token);
            // Set correlationId in ThreadLocal
            correlationIdHolder.set(token);
            if (StringUtils.hasText(responseHeader)) {
                response.addHeader(responseHeader, token);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(mdcKey);
            correlationIdHolder.remove();
        }
    }

    private String extractToken(final HttpServletRequest request) {
        final String token;
        if (StringUtils.hasText(requestHeader) && StringUtils.hasText(request.getHeader(requestHeader))) {
            token = request.getHeader(requestHeader);
        } else {
            token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            log.warn(requestHeader + " not provided in the request header. Generating a token instead: " + token);
        }
        return token;
    }

    @Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

}

