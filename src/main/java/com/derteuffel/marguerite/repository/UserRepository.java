package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findAllByNom(String nom);
    User findByTelephone(String telephone);
    User findByEmail(String email);

    List<User> findAllByQuartier(String quartier);
    List<User> findAllByFonction(String fonction);
}
