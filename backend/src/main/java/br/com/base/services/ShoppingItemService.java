package br.com.base.services;


import br.com.base.domain.Room;
import br.com.base.domain.ShoppingItem;
import br.com.base.domain.User;
import br.com.base.mappers.ShoppingItemMapper;
import br.com.base.records.ShoppingItemRequest;
import br.com.base.records.ShoppingItemResponse;
import br.com.base.repositories.RoomRepository;
import br.com.base.repositories.ShoppingItemRepository;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShoppingItemService {

	private final ShoppingItemRepository shoppingItemRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    
    @Transactional(readOnly = true)
    public Page<ShoppingItemResponse> findAllByOwner(String userEmail, Pageable pageable) {
    	User owner = findUserByEmail(userEmail);
    	return shoppingItemRepository.findAllByOwner_Id(owner.getId(), pageable)
    			.map(ShoppingItemMapper::toResponse);
    }
    
    @Transactional
    public ShoppingItemResponse createItem(ShoppingItemRequest request, String userEmail) {
        User owner = findUserByEmail(userEmail);
        Room room = findRoomByIdAndUser(request.roomId(), owner);
        User responsible = findUserById(request.responsibleUserId());

        ShoppingItem item = new ShoppingItem();
        mapRequestToEntity(request, owner, room, responsible, item);

        item = shoppingItemRepository.save(item);
        return ShoppingItemMapper.toResponse(item);
    }
    
    @Transactional
    public ShoppingItemResponse updateItem(Long itemId, ShoppingItemRequest request, String userEmail) {
        User owner = findUserByEmail(userEmail);
        ShoppingItem item = shoppingItemRepository.findByIdAndOwner_Id(itemId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shopping item not found"));
        
        Room room = findRoomByIdAndUser(request.roomId(), owner);
        User responsible = findUserById(request.responsibleUserId());

        mapRequestToEntity(request, owner, room, responsible, item);
        
        item = shoppingItemRepository.save(item);
        return ShoppingItemMapper.toResponse(item);
    }
    
    @Transactional
    public void deleteItem(long itemId, String userEmail) {
    	User owner = findUserByEmail(userEmail);
        ShoppingItem item = shoppingItemRepository.findByIdAndOwner_Id(itemId, owner.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de compra não encontrado"));
    	
    	shoppingItemRepository.delete(item);
    	
    }
    
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
    
    private Room findRoomByIdAndUser(Long roomId, User user) {
        return roomRepository.findByIdAndUser_Id(roomId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quarto não encontrado para este usuário"));
    }
    
    private User findUserById(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário responsável não encontrado"));
    }
    
    private void mapRequestToEntity(ShoppingItemRequest request, User owner, Room room, User responsible, ShoppingItem item) {
        item.setName(request.name());
        item.setQuantity(request.quantity());
        item.setStatus(request.status());
        item.setPriority(request.priority());
        item.setPurchaseLink(request.purchaseLink());
        item.setEstimatedPrice(request.estimatedPrice());
        item.setNotes(request.notes());
        item.setOwner(owner);
        item.setRoom(room);
        item.setResponsibleUser(responsible);
    }
}
