package br.com.base.mappers;

import br.com.base.domain.ShoppingItem;
import br.com.base.records.ShoppingItemResponse;

public class ShoppingItemMapper {
	
	private ShoppingItemMapper() {}
	
	public static ShoppingItemResponse toResponse(ShoppingItem item) {
		if (item == null) {
			return null;
		}
		
		var responsible = item.getResponsibleUser() != null
				? UserMapper.toUserResponse(item.getResponsibleUser())
				: null;
		
		return new ShoppingItemResponse(
				 item.getId(),
	                item.getName(),
	                item.getQuantity(),
	                item.getStatus(),
	                item.getPriority(),
	                item.getPurchaseLink(),
	                item.getEstimatedPrice(),
	                item.getNotes(),
	                UserMapper.toUserResponse(item.getOwner()),
	                responsible,
	                RoomMapper.toResponse(item.getRoom())
        );
	}
}
