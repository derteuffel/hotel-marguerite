package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.*;
import com.derteuffel.marguerite.enums.ECategory;
import com.derteuffel.marguerite.enums.ECategoryChambre;
import com.derteuffel.marguerite.helpers.CompteRegistrationDto;
import com.derteuffel.marguerite.helpers.ReservationCancel;
import com.derteuffel.marguerite.repository.*;
import com.derteuffel.marguerite.services.CompteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Controller
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private CompteService compteService;

    @Autowired
    private TauxRepository tauxRepository;

    @Autowired
    private PiscineRepository piscineRepository;

    @Value("${file.upload-dir}")
    private String fileStorage;

    @Autowired
    private RoleRepository roleRepository;
   

    @Autowired
    private CompteRepository compteRepository;

    @ModelAttribute("compte")
    public CompteRegistrationDto compteRegistrationDto(){
        return new CompteRegistrationDto();
    }

    @GetMapping("/registration")
    public String registrationForm(Model model){
        return "reservations/registration";
    }
    @GetMapping("/access-denied")
    public String access_denied(){
        return "reservations/access-denied";
    }

    @PostMapping("/registration")
    public String registerUserAccount(@ModelAttribute("compte") @Valid CompteRegistrationDto compteDto,
                                      BindingResult result, RedirectAttributes redirectAttributes, Model model, @RequestParam("file") MultipartFile file) {

        System.out.println(compteDto.getUsername());
        Compte existing = compteService.findByUsername(compteDto.getUsername());
        if (existing != null) {
            System.out.println("i'm there");
            result.rejectValue("username", null, "There is already an account registered with that login");
            model.addAttribute("error","There is already an account registered with that login");
        }

        if (result.hasErrors()) {
            System.out.println(result.toString());
            System.out.println("i'm inside");
            return "reservations/registration";
        }

        if (!(file.isEmpty())){
            try{
                // Get the file and save it somewhere
                byte[] bytes = file.getBytes();
                Path path = Paths.get(fileStorage + file.getOriginalFilename());
                Files.write(path, bytes);
            }catch (IOException e){
                e.printStackTrace();
            }
            compteService.save(compteDto, "/downloadFile/"+file.getOriginalFilename());
        }else {
            compteService.save(compteDto,"/img/default.jpeg");
        }

        redirectAttributes.addFlashAttribute("success","You've been registered successfully, try to login to your account");
        return "redirect:/reservations/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "reservations/login";
    }

    @GetMapping("/chambres/orders")
    public String findAllchambre(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        Collection<Chambre> chambres = new ArrayList<>();
        for (Chambre chambre : chambreRepository.findAll()){
            if (!(chambre.getCategorie().equals(ECategoryChambre.APPART.toString()))){
                chambres.add(chambre);
            }
        }
        model.addAttribute("chambres", chambres);
        model.addAttribute("name","CHAMBRES");
        return "reservations/chambres/all-2";
    }
    @GetMapping("/appartements/orders")
    public String findAllAppart(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        Collection<Chambre> appartements = new ArrayList<>();
        for (Chambre chambre : chambreRepository.findAll()){
            if (chambre.getCategorie().equals(ECategoryChambre.APPART.toString())){
                appartements.add(chambre);
            }
        }
        model.addAttribute("chambres", appartements);
        model.addAttribute("name","APPARTEMENTS");
        return "reservations/appartements/all-2";
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
    public String save(@Valid Reservation reservation, String num,String prixU, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        System.out.println(num);
        Chambre chambre = chambreRepository.findByNumero(num);
        Taux taux = tauxRepository.findFirstByOrderByIdDesc();
        reservation.setPrixU(Double.parseDouble(prixU)* taux.getTaux());
        reservation.setPrixT(Double.parseDouble(prixU) * reservation.getNbreNuits());
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
            reservation.setNumReservation((reservation.getChambre().getNumero()+"/"+(reservationRepository.findAll().size()+1)).toUpperCase());
            chambreRepository.save(chambre);
            reservationRepository.save(reservation);
            if (chambre.getCategorie().equals(ECategoryChambre.CLASSIC.toString()) || chambre.getCategorie().equals(ECategoryChambre.VIP.toString())) {
                return "redirect:/reservations/chambres/orders";
            }else {
                return "redirect:/reservations/appartements/orders";
            }
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
    @GetMapping("/annuler/{id}")
    public String cancelReservation(Model model, @PathVariable Long id, ReservationCancel form){
        Reservation reservation =  reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        model.addAttribute("form", form);
        return "reservations/cancel";
    }

    @PostMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, ReservationCancel form, Model model){
        Reservation reservation = reservationRepository.getOne(id);
        reservation.setDateFin(form.getDateFin());
        reservation.setNbreNuits(form.getNbreNuits());
        reservation.setPrixT(reservation.getPrixU()*form.getNbreNuits());
        reservation.setStatus(false);
        Chambre chambre = reservation.getChambre();
        chambre.setStatus(false);
        chambreRepository.save(chambre);
        reservationRepository.save(reservation);
        return "redirect:/reservations/pdf/generate/"+reservation.getId();
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
            return "redirect:/reservations/all";
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
        return "redirect:/reservations/all" ;
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
        Document document = new Document(PageSize.NOTE, 10, 10, 10, 10);
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+"CH"+reservation.getId()+".pdf").toString())));
            document.open();
            Paragraph para1 = new Paragraph("HÔTEL MARGUERITE");
            para1.setAlignment(Paragraph.ALIGN_CENTER);
            para1.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD,
                    BaseColor.GREEN));
            para1.setSpacingAfter(50);
            document.add(para1);

            Paragraph paragraph = new Paragraph("Ident. Nat.: 5-714-K 21286                                                                       N.R.C: 13680 KIN\n" +
                    "Adresse: N°62, Av. Kabinda, Q/Boom,   C/Kinshasa, Réf. : Croisement Av. Kabinda et Av. Bokassa\n" +
                    "Tél : +243 999950570, +243 998386650, +243 816896454, e-mail : margueritehotel@yahoo.fr\n");
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN,6,Font.BOLD));
            document.add(paragraph);
            Paragraph line = new Paragraph("---------------------------------------------------------------------------");
            line.setAlignment(Element.ALIGN_CENTER);
            document.add(line);

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

            Paragraph para12 = new Paragraph("Cout du sejour :  "+reservation.getPrixT()+"$");
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
        model.addAttribute("reservation", reservation);
        return "reservations/facture";
    }

    @GetMapping("/billViewer/{id}")
    public String viewBillPdf(@PathVariable Long id, Model model){
        Reservation reservation = reservationRepository.getOne(id);
        model.addAttribute("item",reservation);
        return "reservations/pdfViewer";
    }

    //----- piscine methods -----//

    @GetMapping("/piscines/all")
    public String all(Model model){
        model.addAttribute("lists",piscineRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        return "reservations/piscines/all";
    }

    @GetMapping("/piscines/form")
    public String form(Model model){
        model.addAttribute("piscine", new Piscine());
        return "reservations/piscines/form";
    }

    @PostMapping("/piscines/save")
    public String save(Piscine piscine, RedirectAttributes redirectAttributes) {

        piscine.setPrixT(piscine.getPrixU() * piscine.getNbreHeure());
        piscineRepository.save(piscine);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/reservations/piscines/all";
    }




}
