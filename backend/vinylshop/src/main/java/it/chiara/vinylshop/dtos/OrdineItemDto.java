package it.chiara.vinylshop.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrdineItemDto {

    private Long id;

    private VinileSummaryDto vinile;

    private int quantity;

    private double prezzoUnitario;

    private double subtotale;



}
