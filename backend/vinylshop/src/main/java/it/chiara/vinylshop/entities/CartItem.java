package it.chiara.vinylshop.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "vinile_id"})
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // molti item appartengono a un carrello
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // molti item fanno riferimento a un vinile
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vinile_id", nullable = false)
    private Vinile vinile;

    @Column(nullable = false)
    private int quantity;
}