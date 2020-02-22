package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompteRepository extends JpaRepository<Compte,Long> {
}
