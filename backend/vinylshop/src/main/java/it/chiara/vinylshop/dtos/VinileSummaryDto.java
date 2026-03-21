package it.chiara.vinylshop.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VinileSummaryDto {
    private Long id;
    private String codVinile;
    private String titolo;
    private String artista;
}
