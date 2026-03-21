package it.chiara.vinylshop.repository;

import it.chiara.vinylshop.entities.OrdineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineItemRepository extends JpaRepository<OrdineItem, Long> {

    // SELECT * FROM ordine_items WHERE ordine_id = ?
    List<OrdineItem> findByOrdine_Id(Long ordineId);


}