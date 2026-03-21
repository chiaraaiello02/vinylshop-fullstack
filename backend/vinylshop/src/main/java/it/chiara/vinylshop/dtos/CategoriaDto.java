package it.chiara.vinylshop.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaDto {

    private Long id;

    @NotBlank(message = "{NotBlank.Categoria.nome.Validation}")
    private String nome;
}