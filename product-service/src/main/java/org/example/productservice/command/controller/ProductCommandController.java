package org.example.productservice.command.controller;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.example.common.dto.CommonResponseDto;
import org.example.productservice.command.CreateProductCommand;
import org.example.productservice.command.DeleteProductCommand;
import org.example.productservice.command.UpdateProductCommand;
import org.example.productservice.dto.CreateProductDto;
import org.example.productservice.dto.UpdateProductDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductCommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto<Void>> create(@RequestBody CreateProductDto dto) {
        CreateProductCommand command = CreateProductCommand.builder()
                .productId(UUID.randomUUID())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
        commandGateway.sendAndWait(command);
        return ResponseEntity.ok(CommonResponseDto.success("Product created successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<CommonResponseDto<Void>> update(@RequestBody UpdateProductDto dto) {
        UpdateProductCommand command = UpdateProductCommand.builder()
                .productId(dto.getProductId())
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
        commandGateway.sendAndWait(command);
        return ResponseEntity.ok(CommonResponseDto.success("Product updated successfully"));
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<CommonResponseDto<Void>> delete(@PathVariable("productId") UUID productId) {
        DeleteProductCommand command = DeleteProductCommand.builder()
                .productId(productId)
                .build();
        commandGateway.sendAndWait(command);
        return ResponseEntity.ok(CommonResponseDto.success("Product deleted successfully"));
    }
}
