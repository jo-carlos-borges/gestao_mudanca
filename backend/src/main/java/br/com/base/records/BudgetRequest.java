package br.com.base.records;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BudgetRequest (
		@NotBlank
		@Size(max = 100)
		String name,
		
		@NotNull
		@Positive
		BigDecimal totalAmount
) {}
