package com.example.stripe_payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRequest {
    private Long amount;
    private Long quantity;
    private String name;
    private String currency;
}
