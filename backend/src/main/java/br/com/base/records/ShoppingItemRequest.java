package br.com.base.records;

import br.com.base.domain.enums.shopping.ShoppingItemPriority;
import br.com.base.domain.enums.shopping.ShoppingItemStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ShoppingItemRequest(
    @NotBlank @Size(max = 200)
    String name,

    @Min(1)
    int quantity,

    @NotNull
    ShoppingItemStatus status,

    @NotNull
    ShoppingItemPriority priority,

    @Size(max = 2048)
    String purchaseLink,

    BigDecimal estimatedPrice,
    
    String notes,

    @NotNull
    Long roomId,

    Long responsibleUserId
) {}
