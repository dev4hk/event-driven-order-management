package org.example.common.events;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerDeletedEvent {
    private UUID customerId;
}
