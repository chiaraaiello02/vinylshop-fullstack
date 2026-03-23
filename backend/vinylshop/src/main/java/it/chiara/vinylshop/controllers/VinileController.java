package it.chiara.vinylshop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.chiara.vinylshop.dtos.InfoMsg;
import it.chiara.vinylshop.dtos.VinileDto;
import it.chiara.vinylshop.entities.Vinile;
import it.chiara.vinylshop.exceptions.BindingException;
import it.chiara.vinylshop.exceptions.DuplicateException;
import it.chiara.vinylshop.exceptions.NotFoundException;
import it.chiara.vinylshop.services.api.VinileService;
import jakarta.validation.Valid;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
@RestController
@RequestMapping("/vinili")
@CrossOrigin(origins = "http://localhost:4200")
public class VinileController {

    private final VinileService vinileService;
    private final ResourceBundleMessageSource errMessage;

    public VinileController(VinileService vinileService, ResourceBundleMessageSource errMessage) {
        this.vinileService = vinileService;
        this.errMessage = errMessage;
    }

    // GET /vinili/tutti
    @SneakyThrows
    @GetMapping(value = "/tutti", produces = "application/json")
    public ResponseEntity<List<VinileDto>> getAllVinili() {

        log.info("****** Otteniamo tutti i vinili *******");

        List<VinileDto> vinili = vinileService.getAllVinili();

        if (vinili.isEmpty()) {
            String errMsg = "Non è stato trovato alcun vinile!";
            log.warning(errMsg);
            throw new NotFoundException(errMsg);
        }

        return new ResponseEntity<>(vinili, HttpStatus.OK);
    }

    //Paginazione del catalogo dei vinili
    @GetMapping("/paginati")
    public ResponseEntity<Page<VinileDto>> getViniliPaginati(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String q) {

        Page<VinileDto> vinili = vinileService.searchVinili(categoria, q, page, size);
        return ResponseEntity.ok(vinili);
    }

    // GET /vinili/cerca/codice/{codVinile}
    @SneakyThrows
    @GetMapping(value = "/cerca/codice/{codVinile}", produces = "application/json")
    public ResponseEntity<?> findByCodVinile(@PathVariable("codVinile") String codVinile) {

        log.info("****** Otteniamo il vinile con codice " + codVinile + " *******");

        Vinile vinile = vinileService.selByCodVinile(codVinile);

        if (vinile == null) {
            String errMsg = String.format("Il vinile con codice %s non e' stato trovato!", codVinile);
            log.warning(errMsg);
            throw new NotFoundException(errMsg);
        }

        VinileDto dto = vinileService.convertToDto(vinile);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // POST /vinili/inserisci
    @SneakyThrows
    @PostMapping(value = "/inserisci", produces = "application/json")
    public ResponseEntity<InfoMsg> createVinile(@Valid @RequestBody VinileDto vinileDto,
                                                BindingResult bindingResult) {

        log.info("Salviamo il vinile con codice " + vinileDto.getCodVinile());

        if (bindingResult.hasErrors()) {
            String msgErr = sortErrors(bindingResult.getFieldErrors());
            log.warning(msgErr);
            throw new BindingException(msgErr);
        }

        // duplicato su codVinile (PK)
        Vinile duplicato = vinileService.selByCodVinile(vinileDto.getCodVinile());
        if (duplicato != null) {
            String msgErr = String.format("Vinile %s presente in catalogo! Impossibile utilizzare il metodo POST",
                    vinileDto.getCodVinile());
            log.warning(msgErr);
            throw new DuplicateException(msgErr);
        }

        vinileService.saveVinile(vinileDto);

        return new ResponseEntity<>(
                new InfoMsg(LocalDate.now(), "Inserimento Vinile eseguito con successo"),
                HttpStatus.CREATED
        );
    }

    // PUT /vinili/modifica
    @SneakyThrows
    @PutMapping(value = "/modifica", produces = "application/json")
    public ResponseEntity<InfoMsg> updateVinile(@Valid @RequestBody VinileDto vinileDto,
                                                BindingResult bindingResult) {

        log.info("Modifichiamo il vinile con codice " + vinileDto.getCodVinile());

        if (bindingResult.hasErrors()) {
            String msgErr = sortErrors(bindingResult.getFieldErrors());
            log.warning(msgErr);
            throw new BindingException(msgErr);
        }

        vinileService.saveVinile(vinileDto);

        return new ResponseEntity<>(
                new InfoMsg(LocalDate.now(), "Modifica Vinile eseguita con successo!"),
                HttpStatus.CREATED
        );
    }

    // DELETE /vinili/elimina/{codVinile}
    @SneakyThrows
    @DeleteMapping(value = "/elimina/{codVinile}", produces = "application/json")
    public ResponseEntity<?> deleteVinile(@PathVariable("codVinile") String codVinile) {

        log.info("Eliminiamo il vinile con codice " + codVinile);

        Vinile vinile = vinileService.selByCodVinile(codVinile);
        if (vinile == null) {
            String msgErr = String.format("Vinile %s non presente in catalogo!", codVinile);
            log.warning(msgErr);
            throw new NotFoundException(msgErr);
        }

        VinileDto vinileDto = vinileService.convertToDto(vinile);
        vinileService.delVinile(vinileDto);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Vinile " + codVinile + " eseguita con successo");

        return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.OK);
    }

    private String sortErrors(List<FieldError> errors) {
        String msgErr = "";
        List<String> valErrors = new ArrayList<>();

        for (FieldError item : errors) {
            valErrors.add(errMessage.getMessage(item, LocaleContextHolder.getLocale()) + ". ");
        }

        Collections.sort(valErrors);

        for (String str : valErrors) {
            msgErr += str;
        }

        return msgErr;
    }
}