package com.solofunds.memberaccounting.gateway.config;

import com.solofunds.memberaccounting.gateway.filter.CorrelationIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import static com.solofunds.memberaccounting.messaging.messenger.config.CorrelationIdConfig.DEFAULT_HEADER_TOKEN;
import static com.solofunds.memberaccounting.messaging.messenger.config.CorrelationIdConfig.DEFAULT_MDC_UUID_TOKEN_KEY;

public class CorrelationIdFilterConfiguration {

    @Bean
    public FilterRegistrationBean<CorrelationIdFilter> servletRegistrationBean() {
        final FilterRegistrationBean<CorrelationIdFilter> registrationBean = new FilterRegistrationBean<>();
        final CorrelationIdFilter log4jMDCFilterFilter = new CorrelationIdFilter(DEFAULT_HEADER_TOKEN, DEFAULT_MDC_UUID_TOKEN_KEY, DEFAULT_HEADER_TOKEN);
        registrationBean.setFilter(log4jMDCFilterFilter);
        registrationBean.setOrder(2);
        return registrationBean;
    }
}
