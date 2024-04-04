Messaging Library

Common library that provides event messaging functionality. Messaging must be built before Gateway and Service, as it is a dependency in both modules. Messaging makes use of Spring's JmsTemplate to publish and consume messages to/from ActiveMQ. 