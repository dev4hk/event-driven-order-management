# 📝 Event-Driven Order Management System

This project is a comprehensive, event-driven order management system built with a microservices architecture. It showcases a modern approach to building scalable and resilient applications using Java, Spring Boot, and Axon Framework. This project is an ideal entry for a junior developer's portfolio, demonstrating a solid understanding of distributed systems and advanced architectural patterns.

## 📜 Overview

The Event-Driven Order Management System is designed to handle the entire lifecycle of an order, from customer registration and product management to payment processing and shipping. The use of an event-driven architecture ensures loose coupling between services, allowing for greater flexibility and scalability.

This project serves as a practical example of implementing advanced software engineering concepts that are highly sought after in the industry.

### Prerequisites

- Java 21
- Maven
- PostgreSQL (running instance)
- Axon Server (running instance)

## 🏗️ Architecture

The system is composed of several microservices that communicate with each other through an event bus. Axon Server is used as the event store and message broker, while Eureka Server handles service discovery.

-   **🌐 Eureka Server:** A service registry that allows microservices to locate and communicate with each other.
-   **🧩 Microservices:** Each service is responsible for a specific business domain (e.g., customers, products, orders). They are designed to be independent, scalable, and resilient.
-   **⚡ Axon Server:** The backbone of the event-driven architecture, providing an event store and a message bus for inter-service communication.

## 🔄 Event-Driven Architecture Patterns

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

### Materialized Views

To enhance query performance and simplify data retrieval, the system extensively uses **Materialized Views** on the query side. For example, the `Order` entity in the `order-service`'s read model acts as a materialized view. It combines and denormalizes relevant information from different services (like payment status from the Payment Service and shipping details from the Shipping Service) into a single, read-optimized projection. This eliminates the need for complex joins or multiple service calls when querying order details, providing a fast and consistent view of the order's state.

## ✨ Event Flow for Order Creation

The order creation process is managed by a saga that coordinates multiple services. The following tables describe the happy path and the compensation logic for failures.

#### Order Creation Steps (Happy Path)

| Step | Action | Saga Event Handler | On Success | On Failure |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `OrderInitiatedEvent` | `on(OrderInitiatedEvent)` | `ValidateCustomerCommand` | `cancelOrder` |
| 2 | `CustomerValidatedEvent` | `on(CustomerValidatedEvent)` | `UpdateCustomerInfoCommand` | `cancelOrder` |
| 3 | `CustomerInfoUpdatedEvent` | `on(CustomerInfoUpdatedEvent)` | `ReserveProductCommand` | `releaseAllReservedProducts` |
| 4 | `ProductReservedEvent` | `on(ProductReservedEvent)` | `InitiatePaymentCommand` | `releaseAllReservedProducts` |
| 5 | `PaymentInitiatedEvent` | `on(PaymentInitiatedEvent)` | `ProcessPaymentCommand` | `cancelPayment` |
| 6 | `PaymentProcessedEvent` | `on(PaymentProcessedEvent)` | `UpdatePaymentStatusCommand` | `cancelPayment` |
| 7 | `PaymentStatusUpdatedEvent` | `on(PaymentStatusUpdatedEvent)` | `InitiateShippingCommand` | `cancelShipping` |
| 8 | `ShippingInitiatedEvent` | `on(ShippingInitiatedEvent)` | `ProcessShippingCommand` | `cancelShipping` |
| 9 | `ShippingProcessedEvent` | `on(ShippingProcessedEvent)` | `UpdateShippingStatusCommand` | `cancelShipping` |
| 10 | `ShippingStatusUpdatedEvent` | `on(ShippingStatusUpdatedEvent)` | `DeliverShippingCommand` | `cancelShipping` |
| 11 | `ShippingDeliveredEvent` | `on(ShippingDeliveredEvent)` | `CompleteOrderCommand` | `cancelShipping` |
| 12 | `OrderCompletedEvent` | `on(OrderCompletedEvent)` | **Saga Ends** | - |

#### Compensation Chain (Failure Rollback)

| Compensation Action | Triggered By Failure In | Leads To |
| :--- | :--- | :--- |
| `cancelShipping` | Shipping Steps | `cancelPayment` |
| `cancelPayment` | Payment Steps | `releaseAllReservedProducts` |
| `releaseAllReservedProducts`| Product Reservation Step| `cancelOrder` |
| `cancelOrder` | Initial Steps | **Saga Ends** |

## ❌ Event Flow for Order Cancellation

When a user requests to cancel an order, a separate saga is initiated to handle the process.

#### Order Cancellation Steps

| Step | Action | Saga Event Handler | Condition / On Success | On Failure |
| :--- | :--- | :--- | :--- | :--- |
| 1 | `OrderCancellationRequestedEvent` | `handle(OrderCancellationRequestedEvent)` | `ValidateCustomerCommand` | Saga Ends |
| 2 | `CustomerValidatedEvent` | `handle(CustomerValidatedEvent)` | **If paymentId:** `CancelPaymentCommand`<br>**Else:** `ReleaseProductReservationCommand` | Saga Ends |
| 3 | `PaymentCancelledEvent` | `handle(PaymentCancelledEvent)` | `UpdatePaymentStatusCommand` | `rollbackPayment` |
| 4 | `PaymentStatusUpdatedEvent` | `handle(PaymentStatusUpdatedEvent)` | `ReleaseProductReservationCommand` | `rollbackPayment` |
| 5 | `ProductReservationReleasedEvent` | `handle(ProductReservationReleasedEvent)` | `CancelOrderCommand` | `rollbackOrderCancellation` |
| 6 | `OrderCancelledEvent` | `handle(OrderCancelledEvent)` | **Saga Ends** | - |

#### Compensation Chain (Cancellation Failure)

| Compensation Action | Triggered By Failure In | Leads To |
| :--- | :--- | :--- |
| `rollbackPayment` | Payment Cancellation Steps | `rollbackOrderCancellation` |
| `rollbackOrderCancellation`| Any Step After Validation | **Saga Ends** |

## 💻 Technologies Used

![Java](https://img.shields.io/badge/Java-21-blue.svg?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green.svg?style=for-the-badge&logo=spring-boot)
![Axon Framework](https://img.shields.io/badge/Axon%20Framework-4.x-blue.svg?style=for-the-badge)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-blue.svg?style=for-the-badge)
![Lombok](https://img.shields.io/badge/Lombok-a9429e?style=for-the-badge&logo=lombok&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2%20Database-gray.svg?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

## 🧩 Microservices

-   **📜 BOM Service:** A Maven Bill of Materials (BOM) module to define and manage consistent versions of common dependencies across all microservices.
-   **🧱 Common Module:** Contains shared code, such as common DTOs, commands, events, and constants, used by multiple services.
-   **👤 Customer Service:** Manages customer information.
-   **📦 Product Service:** Handles the product catalog and inventory.
-   **🛒 Order Service:** Manages the entire order lifecycle and orchestrates the Sagas.
-   **💳 Payment Service:** Processes payments.
-   **🚚 Shipping Service:** Manages shipping and delivery.