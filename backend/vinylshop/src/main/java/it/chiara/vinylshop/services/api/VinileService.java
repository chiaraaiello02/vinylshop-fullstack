package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.VinileDto;
import it.chiara.vinylshop.entities.Vinile;

import java.util.List;

public interface VinileService {

    List<VinileDto> getAllVinili();

    Vinile selByCodVinile(String codVinile);

    VinileDto convertToDto(Vinile vinile);

    boolean isDuplicato(VinileDto vinileDto);

    void saveVinile(VinileDto vinileDto);

    void delVinile(Vinile vinile);
}
