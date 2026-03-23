package it.chiara.vinylshop.repository;

import it.chiara.vinylshop.entities.Vinile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VinileRepository extends JpaRepository<Vinile, Long> {

    Optional<Vinile> findByCodVinile(String codVinile);

    boolean existsByCodVinile(String codVinile);

    @Query("""
       SELECT v
       FROM Vinile v
       JOIN v.categoria c
       WHERE LOWER(TRIM(c.nome)) = LOWER(TRIM(:nome))
       """)
    Page<Vinile> findByCategoriaNome(@Param("nome") String nome, Pageable pageable);

    @Query("""
       SELECT v
       FROM Vinile v
       JOIN v.categoria c
       WHERE (:categoria IS NULL OR LOWER(TRIM(c.nome)) = LOWER(TRIM(:categoria)))
         AND (:q IS NULL
              OR LOWER(v.titolo) LIKE LOWER(CONCAT('%', :q, '%'))
              OR LOWER(v.artista) LIKE LOWER(CONCAT('%', :q, '%')))
       """)
    Page<Vinile> searchVinili(@Param("categoria") String categoria,
                              @Param("q") String q,
                              Pageable pageable);




}