package it.chiara.vinylshop.repository;

import it.chiara.vinylshop.entities.Vinile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VinileRepository extends JpaRepository<Vinile, Long> {

    Optional<Vinile> findByCodVinile(String codVinile);

    boolean existsByCodVinile(String codVinile);


}