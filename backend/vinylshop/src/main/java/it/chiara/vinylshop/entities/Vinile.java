package it.chiara.vinylshop.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vinili")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vinile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK tecnica

    @Column(name = "codvinile", unique = true, nullable = false)
    @NotBlank(message = "Codice vinile obbligatorio")
    @Size(min = 3, max = 30)
    private String codVinile;  // chiave business

    @Column(nullable = false)
    @NotBlank(message = "Titolo obbligatorio")
    private String titolo;

    private String artista;

    private String descrizione;

    @Column(nullable = false)
    @NotNull(message = "Stock obbligatorio")
    @Min(value = 0, message = "Stock non può essere negativo")
    private Integer stock;

    @Column(nullable = false)
    @NotNull(message = "Prezzo obbligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Prezzo deve essere maggiore di 0")
    private Double prezzo;

    @Column(nullable = false)
    @NotNull(message = "Data inserimento obbligatoria")
    private LocalDate dataInserimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcat", nullable = false)
    private Categoria categoria;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    @Column(name = "anno_pubblicazione")
    private Integer annoPubblicazione;

}