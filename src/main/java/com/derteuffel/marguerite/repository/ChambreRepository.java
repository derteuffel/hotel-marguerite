package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre,Long> {

    List<Chambre> findAllByCategorie(String categorie);
    List<Chambre> findAllByPrix(Float prix);
    List<Chambre> findAllByNbre_lit(int nbrLit);
    List<Chambre> findAllByNbre_piece(int nbre_piece);
    List<Chambre> findAllByNbre_place(int nbre_place);
    List<Chambre> findAllByStatus(Boolean status);
}
