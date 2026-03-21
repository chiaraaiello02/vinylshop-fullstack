package it.chiara.vinylshop.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class OrdineDto  {

    private Long id;

    private Long userId;

    private String username;

    private Double totaleSpeso;

    private LocalDateTime dataOrdine;

    private List<OrdineItemDto> items;

}
