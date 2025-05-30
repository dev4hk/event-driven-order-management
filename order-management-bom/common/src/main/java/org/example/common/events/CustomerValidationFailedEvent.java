package org.example.common.events;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerValidationFailedEvent {

    private UUID customerId;
    private String reason;

}
