package br.com.base.records;

import br.com.base.domain.enums.shopping.ShoppingItemPriority;
import br.com.base.domain.enums.shopping.ShoppingItemStatus;

import java.math.BigDecimal;

public record ShoppingItemResponse(
    Long id,
    String name,
    int quantity,
    ShoppingItemStatus status,
    ShoppingItemPriority priority,
    String purchaseLink,
    BigDecimal estimatedPrice,
    String notes,
    UserResponse owner,
    UserResponse responsibleUser,
    RoomResponse room
) {}