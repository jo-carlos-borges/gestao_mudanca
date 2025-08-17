package br.com.base.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.base.domain.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{
	
	List<Expense> findAllByUser_IdOrderByDateDesc(Long userId);
	List<Expense> findAllByBudget_IdAndUser_Id(Long budgetId, Long userId);
	
	Optional<Expense> findByIdAndUser_Id(Long id, Long userId);
	
}
