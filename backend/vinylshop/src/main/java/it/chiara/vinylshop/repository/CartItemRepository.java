package it.chiara.vinylshop.repository;

import it.chiara.vinylshop.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_Id(Long cartId);
    void deleteByCartId(Long cartId);
    Optional<CartItem> findByCart_IdAndVinile_Id(Long cartId, Long vinileId);
}