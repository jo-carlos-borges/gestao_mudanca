package br.com.base.records;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse (
		
		Long id,
		String description,
		BigDecimal amount,
		LocalDate date,
		BudgetResponse budget,
		ShoppingItemResponse shoppingItem
) {}
