package org.example.customerservice.command.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.customerservice.command.CreateCustomerCommand;
import org.example.customerservice.command.UpdateCustomerCommand;
import org.example.customerservice.dto.CustomerDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/customers", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Validated
public class CustomerCommandController {

    private final CommandGateway commandGateway;

    @PostMapping
    public ResponseEntity<CommonResponseDto<Void>> create(@Valid @RequestBody CustomerDto customerDto) {

        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .customerId(UUID.randomUUID())
                .name(customerDto.getName())
                .email(customerDto.getEmail())
                .active(customerDto.isActive())
                .creditApproved(customerDto.isCreditApproved())
                .build();
        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(CommonResponseDto.success("Customer created successfully"));

    }

    @PutMapping("/update")
    public ResponseEntity<CommonResponseDto<CustomerDto>> update(@Valid @RequestBody CustomerDto customerDto) {

        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .customerId(UUID.randomUUID())
                .name(customerDto.getName())
                .email(customerDto.getEmail())
                .active(customerDto.isActive())
                .creditApproved(customerDto.isCreditApproved())
                .build();
        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(CommonResponseDto.success("Customer updated successfully"));

    }
}
