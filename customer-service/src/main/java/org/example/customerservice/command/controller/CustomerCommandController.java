package org.example.customerservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.customerservice.command.CreateCustomerCommand;
import org.example.customerservice.command.DeleteCustomerCommand;
import org.example.customerservice.command.UpdateCustomerCommand;
import org.example.customerservice.dto.CreateCustomerDto;
import org.example.customerservice.dto.UpdateCustomerDto;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/api/customers", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CustomerCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/create")
    public CompletableFuture<CommonResponseDto<Void>> create(@Valid @RequestBody CreateCustomerDto dto) {
        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .customerId(UUID.randomUUID())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Customer created successfully"));
    }

    @PutMapping("/update")
    public CompletableFuture<CommonResponseDto<Void>> update(@Valid @RequestBody UpdateCustomerDto dto) {
        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .customerId(dto.getCustomerId())
                .name(dto.getName())
                .email(dto.getEmail())
                .active(dto.isActive())
                .creditApproved(dto.isCreditApproved())
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Customer updated successfully"));
    }

    @DeleteMapping("/delete/{customerId}")
    public CompletableFuture<CommonResponseDto<Void>> delete(@PathVariable("customerId") UUID customerId) {
        DeleteCustomerCommand command = DeleteCustomerCommand.builder()
                .customerId(customerId)
                .build();

        return commandGateway.send(command)
                .thenApply(result -> CommonResponseDto.success("Customer deleted successfully"));
    }
}

