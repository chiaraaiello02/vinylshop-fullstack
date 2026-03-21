package it.chiara.vinylshop.dtos;

import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VinileDto {

    private Long id;   // PK tecnica (serve per update)

    @NotBlank(message = "{NotBlank.Vinile.codVinile.Validation}")
    @Size(min = 3, max = 30, message = "{Size.Vinile.codVinile.Validation}")
    private String codVinile;

    @NotBlank(message = "{NotBlank.Vinile.titolo.Validation}")
    @Size(min = 2, max = 100, message = "{Size.Vinile.titolo.Validation}")
    private String titolo;

    private String artista;

    @Size(max = 1000, message = "{Size.Vinile.descrizione.Validation}")
    private String descrizione;

    @NotNull(message = "{NotNull.Vinile.stock.Validation}")
    @Min(value = 0, message = "{Min.Vinile.stock.Validation}")
    private Integer stock;

    @NotNull(message = "{NotNull.Vinile.prezzo.Validation}")
    @PositiveOrZero(message = "{Positive.Vinile.prezzo.Validation}")
    private Double prezzo;

    @NotNull(message = "{NotNull.Vinile.dataInserimento.Validation}")
    private LocalDate dataInserimento;

    @NotNull(message = "{NotNull.Vinile.categoria.Validation}")
    @Valid
    private CategoriaDto categoria;


    @Version
    private Integer version;

    private Integer annoPubblicazione;
}