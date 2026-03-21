package it.chiara.vinylshop.repository;

import it.chiara.vinylshop.entities.Ordine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdineRepository extends JpaRepository<Ordine, Long> {
    List<Ordine> findByUserId(Long userId);

    List<Ordine> findAllByOrderByDataOrdineDesc();
}