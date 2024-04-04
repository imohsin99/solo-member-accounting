Solo Service

Provides transaction, accounting, and loan functionality.

HTTP requests are sent to Gateway. Gateway creates an Event and publishes it to a queue in ActiveMQ. Service listens for Events to be added to a queue, and consumes/processes Events when they are added. Service makes use of Spring Data to read and write from the Postgres DB, and then returns a response to Gateway via a temporary queue that was created when the Event was published by Gateway. 