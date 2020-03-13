package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande,Long> {

    List<Commande> findAllByNumTable(int numTable);
    List<Commande> findAllByHeure(String heure);
    List<Commande> findAllByDate(String date);
    List<Commande> findAllByChambre_Id(Long id);
    List<Commande> findAllByStatus(Boolean status);
    Commande findByNumTableAndStatus(String numTable, Boolean status);
}
