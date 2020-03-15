package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Bon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Bon, Long> {
    List<Bon> findAllBySecteur(String secteur);
    Optional<Bon> findBySecteurAndCommande_Id(String secteur, Long id);
}
