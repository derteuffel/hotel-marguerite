package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution,Long> {

    List<Distribution> findAllByNom(String nom);
    List<Distribution> findAllByDate(String date);
    List<Distribution> findAllBySecteur(String secteur);
    List<Distribution> findAllByCompte_Id(Long id);
    List<Distribution> findAllByStock_Id(Long id);
}
