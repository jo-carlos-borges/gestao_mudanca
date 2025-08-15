package br.com.base.controller;

import br.com.base.records.ShoppingItemRequest;
import br.com.base.records.ShoppingItemResponse;
import br.com.base.services.ShoppingItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shopping-items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class ShoppingItemController {
	
	private final ShoppingItemService shoppingItemService;
	
	@PostMapping
    public ResponseEntity<ShoppingItemResponse> createItem(@Valid @RequestBody ShoppingItemRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ShoppingItemResponse createdItem = shoppingItemService.createItem(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    @GetMapping
    public ResponseEntity<Page<ShoppingItemResponse>> findItemsByOwner(@AuthenticationPrincipal UserDetails userDetails, Pageable pageable) {
        Page<ShoppingItemResponse> items = shoppingItemService.findAllByOwner(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShoppingItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody ShoppingItemRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        ShoppingItemResponse updatedItem = shoppingItemService.updateItem(id, request, userDetails.getUsername());
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        shoppingItemService.deleteItem(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
