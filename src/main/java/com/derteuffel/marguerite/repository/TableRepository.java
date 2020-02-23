package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table,Long> {

    List<Table> findAllBySecteur(String secteur);
    List<Table> findAllByNbre_place(int number);
}
