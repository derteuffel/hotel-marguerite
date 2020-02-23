package com.derteuffel.marguerite.repository;

import com.derteuffel.marguerite.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    List<Reservation> findAllByCompte_Id(Long id);
    List<Reservation> findAllByChambre_Id(Long id);
    List<Reservation> findAllByStatus(Boolean status);
    List<Reservation> findAllByDate_debut(Date date);
    List<Reservation> findAllByDate_fin(Date date);
    List<Reservation> findAllByNbre_nuits(int number);
    List<Reservation> findAllByDateJour(Date date);
}
