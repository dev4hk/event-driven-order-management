package org.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.OrderStatus;
import org.example.common.constants.PaymentStatus;
import org.example.common.constants.ShippingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "orders", indexes = {
        @Index(name = "idx_customer_id", columnList = "customerId"),
        @Index(name = "idx_payment_id", columnList = "paymentId"),
        @Index(name = "idx_shipping_id", columnList = "shippingId")
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @Column(nullable = false)
    private UUID orderId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(length = 255)
    private String reason;

    private LocalDateTime updatedAt;

    // Customer Info
    @Column(nullable = false)
    private UUID customerId;

    @Column(length = 100)
    private String customerName;

    @Column(length = 100)
    private String customerEmail;

    // Address Info
    @Column(nullable = false, length = 150)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(nullable = false, length = 5)
    private String zipCode;

    // Payment Info
    @Column
    private UUID paymentId;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    // Shipping Info
    @Column
    private UUID shippingId;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private ShippingStatus shippingStatus;
}
