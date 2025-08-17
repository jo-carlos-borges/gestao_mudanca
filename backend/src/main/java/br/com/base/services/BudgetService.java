package br.com.base.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.base.domain.Budget;
import br.com.base.domain.User;
import br.com.base.mappers.BudgetMapper;
import br.com.base.records.BudgetRequest;
import br.com.base.records.BudgetResponse;
import br.com.base.repositories.BudgetRepository;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BudgetService {
	
	private final BudgetRepository budgetRepository;
	private final UserRepository userRepository;
	
	private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
	
	
	@Transactional(readOnly = true)
	public List<BudgetResponse> findAllByUser(String userEmail) {
		User user = findUserByEmail(userEmail);
		return budgetRepository.findAllByUser_Id(user.getId())
				.stream()
				.map(BudgetMapper::toResponse)
				.collect(Collectors.toList());
	}
	
	@Transactional
	public BudgetResponse createBudget(BudgetRequest request, String userEmail) {
		User user = findUserByEmail(userEmail);
		
		Budget budget = new Budget();
		budget.setName(request.name());
		budget.setTotalAmount(request.totalAmount());
		budget.setUser(user);
		
		budget = budgetRepository.save(budget);
		return BudgetMapper.toResponse(budget);
	}
	
	@Transactional
	public BudgetResponse updateBudget(Long budgetId, BudgetRequest request, String userEmail) {
		User user = findUserByEmail(userEmail);
		Budget budget = budgetRepository.findByIdAndUser_Id(budgetId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado"));
		
		budget.setName(request.name());
		budget.setTotalAmount(request.totalAmount());
		budget = budgetRepository.save(budget);
		return BudgetMapper.toResponse(budget);
	}
	
	@Transactional
	public void deleteBudget(Long budgetId, String userEmail) {
		User user = findUserByEmail(userEmail);
		Budget budget = budgetRepository.findByIdAndUser_Id(budgetId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orçamento não encontrado"));
		
		budgetRepository.delete(budget);
	}
}
