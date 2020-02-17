package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution,Long> {
}
