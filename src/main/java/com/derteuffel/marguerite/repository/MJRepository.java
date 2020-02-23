package com.derteuffel.marguerite.repository;


import com.derteuffel.marguerite.domain.MJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MJRepository extends JpaRepository<MJ, Long> {

    List<MJ> findAllByDate(String date);
    List<MJ> findAllByNom(String nom);
    List<MJ> findAllByStock_Id(Long id);
}
