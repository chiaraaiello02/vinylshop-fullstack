package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.CategoriaDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface CategoriaService {
    public List<CategoriaDto> selTutti();
}
