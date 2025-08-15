package br.com.base.domain;

import br.com.base.domain.enums.shopping.ShoppingItemPriority;
import br.com.base.domain.enums.shopping.ShoppingItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE shopping_item SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class ShoppingItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 200)
	private String name;
	
	@Column(nullable = false)
	private int quantity = 1;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
	private ShoppingItemStatus status = ShoppingItemStatus.PENDING;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
	private ShoppingItemPriority priority = ShoppingItemPriority.IMPORTANT;
	
	@Column(length = 2048)
	private String purchaseLink;

    private BigDecimal estimatedPrice;

    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_user_id")
    private User responsibleUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private boolean deleted = false;
}
