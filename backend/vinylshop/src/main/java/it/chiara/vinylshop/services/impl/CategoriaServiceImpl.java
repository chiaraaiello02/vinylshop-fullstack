package it.chiara.vinylshop.services.impl;

import it.chiara.vinylshop.dtos.CategoriaDto;
import it.chiara.vinylshop.entities.Categoria;
import it.chiara.vinylshop.repository.CategoriaRepository;
import it.chiara.vinylshop.services.api.CategoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final ModelMapper modelMapper;
    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, ModelMapper modelMapper) {
        this.categoriaRepository = categoriaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<CategoriaDto> selTutti() {

        List<Categoria> categorie = categoriaRepository.findAll();

        return categorie.stream()
                .map(source -> modelMapper.map(source, CategoriaDto.class))
                .toList();
    }
}