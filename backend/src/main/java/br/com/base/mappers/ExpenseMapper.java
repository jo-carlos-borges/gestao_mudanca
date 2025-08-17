package br.com.base.mappers;

import br.com.base.domain.Expense;
import br.com.base.records.ExpenseResponse;

public class ExpenseMapper {
	
	private ExpenseMapper() {}
	
	public static ExpenseResponse toResponse(Expense expense) {
		return new ExpenseResponse(
				expense.getId(),
				expense.getDescription(),
				expense.getAmount(),
				expense.getDate(),
				BudgetMapper.toResponse(expense.getBudget()),
				expense.getShoppingItem() != null ? ShoppingItemMapper.toResponse(expense.getShoppingItem()) : null
		);
	}
}
