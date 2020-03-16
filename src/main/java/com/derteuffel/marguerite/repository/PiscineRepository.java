package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Piscine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiscineRepository extends JpaRepository<Piscine,Long> {
}
