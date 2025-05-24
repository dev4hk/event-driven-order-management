package org.example.productservice.command;

import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.UUID;

@Data
@Builder
public class DeleteProductCommand {
    @TargetAggregateIdentifier
    private final UUID productId;
}
