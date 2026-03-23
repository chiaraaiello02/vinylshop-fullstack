package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.VinileDto;
import it.chiara.vinylshop.entities.Vinile;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VinileService {

    List<VinileDto> getAllVinili();

    Page<VinileDto> getViniliPaginati(int page, int size);

    Page<VinileDto> getViniliPaginatiPerCategoria(String categoria, int page, int size);

    Vinile selByCodVinile(String codVinile);

    VinileDto convertToDto(Vinile vinile);

    boolean isDuplicato(VinileDto vinileDto);

    void saveVinile(VinileDto vinileDto);

    void delVinile(Vinile vinile);
}
