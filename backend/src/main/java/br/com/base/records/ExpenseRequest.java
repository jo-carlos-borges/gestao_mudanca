package br.com.base.records;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ExpenseRequest (
		@NotBlank @Size(max = 200)
		String description,
		
		@NotNull @Positive
		BigDecimal amount,
		
		@NotNull @PastOrPresent
		LocalDate date,
		
		@NotNull
		Long budgetId,
		
		Long shoppingItemId
) {}
