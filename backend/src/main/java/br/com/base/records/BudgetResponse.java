package br.com.base.records;

import java.math.BigDecimal;

public record BudgetResponse (
		Long id,
		String name,
		BigDecimal totalAmount
) {}
