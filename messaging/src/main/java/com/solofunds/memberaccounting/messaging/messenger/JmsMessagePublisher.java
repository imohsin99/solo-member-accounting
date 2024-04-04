package com.solofunds.memberaccounting.messaging.messenger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.solofunds.memberaccounting.messaging.messenger.event.Event;
import jakarta.jms.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static com.solofunds.memberaccounting.messaging.messenger.config.CorrelationIdConfig.DEFAULT_HEADER_TOKEN;
import static com.solofunds.memberaccounting.messaging.messenger.config.CorrelationIdConfig.correlationIdHolder;

@Component
@Slf4j
public class JmsMessagePublisher implements MessagePublisher<Event> {

    final
    JmsTemplate jmsTemplate;

    final JmsMessagingTemplate jmsMessagingTemplate;

    private final ObjectMapper mapper = JsonMapper.builder()
            .findAndAddModules()
            .build();

    public JmsMessagePublisher(JmsTemplate jmsTemplate, JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }


    public void publish(Event event, String message) throws Exception {
        // Marshall the Event into JSON
        String eventJson = mapper.writeValueAsString(event);

        // Retrieve the correlationId from ThreadLocal
        String correlationId = correlationIdHolder.get();

        // Send the request message
        MessageCreator messageCreator = messageCreatorSession -> {
            TextMessage requestTextMessage = messageCreatorSession.createTextMessage(eventJson);
            requestTextMessage.setStringProperty(DEFAULT_HEADER_TOKEN, correlationId);
            return requestTextMessage;
        };

        Destination queueDestination = new ActiveMQQueue(event.getDestination());
        jmsTemplate.convertAndSend(queueDestination, eventJson);
        log.info("Published message: "+ message + " to queue: " + event.getDestination());
    }


    @Override
    @Deprecated
    public String publishAndWait(Event event, String destination) throws Exception {
        Connection connection = null;
        Session session = null;

        try {
            // Create a connection and session

            @NotNull
            ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory) jmsTemplate.getConnectionFactory(); // Set in ActiveMQConnectionFactoryConfig
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create a temporary queue for receiving the response
            TemporaryQueue temporaryQueue = session.createTemporaryQueue();

            // Set up a message listener to process the response
            MessageConsumer responseConsumer = session.createConsumer(temporaryQueue);

            // Marshall the Event into JSON
            String eventJson = mapper.writeValueAsString(event);

            // Retrieve the correlationId from ThreadLocal
            String correlationId = correlationIdHolder.get();
            // Send the request message
            MessageCreator messageCreator = messageCreatorSession -> {
                TextMessage requestTextMessage = messageCreatorSession.createTextMessage(eventJson);
                requestTextMessage.setJMSReplyTo(temporaryQueue);
                requestTextMessage.setStringProperty(DEFAULT_HEADER_TOKEN, correlationId);
                return requestTextMessage;
            };

            jmsTemplate.send(destination, messageCreator);
            log.info("Published message to '" + destination + "': " + eventJson + " with temporaryQueue: " + "temp-queue://" + temporaryQueue.getQueueName());

            // Start the connection to receive messages
            connection.start();

            // Wait for the response
            TextMessage responseMessage = (TextMessage) responseConsumer.receive();
            log.info("Received response message: " + responseMessage.getText());


            return responseMessage.getText();
        } catch (JMSException e) {
            log.error("Exception encountered attempting to publishAndWait", e);
            throw e; // let our services/controllers handle the exception as they please
        } finally {
            // Cleanup resources
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    log.error("Exception encountered attempting to close session in JMSMessageProducer", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error("Exception encountered attempting to close connection in JMSMessageProducer", e);
                }
            }
        }
    }

    @Override
    public <R> R publishAndReceive(Event event, String destination, Class<R> type) {
        // Retrieve the correlationId from ThreadLocal
        String correlationId = correlationIdHolder.get();
        // build message
        org.springframework.messaging.Message<Event> request = MessageBuilder
                .withPayload(event)
                .setHeader(DEFAULT_HEADER_TOKEN, correlationId)
                .build();

        //send message and get response
        org.springframework.messaging.Message<?> response =
                jmsMessagingTemplate.sendAndReceive(destination, request);

        //TODO handle null headers, and missing 'status' header
        ResponseStatus status = ResponseStatus.valueOf(response.getHeaders().get(ResponseHeader.STATUS, String.class));
        String message = response.getHeaders().get(ResponseHeader.MESSAGE, String.class);

        log.info("Received response with status: {} and {}: {}",
                status,
                status == ResponseStatus.OK ? "payload" : "error",
                status == ResponseStatus.OK ? response.getPayload() : message
                );

        return switch (status) {
            case OK -> type.cast(response.getPayload());
            case NOT_FOUND -> throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, response.getHeaders().get(ResponseHeader.MESSAGE, String.class)
            );
            case INTERNAL_ERROR -> throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, response.getHeaders().get(ResponseHeader.MESSAGE, String.class)
            );
        };
    }
}