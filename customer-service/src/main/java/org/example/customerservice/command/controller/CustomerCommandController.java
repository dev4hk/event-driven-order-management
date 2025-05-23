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

    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<Void>> create(@Valid @RequestBody CreateCustomerDto createCustomerDto) {

        CreateCustomerCommand command = CreateCustomerCommand.builder()
                .customerId(UUID.randomUUID())
                .name(createCustomerDto.getName())
                .email(createCustomerDto.getEmail())
                .build();
        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(CommonResponseDto.success("Customer created successfully"));

    }

    @PutMapping("/update")
    public ResponseEntity<CommonResponseDto<Void>> update(@Valid @RequestBody UpdateCustomerDto updateCustomerDto) {

        UpdateCustomerCommand command = UpdateCustomerCommand.builder()
                .customerId(UUID.randomUUID())
                .name(updateCustomerDto.getName())
                .email(updateCustomerDto.getEmail())
                .active(updateCustomerDto.isActive())
                .creditApproved(updateCustomerDto.isCreditApproved())
                .build();
        commandGateway.sendAndWait(command);

        return ResponseEntity.ok(CommonResponseDto.success("Customer updated successfully"));

    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<CommonResponseDto<Void>> delete(@PathVariable("customerId") UUID customerId) {
        DeleteCustomerCommand command = DeleteCustomerCommand.builder()
                .customerId(customerId)
                .build();
        commandGateway.sendAndWait(command);
        return ResponseEntity.ok(CommonResponseDto.success("Customer deleted successfully"));
    }


}
