package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Chambre;
import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Reservation;
import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.CompteRepository;
import com.derteuffel.marguerite.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/hotel/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private CompteRepository compteRepository;

    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("reservations", reservationRepository.findAll());
        return "reservations/reservationsList";
    }

    @GetMapping("/form")
    public String form(Model model, Long id){
        List<Chambre> chambres = chambreRepository.findAll();
        List<Compte> comptes = compteRepository.findAll();
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("chambres", chambres);
        model.addAttribute("comptes", comptes);
        return "reservations/new";
    }

    @PostMapping("/save")
    public String save(@Valid Reservation reservation){
        reservationRepository.save(reservation);
        return "redirect:/hotel/reservations/all"  ;
    }

    @GetMapping("/edit/{id}")
    public String updatedReservation(Model model, @PathVariable Long id){
        Reservation reservation =  reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "reservations/edit";
    }

    @PostMapping("/update/{id}")
    public String save(@Valid Reservation reservation, @PathVariable("id") Long id,
                       BindingResult result, Model model, HttpSession session,  Float prixT, String status, int nbreNuits ){
        Chambre chambre = chambreRepository.getOne((Long)session.getAttribute("id"));
        Compte compte = compteRepository.getOne((Long)session.getAttribute("id"));

        reservation.setStatus(Boolean.parseBoolean(status));
        reservation.setNbreNuits(nbreNuits);
        reservation.setPrixT(prixT);
        reservation.setCompte(compte);
        reservation.setChambre(chambre);
        reservationRepository.save(reservation);
        model.addAttribute("reservations", reservationRepository.findAll());
        return "redirect:/hotel/reservations/all";

    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation id:" +id));
        System.out.println("reservation id: " + reservation.getId());
        reservationRepository.delete(reservation);
        model.addAttribute("reservations", reservationRepository.findAll());
        return "redirect:/hotel/reservations/all" ;
    }

    @GetMapping("/detail/{id}")
    public String getReservation(Model model, @PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "reservations/detail";

    }
}
