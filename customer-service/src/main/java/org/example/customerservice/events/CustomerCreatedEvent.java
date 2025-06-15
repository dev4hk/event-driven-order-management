package org.example.customerservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreatedEvent {

    private UUID customerId;
    private String name;
    private String email;
    private boolean active;
    private boolean creditApproved;

}
