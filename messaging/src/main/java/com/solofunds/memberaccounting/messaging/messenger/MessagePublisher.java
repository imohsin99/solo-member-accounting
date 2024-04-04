package com.solofunds.memberaccounting.messaging.messenger;

public interface MessagePublisher<T> {
    void publish(T object, String destination) throws Exception;

    @Deprecated
    String publishAndWait(T object, String destination) throws Exception;

    <R> R publishAndReceive(T object, String destination, Class<R> type) throws Exception;
}
