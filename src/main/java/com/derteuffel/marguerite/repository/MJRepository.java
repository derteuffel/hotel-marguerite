package com.derteuffel.marguerite.repository;


import com.derteuffel.marguerite.domain.MJ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MJRepository extends JpaRepository<MJ, Long> {
}
