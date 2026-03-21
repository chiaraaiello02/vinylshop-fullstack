package it.chiara.vinylshop.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long id;

    private String codVinile;

    private Integer quantity;

    private String titolo;

    private double prezzoUnitario;

    //quantity*prezzounitario
    private double totaleRiga;


}
