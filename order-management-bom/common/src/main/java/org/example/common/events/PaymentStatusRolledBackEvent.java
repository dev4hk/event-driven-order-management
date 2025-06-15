package org.example.common.events;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.common.constants.PaymentStatus;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusRolledBackEvent {
    private UUID paymentId;
    private UUID orderId;
    private PaymentStatus paymentStatus;
}
