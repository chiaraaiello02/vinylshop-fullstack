package it.chiara.vinylshop.controllers;

import it.chiara.vinylshop.dtos.OrdineDto;
import it.chiara.vinylshop.dtos.OrdineItemDto;
import it.chiara.vinylshop.services.api.OrdineService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordini")
@CrossOrigin(origins = "http://localhost:4200")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }


    // ADMIN: vede tutti gli ordini
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<OrdineDto>> getAllOrdini() {
        return ResponseEntity.ok(ordineService.getAllOrdini());
    }

    // USER: vede i propri ordini
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @GetMapping("/me")
    public ResponseEntity<List<OrdineDto>> getMyOrdini() {
        return ResponseEntity.ok(ordineService.getMyOrdini());
    }

    @GetMapping("/admin/paginati")
    public ResponseEntity<Page<OrdineDto>> getOrdiniPaginati(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(ordineService.getOrdiniPaginati(page, size));
    }


    // ADMIN: ordini di uno specifico user
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrdineDto>> getOrdiniByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ordineService.getOrdiniByUser(userId));
    }

    // USER/ADMIN: items ordine (solo se è tuo o sei admin)
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/{ordineId}/items")
    public ResponseEntity<List<OrdineItemDto>> getItems(@PathVariable Long ordineId) {
        return ResponseEntity.ok(ordineService.getOrdineItemByOrdineProtected(ordineId));
    }

    // CHECKOUT: crea ordine dal carrello + calcola totale + svuota carrello
    // Non passiamo userId: lo ricaviamo dal token
   // @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/checkout")
    public ResponseEntity<OrdineDto> checkout() {
        OrdineDto created = ordineService.createOrderFromCart();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


}