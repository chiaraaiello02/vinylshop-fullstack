package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.CartItemDto;

import java.util.List;

public interface CartService {

    List<CartItemDto> getCartItems(Long userId);

    void addToCart(Long userId, String codVinile, Integer quantity);


    void removeItem(Long itemId);

    void updateCartItem(Long itemId, int quantity);
}