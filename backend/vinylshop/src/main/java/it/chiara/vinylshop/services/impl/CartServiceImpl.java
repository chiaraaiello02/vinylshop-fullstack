package it.chiara.vinylshop.services.impl;

import it.chiara.vinylshop.dtos.CartItemDto;
import it.chiara.vinylshop.entities.Cart;
import it.chiara.vinylshop.entities.CartItem;
import it.chiara.vinylshop.entities.User;
import it.chiara.vinylshop.entities.Vinile;
import it.chiara.vinylshop.repository.CartItemRepository;
import it.chiara.vinylshop.repository.CartRepository;
import it.chiara.vinylshop.repository.UserRepository;
import it.chiara.vinylshop.repository.VinileRepository;
import it.chiara.vinylshop.services.api.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final VinileRepository vinileRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           VinileRepository vinileRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.vinileRepository = vinileRepository;
    }

    @Override
    public List<CartItemDto> getCartItems(Long userId) {

        Cart cart = cartRepository.findByUser_Id(userId).orElse(null);
        if (cart == null) {
            return List.of();
        }

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        return items.stream().map(item -> {
            Vinile v = item.getVinile();

            CartItemDto dto = new CartItemDto();
            dto.setId(item.getId());
            dto.setQuantity(item.getQuantity());

            if (v != null) {
                dto.setCodVinile(v.getCodVinile());
                dto.setTitolo(v.getTitolo());
                dto.setPrezzoUnitario(v.getPrezzo());
                dto.setTotaleRiga(v.getPrezzo() * item.getQuantity());
            } else {
                dto.setTotaleRiga(0.0);
            }

            return dto;
        }).toList();
    }

    @Override
    public void addToCart(Long userId, String codVinile, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("La quantità deve essere maggiore di 0");
        }

        Cart cart = cartRepository.findByUser_Id(userId).orElse(null);

        if (cart == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User non trovato"));

            Cart newCart = new Cart();
            newCart.setUser(user);

            cart = cartRepository.save(newCart);
        }

        Vinile vinile = vinileRepository.findByCodVinile(codVinile)
                .orElseThrow(() -> new RuntimeException("Vinile non trovato"));

        if (quantity > vinile.getStock()) {
            throw new RuntimeException("Quantità richiesta superiore allo stock disponibile");
        }

        Optional<CartItem> existingItemOpt =
                cartItemRepository.findByCart_IdAndVinile_Id(cart.getId(), vinile.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();

            int nuovaQuantita = existingItem.getQuantity() + quantity;

            if (nuovaQuantita > vinile.getStock()) {
                throw new RuntimeException("Quantità totale superiore allo stock disponibile");
            }

            existingItem.setQuantity(nuovaQuantita);
            cartItemRepository.save(existingItem);

        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setVinile(vinile);
            item.setQuantity(quantity);

            cartItemRepository.save(item);
        }
    }

    @Override
    public void removeItem(Long itemId) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trovato"));

        cartItemRepository.delete(item);
    }

    @Override
    public void updateCartItem(Long itemId, int quantity) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item non trovato"));

        if (quantity <= 0) {
            throw new RuntimeException("La quantità deve essere maggiore di 0");
        }

        Vinile vinile = item.getVinile();

        if (vinile == null) {
            throw new RuntimeException("Vinile associato non trovato");
        }
        if (quantity > vinile.getStock()) {
            throw new RuntimeException("Quantità richiesta superiore allo stock disponibile");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
}