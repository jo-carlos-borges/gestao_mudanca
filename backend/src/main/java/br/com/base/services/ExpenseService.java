package br.com.base.services;

import br.com.base.domain.Budget;
import br.com.base.domain.Expense;
import br.com.base.domain.ShoppingItem;
import br.com.base.domain.User;
import br.com.base.mappers.ExpenseMapper;
import br.com.base.records.ExpenseRequest;
import br.com.base.records.ExpenseResponse;
import br.com.base.repositories.BudgetRepository;
import br.com.base.repositories.ExpenseRepository;
import br.com.base.repositories.ShoppingItemRepository;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ShoppingItemRepository shoppingItemRepository;

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAllByUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        return expenseRepository.findAllByUser_IdOrderByDateDesc(user.getId())
                .stream()
                .map(ExpenseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request, String userEmail) {
        User user = findUserByEmail(userEmail);
        Budget budget = findBudgetByIdAndUser(request.budgetId(), user);
        ShoppingItem shoppingItem = findShoppingItemByIdAndUser(request.shoppingItemId(), user);

        Expense expense = new Expense();
        mapRequestToEntity(request, user, budget, shoppingItem, expense);

        expense = expenseRepository.save(expense);
        return ExpenseMapper.toResponse(expense);
    }
    
    @Transactional
    public ExpenseResponse updateExpense(Long expenseId, ExpenseRequest request, String userEmail) {
        User user = findUserByEmail(userEmail);
        Expense expense = expenseRepository.findByIdAndUser_Id(expenseId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        
        Budget budget = findBudgetByIdAndUser(request.budgetId(), user);
        ShoppingItem shoppingItem = findShoppingItemByIdAndUser(request.shoppingItemId(), user);
        
        mapRequestToEntity(request, user, budget, shoppingItem, expense);

        expense = expenseRepository.save(expense);
        return ExpenseMapper.toResponse(expense);
    }
    
    @Transactional
    public void deleteExpense(Long expenseId, String userEmail) {
        User user = findUserByEmail(userEmail);
        Expense expense = expenseRepository.findByIdAndUser_Id(expenseId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense not found"));
        
        expenseRepository.delete(expense);
    }
    
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private Budget findBudgetByIdAndUser(Long budgetId, User user) {
        return budgetRepository.findByIdAndUser_Id(budgetId, user.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Budget not found for this user"));
    }

    private ShoppingItem findShoppingItemByIdAndUser(Long itemId, User user) {
        if (itemId == null) return null;
        return shoppingItemRepository.findByIdAndOwner_Id(itemId, user.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shopping item not found for this user"));
    }

    private void mapRequestToEntity(ExpenseRequest request, User user, Budget budget, ShoppingItem shoppingItem, Expense expense) {
        expense.setDescription(request.description());
        expense.setAmount(request.amount());
        expense.setDate(request.date());
        expense.setUser(user);
        expense.setBudget(budget);
        expense.setShoppingItem(shoppingItem);
    }
}