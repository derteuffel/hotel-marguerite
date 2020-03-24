package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Chambre;
import com.derteuffel.marguerite.domain.Compte;
import com.derteuffel.marguerite.domain.Facture;
import com.derteuffel.marguerite.domain.Reservation;
import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.ReservationRepository;
import com.derteuffel.marguerite.repository.RoleRepository;
import com.derteuffel.marguerite.services.CompteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Controller
@RequestMapping("/hotel/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private CompteService compteService;

    @Autowired
    private RoleRepository roleRepository;
    @Value("${file.upload-dir}")
    private String fileStorage;

    @GetMapping("/all")
    public String findAll(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("reservations", reservationRepository.findAll());
        if (compte.getRoles().size() <= 1 && compte.getRoles().contains(roleRepository.findByName("SELLER"))){
            return "redirect:/hotel/reservations/reservation";
        }else {
            return "reservations/reservations";
        }
    }

    @GetMapping("/reservation")
    public String getAllByStatus(Model model) {
        List<Reservation>reservations = reservationRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("reservations", reservations);
        return "reservations/all";
    }

    @GetMapping("/form/{id}")
    public String form(Model model, @PathVariable Long id){
        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre",chambre);
        model.addAttribute("reservation", new Reservation());
        return "reservations/form";
    }

    @PostMapping("/save")
    public String save(@Valid Reservation reservation, String num, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        System.out.println(num);
        Chambre chambre = chambreRepository.findByNumero(num);
        reservation.setPrixT(reservation.getPrixU() * reservation.getNbreNuits());
        if (chambre != null){
            reservation.setChambre(chambre);
            TimerTask activate = new TimerTask() {
                @Override
                public void run() {
                    chambre.setStatus(true);
                    reservation.setStatus(true);
                    System.out.println("action started");
                }
            };
            TimerTask deactivate = new TimerTask() {
                @Override
                public void run() {
                    chambre.setStatus(false);
                    reservation.setStatus(false);
                }
            };
            Timer timer = new Timer();
            timer.schedule(activate,reservation.getDateDebut());
            timer.schedule(deactivate,reservation.getDateFin());
            reservation.setCompte(compte);
            reservation.setNumReservation((reservation.getChambre().getNumero()+(reservationRepository.findAll().size()+1)).toUpperCase());
            reservationRepository.save(reservation);
            return "redirect:/hotel/reservations/detail/"+reservation.getId();
        }else {
            model.addAttribute("error", "There are no room with the provided number :"+num);
            return "reservations/form";
        }

    }

    @GetMapping("/edit/{id}")
    public String updatedReservation(Model model, @PathVariable Long id){
        Reservation reservation =  reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "reservations/edit";
    }

    @PostMapping("/update")
    public String update(@Valid Reservation reservation, String num, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Chambre chambre = chambreRepository.findByNumero(num);
        reservation.setPrixT(reservation.getPrixU() * reservation.getNbreNuits());
        if (chambre != null){
            reservation.setChambre(chambre);
            reservation.setCompte(compte);
            reservationRepository.save(reservation);
            return "redirect:/hotel/reservations/all";
        }else {
            model.addAttribute("reservation",reservationRepository.getOne(reservation.getId()));
            model.addAttribute("error", "There are no room with the provided number :"+num);
            return "reservations/edit";
        }

    }

    @GetMapping("/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation id:" +id));
        System.out.println("reservation id: " + reservation.getId());
        reservationRepository.delete(reservation);
        return "redirect:/hotel/reservations/all" ;
    }

    @GetMapping("/detail/{id}")
    public String getReservation(Model model, @PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "reservations/detail";

    }


    @GetMapping("/pdf/generate/{id}")
    public String reservationBill(@PathVariable Long id, Model model){
        Reservation reservation = reservationRepository.getOne(id);
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+"CH"+reservation.getId()+".pdf").toString())));
            document.open();
            Paragraph para1 = new Paragraph("MARGUERITE HÔTEL");
            para1.setAlignment(Paragraph.ALIGN_CENTER);
            para1.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD,
                    BaseColor.GREEN));
            para1.setSpacingAfter(50);
            document.add(para1);

            Paragraph para5 = new Paragraph("Date du jour :  "+reservation.getDateJour());
            para5.setAlignment(Paragraph.ALIGN_CENTER);
            para5.setSpacingAfter(10);
            document.add(para5);

            Paragraph para2 = new Paragraph("Secteur :   "+"Resrvation chambre");
            para2.setAlignment(Paragraph.ALIGN_LEFT);
            para2.setSpacingAfter(3);
            document.add(para2);

            Paragraph para3 = new Paragraph("Reservation Numero :   "+reservation.getNumReservation());
            para3.setAlignment(Paragraph.ALIGN_LEFT);
            para3.setSpacingAfter(3);
            document.add(para3);

            Paragraph para4 = new Paragraph("Chambre Numero :  "+reservation.getChambre().getNumero());
            para4.setAlignment(Paragraph.ALIGN_LEFT);
            para4.setSpacingAfter(3);
            document.add(para4);

            Paragraph para6 = new Paragraph("Nom client :  "+reservation.getNomClient());
            para6.setAlignment(Paragraph.ALIGN_LEFT);
            para6.setSpacingAfter(3);
            document.add(para6);

            Paragraph para7 = new Paragraph("Telephone client :  "+reservation.getTelephone());
            para7.setAlignment(Paragraph.ALIGN_LEFT);
            para7.setSpacingAfter(3);
            document.add(para7);

            Paragraph para8 = new Paragraph("Email client :  "+reservation.getEmail());
            para8.setAlignment(Paragraph.ALIGN_LEFT);
            para8.setSpacingAfter(3);
            document.add(para8);

            Paragraph para9 = new Paragraph("Nombre de nuite :  "+reservation.getNbreNuits());
            para9.setAlignment(Paragraph.ALIGN_LEFT);
            para9.setSpacingAfter(3);
            document.add(para9);

            Paragraph para10 = new Paragraph("Debut du sejour :  "+reservation.getDateDebut());
            para10.setAlignment(Paragraph.ALIGN_LEFT);
            para10.setSpacingAfter(3);
            document.add(para10);

            Paragraph para11 = new Paragraph("Fin du sejour :  "+reservation.getDateFin());
            para11.setAlignment(Paragraph.ALIGN_LEFT);
            para11.setSpacingAfter(3);
            document.add(para11);

            Paragraph para12 = new Paragraph("Cout du sejour :  "+reservation.getPrixT());
            para12.setAlignment(Paragraph.ALIGN_LEFT);
            para12.setSpacingAfter(3);
            document.add(para12);

            Paragraph para13 = new Paragraph("Chambre Numero :  "+reservation.getChambre().getNumero());
            para13.setAlignment(Paragraph.ALIGN_LEFT);
            para13.setSpacingAfter(3);
            document.add(para13);
            document.close();
            System.out.println("the job is done!!!");
            reservation.setBillTrace("/downloadFile/"+"CH"+reservation.getId()+".pdf");
            reservationRepository.save(reservation);
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/hotel/reservations/detail/"+reservation.getId();
    }
}
