package com.solofunds.memberaccounting.messaging.messenger.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class ActiveMQConnectionFactoryConfig {

    @Value("${spring.activemq.broker-url}")
    String BROKER_URL;
    @Value("${spring.activemq.user}")
    String ACTIVEMQ_BROKER_USER;
    @Value("${spring.activemq.password}")
    String ACTIVEMQ_BROKER_PASSWORD;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new  ActiveMQConnectionFactory();
        connectionFactory.setTrustAllPackages(true);
        connectionFactory.setBrokerURL(BROKER_URL);
        connectionFactory.setUserName(ACTIVEMQ_BROKER_USER);
        connectionFactory.setPassword(ACTIVEMQ_BROKER_PASSWORD);
        return connectionFactory;
    }

}
