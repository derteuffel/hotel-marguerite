package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findAllBySecteur(String secteur);
    Place findByNumTable(String numTable);
    List<Place> findAllByStatus(Boolean status);
}
