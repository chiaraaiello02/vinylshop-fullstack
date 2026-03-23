package it.chiara.vinylshop.services.impl;

import it.chiara.vinylshop.dtos.VinileDto;
import it.chiara.vinylshop.entities.Vinile;
import it.chiara.vinylshop.repository.VinileRepository;
import it.chiara.vinylshop.services.api.VinileService;
import jakarta.persistence.OptimisticLockException;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VinileServiceImpl implements VinileService {

    private final ModelMapper modelMapper;
    private final VinileRepository vinileRepository;

    public VinileServiceImpl(ModelMapper modelMapper, VinileRepository vinileRepository) {
        this.modelMapper = modelMapper;
        this.vinileRepository = vinileRepository;
    }

    @Override
    public List<VinileDto> getAllVinili() {
        return vinileRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public Page<VinileDto> getViniliPaginati(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vinileRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<VinileDto> getViniliPaginatiPerCategoria(String categoria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return vinileRepository.findByCategoriaNome(categoria.trim(), pageable)
                .map(this::convertToDto);
    }

    @Override
    public Page<VinileDto> searchVinili(String categoria, String q, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        String categoriaPulita = (categoria == null || categoria.equalsIgnoreCase("Tutti"))
                ? null
                : categoria;

        String qPulita = (q == null || q.trim().isEmpty())
                ? null
                : q.trim();

        return vinileRepository.searchVinili(categoriaPulita, qPulita, pageable)
                .map(this::convertToDto);
    }



    @Override
    public Vinile selByCodVinile(String codVinile) {
        return vinileRepository.findByCodVinile(codVinile).orElse(null);
    }

    @Override
    public VinileDto convertToDto(Vinile vinile) {
        return vinile == null ? null : modelMapper.map(vinile, VinileDto.class);
    }

    private Vinile convertToEntity(VinileDto dto) {
        if (dto == null) return null;

        Vinile entity = modelMapper.map(dto, Vinile.class);

        // Se stai facendo INSERT e non arriva la data, impostala lato backend
        if (entity.getDataInserimento() == null) {
            entity.setDataInserimento(LocalDate.now());
        }

        return entity;
    }

    @Override
    public boolean isDuplicato(VinileDto dto) {
        if (dto == null || dto.getCodVinile() == null || dto.getCodVinile().isBlank()) return false;

        // duplicato per codice
        boolean exists = vinileRepository.existsByCodVinile(dto.getCodVinile());

        // se esiste ma è lo stesso record (update), non è duplicato
        if (exists && dto.getCodVinile() != null) {
            Vinile existing = selByCodVinile(dto.getCodVinile());
            // se stai aggiornando lo stesso vinile (stesso id), ok
            if (existing != null && existing.getId() != null && dto.getCodVinile().equals(existing.getCodVinile())) {
                return false;
            }
        }

        return exists;
    }

    @Override
    public void saveVinile(VinileDto dto) {
        Vinile vinile = convertToEntity(dto);

        try {
            vinileRepository.save(vinile);
        } catch (OptimisticLockException ex) {
            throw new RuntimeException("Conflitto durante l'aggiornamento del vinile: qualcun altro lo ha modificato.");
        }
    }


    @Override
    public void delVinile(VinileDto vinileDto) {
        Vinile vinile = vinileRepository.findByCodVinile(vinileDto.getCodVinile())
                .orElseThrow(() -> new RuntimeException("Vinile non trovato"));

        vinileRepository.delete(vinile);
    }
}