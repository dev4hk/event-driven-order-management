# Event-Driven Order Management System

This project is a comprehensive, event-driven order management system built with a microservices architecture. It showcases a modern approach to building scalable and resilient applications using Java, Spring Boot, and Axon Framework. This project is an ideal entry for a junior developer's portfolio, demonstrating a solid understanding of distributed systems and advanced architectural patterns.


## Overview

The Event-Driven Order Management System is designed to handle the entire lifecycle of an order, from customer registration and product management to payment processing and shipping. The use of an event-driven architecture ensures loose coupling between services, allowing for greater flexibility and scalability.

This project serves as a practical example of implementing advanced software engineering concepts that are highly sought after in the industry.

### Prerequisites

- Java 21
- Maven
- PostgreSQL (running instance)
- Axon Server (running instance)

## Architecture

The system is composed of several microservices that communicate with each other through an event bus. Axon Server is used as the event store and message broker, while Eureka Server handles service discovery.

- **Eureka Server:** A service registry that allows microservices to locate and communicate with each other.
- **Microservices:** Each service is responsible for a specific business domain (e.g., customers, products, orders). They are designed to be independent, scalable, and resilient.
- **Axon Server:** The backbone of the event-driven architecture, providing an event store and a message bus for inter-service communication.

## Event-Driven Architecture Patterns

This project heavily utilizes several key event-driven architecture patterns, which are crucial for building robust and scalable distributed systems.

### Command Query Responsibility Segregation (CQRS)

In each microservice, the application is divided into two distinct parts: the **Command** side and the **Query** side.

-   **Command Side:** Handles all the create, update, and delete operations. It is responsible for changing the state of the application and emitting events.
-   **Query Side:** Manages all the read operations. It listens to the events emitted by the command side and updates its own data model, which is optimized for querying.

This separation allows for independent scaling of read and write operations, leading to better performance and a more maintainable codebase.

### Event Sourcing

Instead of storing the current state of an entity, we store a sequence of events that have happened to that entity. The current state is derived by replaying these events.

-   **Benefits:** This provides a full audit log of all changes, makes it easier to debug issues, and allows for the implementation of temporal queries (i.e., querying the state of an entity at a specific point in time).

### Saga Pattern for Distributed Transactions

The Saga pattern is used to manage transactions that span across multiple microservices. The `OrderSaga` in the `order-service` is a prime example of this. It coordinates the complex process of an order, ensuring that if one step fails, compensating actions are triggered to maintain data consistency across the system.

## Event Flow for Order Creation

OrderInitiated[Order Initiated Event] --> ValidateCustomer(Validate Customer)

ValidateCustomer -- Success --> UpdateCustomerInfo(Update Order with Customer Info)
ValidateCustomer -- Failed --> CompensateCancelOrder[Compensation: Cancel Order]

UpdateCustomerInfo -- Success --> ReserveProducts(Reserve Products)
UpdateCustomerInfo -- Failed --> CompensateCancelOrder

ReserveProducts -- All Reserved --> ProcessPayment(Process Payment)
ReserveProducts -- Some/All Failed --> ReleaseProductReservations[Compensation: Release Product Reservations]

ProcessPayment -- Payment Processed --> UpdatePaymentStatus(Update Order with Payment Status)
ProcessPayment -- Failed --> CompensateCancelPayment[Compensation: Cancel Payment]

UpdatePaymentStatus -- Success --> InitiateShipping(Initiate Shipping)
UpdatePaymentStatus -- Failed --> CompensateCancelPayment

InitiateShipping -- Shipping Processed --> UpdateShippingStatus(Update Order with Shipping Status)
InitiateShipping -- Failed --> CompensateCancelShipping[Compensation: Cancel Shipping]

UpdateShippingStatus -- Shipping Delivered --> OrderCompleted[Order Completed Event]
UpdateShippingStatus -- Failed / Other Status --> CompensateCancelShipping

ReleaseProductReservations --> CompensateCancelOrder
CompensateCancelPayment --> ReleaseProductReservations
CompensateCancelShipping --> CompensateCancelPayment

CompensateCancelOrder --> SagaEndCanceled[Saga End: Order Cancelled]
OrderCompleted --> SagaEndCompleted[Saga End: Order Completed]

## Event Flow for Order Cancellation

OrderCancellationRequested[Order Cancellation Requested Event] --> ValidateCustomerForCancellation(Validate Customer for Cancellation)

ValidateCustomerForCancellation -- Success --> CheckPaymentExists{Payment Exists?}
ValidateCustomerForCancellation -- Failed --> SagaEndCancellationFailed[Saga End: Cancellation Failed]

CheckPaymentExists -- Yes --> CancelPayment(Cancel Payment)
CheckPaymentExists -- No --> ReleaseProductReservationsCancel(Release Product Reservations)

CancelPayment -- Payment Cancelled --> UpdatePaymentStatusCancel(Update Order with Payment Status)
CancelPayment -- Failed --> RollbackPayment[Compensation: Rollback Payment]

UpdatePaymentStatusCancel -- Success --> ReleaseProductReservationsCancel
UpdatePaymentStatusCancel -- Failed --> RollbackPayment

ReleaseProductReservationsCancel -- All Products Released --> CancelOrder(Cancel Order)
ReleaseProductReservationsCancel -- Failed --> RollbackOrderCancellation[Compensation: Rollback Order Cancellation]

RollbackPayment --> RollbackOrderCancellation
RollbackOrderCancellation --> SagaEndCancellationFailed

CancelOrder --> OrderCancelled[Order Cancelled Event]
OrderCancelled --> SagaEndCanceled[Saga End: Order Cancelled]

## Technologies Used

- **Java 21:** The core programming language.
- **Spring Boot:** For building the microservices.
- **Axon Framework:** For implementing CQRS and event-sourcing.
- **Spring Data JPA:** For database interactions.
- **Lombok:** To reduce boilerplate code.
- **H2 Database:** An in-memory database for development.
- **Maven:** For project management.

## Microservices

- **BOM Service:** A Maven Bill of Materials (BOM) module to define and manage consistent versions of common dependencies across all microservices.
- **Common Module:** Contains shared code, such as common DTOs, commands, events, and constants, used by multiple services.
- **Customer Service:** Manages customer information.
- **Product Service:** Handles the product catalog and inventory.
- **Order Service:** Manages the entire order lifecycle and orchestrates the Sagas.
- **Payment Service:** Processes payments.
- **Shipping Service:** Manages shipping and delivery.
