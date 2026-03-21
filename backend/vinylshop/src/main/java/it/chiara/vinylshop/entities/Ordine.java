package it.chiara.vinylshop.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ordini")
@Getter
@Setter
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "totale_speso", nullable = false)
    private double totaleSpeso;

    @Column(name = "data_ordine", nullable = false)
    private LocalDateTime dataOrdine;


    @PrePersist
    protected void onCreate() {
        if (dataOrdine == null) dataOrdine = LocalDateTime.now();

        if (totaleSpeso < 0) totaleSpeso = 0.0;
    }
}