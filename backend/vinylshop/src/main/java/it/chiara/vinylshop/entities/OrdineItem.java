package it.chiara.vinylshop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ordine_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // riferimento all'ordine
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    // riferimento al vinile (FK verso VINILI.id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vinile_id", nullable = false)
    private Vinile vinile;

    @Column(name = "quantita", nullable = false)
    private int quantity;

    // prezzo snapshot: prezzo pagato al momento dell’ordine
    @Column(name = "prezzo_unitario", nullable = false)
    private double prezzoUnitario;

    // totale riga (prezzoUnitario * quantity)
    @Column(name = "subtotale", nullable = false)
    private double subtotale;

    @PrePersist
    protected void prePersist() {
        this.subtotale = this.prezzoUnitario * this.quantity;
    }
}