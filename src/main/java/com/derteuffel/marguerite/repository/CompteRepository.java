package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte,Long> {

    Optional<Compte> findByUsername(String username);
    Optional<Compte> findByUser_Id(Long id);
}
