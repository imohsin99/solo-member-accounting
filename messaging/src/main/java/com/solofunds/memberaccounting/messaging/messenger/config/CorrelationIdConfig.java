package com.solofunds.memberaccounting.messaging.messenger.config;

public class CorrelationIdConfig {

    public static final String DEFAULT_HEADER_TOKEN = "correlationId";
    public static final String DEFAULT_MDC_UUID_TOKEN_KEY = "correlationId";

    public static ThreadLocal<String> correlationIdHolder = new ThreadLocal<>();
}
