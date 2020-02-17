package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeRepository extends JpaRepository<Commande,Long> {
}
