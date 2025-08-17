package br.com.base.controller;

import br.com.base.records.ExpenseRequest;
import br.com.base.records.ExpenseResponse;
import br.com.base.services.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse createdExpense = expenseService.createExpense(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> findExpensesByUser(@AuthenticationPrincipal UserDetails userDetails) {
        List<ExpenseResponse> expenses = expenseService.findAllByUser(userDetails.getUsername());
        return ResponseEntity.ok(expenses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ExpenseResponse updatedExpense = expenseService.updateExpense(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedExpense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        expenseService.deleteExpense(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}