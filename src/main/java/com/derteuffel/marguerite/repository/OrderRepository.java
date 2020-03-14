package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllBySecteur(String secteur);
    Optional<Order> findBySecteurAndCommande_Id(String secteur, Long id);
}
