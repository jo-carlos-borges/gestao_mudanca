package br.com.base.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.base.domain.Budget;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
	
	List<Budget> findAllByUser_Id(Long userId);
	
	Optional<Budget> findByIdAndUser_Id(Long id, Long userId);
}
