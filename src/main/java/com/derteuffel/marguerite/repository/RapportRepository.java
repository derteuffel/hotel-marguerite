package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Rapport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RapportRepository extends JpaRepository<Rapport, Long> {
    Optional<Rapport> findByDate(String date);
    List<Rapport> findAllByCompte_Id(Long id);
}
