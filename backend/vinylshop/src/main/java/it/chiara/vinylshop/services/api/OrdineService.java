package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.OrdineDto;
import it.chiara.vinylshop.dtos.OrdineItemDto;
import it.chiara.vinylshop.dtos.VinileDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrdineService {

    // ADMIN
    List<OrdineDto> getAllOrdini();

    //Paginazione degli ordini
    Page<OrdineDto> getOrdiniPaginati(int page, int size);

    // ADMIN (opzionale)
    List<OrdineDto> getOrdiniByUser(Long userId);

    // USER/ADMIN (solo i miei)
    List<OrdineDto> getMyOrdini();

    // items (non protetto)
    List<OrdineItemDto> getOrdineItemByOrdine(Long ordineId);

    // items (protetto: mio o admin)
    List<OrdineItemDto> getOrdineItemByOrdineProtected(Long ordineId);

    // checkout (prende user dal token)
    OrdineDto createOrderFromCart();



}