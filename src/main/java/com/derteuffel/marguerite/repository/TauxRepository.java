package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Taux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TauxRepository extends JpaRepository<Taux, Long> {

    Taux findTopByOrderByIdDesc();
    Taux findFirstByOrderByIdDesc();
}
