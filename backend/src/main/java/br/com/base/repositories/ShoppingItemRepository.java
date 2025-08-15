package br.com.base.repositories;

import br.com.base.domain.ShoppingItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
	 
	Page<ShoppingItem> findAllByOwner_Id(Long ownerId, Pageable pageable);
	
	Optional<ShoppingItem> findByIdAndOwner_Id(Long id, Long ownerId);
}
