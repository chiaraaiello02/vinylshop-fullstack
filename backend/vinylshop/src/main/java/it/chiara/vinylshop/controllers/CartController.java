package it.chiara.vinylshop.controllers;

import it.chiara.vinylshop.dtos.CartItemDto;
import it.chiara.vinylshop.dtos.InfoMsg;
import it.chiara.vinylshop.services.api.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /cart/{userId}
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<List<CartItemDto>> getCartItems(@PathVariable Long userId) {
        List<CartItemDto> cart = cartService.getCartItems(userId);
        return ResponseEntity.ok(cart);
    }

    // POST /cart/{userId}/add-item
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping(value = "/{userId}/add-item", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> addToCart(@PathVariable Long userId,
                                       @RequestBody CartItemDto item) {

        cartService.addToCart(userId, item.getCodVinile(), item.getQuantity());

        return new ResponseEntity<>(
                new InfoMsg(LocalDate.now(), "Vinile aggiunto al carrello"),
                HttpStatus.CREATED
        );
    }

    // DELETE /cart/rimuovi/{itemId}
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping(value = "/rimuovi/{itemId}", produces = "application/json")
    public ResponseEntity<?> removeItem(@PathVariable Long itemId) {

        cartService.removeItem(itemId);

        return new ResponseEntity<>(
                new InfoMsg(LocalDate.now(), "Vinile rimosso dal carrello"),
                HttpStatus.OK
        );
    }

    // PUT /cart/update-item/{itemId}?quantity=3
    @PreAuthorize("hasAnyAuthority('ROLE_USER')")
    @PutMapping(value = "/update-item/{itemId}", produces = "application/json")
    public ResponseEntity<?> updateCartItem(@PathVariable Long itemId,
                                            @RequestParam int quantity) {

        cartService.updateCartItem(itemId, quantity);

        return ResponseEntity.ok(
                new InfoMsg(LocalDate.now(), "Quantità aggiornata con successo")
        );
    }
}