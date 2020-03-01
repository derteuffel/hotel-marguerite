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
    List<Reservation> findAllByDateDebut(Date date);
    List<Reservation> findAllByDateFin(Date date);
    List<Reservation> findAllByNbreNuits(int number);
    List<Reservation> findAllByDateJour(Date date);
}
