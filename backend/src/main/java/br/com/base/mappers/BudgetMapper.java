package br.com.base.mappers;

import br.com.base.domain.Budget;
import br.com.base.records.BudgetResponse;

public class BudgetMapper {
	
	private BudgetMapper() {}
	
	public static BudgetResponse toResponse(Budget budget) {
		return new BudgetResponse(
				budget.getId(),
				budget.getName(),
				budget.getTotalAmount()
		);
	}

}
