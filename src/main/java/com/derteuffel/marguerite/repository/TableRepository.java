package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<Table,Long> {
}
