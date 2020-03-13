package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {

    List<Stock> findAllByCompte_Id(Long id);
    List<Stock> findAllByDate(Date date);
    List<Stock> findAllByNom(String nom);
    List<Stock> findAllByCategorie(String categorie);
    Stock findByNom(String nom);
    List<Stock> findAllByType(String type);
}
