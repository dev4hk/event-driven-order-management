package org.example.common.events;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerUpdatedEvent {

    private UUID customerId;
    private String name;
    private String email;
    private boolean active;
    private boolean creditApproved;

}
