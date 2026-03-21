package it.chiara.vinylshop.controllers;

import it.chiara.vinylshop.dtos.CategoriaDto;
import it.chiara.vinylshop.exceptions.NotFoundException;
import it.chiara.vinylshop.services.api.CategoriaService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/cat")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @SneakyThrows
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<CategoriaDto>> getCat() {

        log.info("****** Otteniamo le Categorie *******");

        List<CategoriaDto> categorie = categoriaService.selTutti();

        if (categorie.isEmpty()) {
            String errMsg = "Nessun elemento esistente!";
            log.warning(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new ResponseEntity<>(categorie, HttpStatus.OK);
    }
}