package com.derteuffel.marguerite.repository;


import com.derteuffel.marguerite.domain.Appartement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppartementRepository extends JpaRepository<Appartement, Long> {


    List<Appartement> findAllByStatus(Boolean status);

    List<Appartement> findAllByPrix(Float prix);
}
