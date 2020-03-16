package com.derteuffel.marguerite.controller;


import com.derteuffel.marguerite.domain.Chambre;
import com.derteuffel.marguerite.domain.Facture;
import com.derteuffel.marguerite.domain.Reservation;
import com.derteuffel.marguerite.repository.ChambreRepository;
import com.derteuffel.marguerite.repository.FactureRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("/hotel/chambres")
public class ChambreController {

    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private FactureRepository factureRepository;
    @Value("${file.upload-dir}")
    private String fileStorage;


    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("chambres", chambreRepository.findAll());
        return "chambres/all";
    }

    @GetMapping("/bedroom")
    public String getAll(Model model){
        model.addAttribute("chambres", chambreRepository.findAll());
        return "chambres/all-2";
    }

    @GetMapping("/form")
    public String form(Model model){
        model.addAttribute("chambre", new Chambre());
        return "chambres/form";
    }

    @PostMapping("/save")
    public String save(Chambre chambre, RedirectAttributes redirectAttributes) {
        chambre.setStatus(true);
        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/hotel/chambres/all";
    }

    @GetMapping("/detail/{id}")
    public String findById(@PathVariable Long id, Model model){

        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "chambres/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "chambres/update";
    }

    @PostMapping("/update")
    public String update(Chambre chambre, RedirectAttributes redirectAttributes){

        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("suuccess","You've been save your data successfully");
        return "redirect:/hotel/chambres/all";

    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chambre id:" +id));
        chambreRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/hotel/chambres/all";
    }

    public String reservation(Long id,Model model){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Facture facture = new Facture();
        ArrayList<String> noms = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<Date> dates = new ArrayList<>();
        ArrayList<Date> dates1 = new ArrayList<>();
        ArrayList<Double> prixT = new ArrayList<>();
        Chambre chambre = chambreRepository.getOne(id);
        for (Reservation reservation :chambre.getReservations()){
            noms.add(reservation.getNomClient());
            prices.add(reservation.getPrixU());
            dates.add(reservation.getDateDebut());
            dates1.add(reservation.getDateFin());
            prixT.add(reservation.getPrixT());

        }

        return "";

    }

    @GetMapping("/reservation/pdf/{id}")
    public String reservationPdf(@PathVariable Long id){
        Facture facture = factureRepository.getOne(id);
        Document document = new Document();
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+facture.getNumCmd()+facture.getId()+".pdf").toString())));
            document.open();
            document.add(new Paragraph("Marguerite Hotel"));
            document.add(new Paragraph("Numéro de la Chambre :   "+facture.getCommande().getChambre().getNumero()));
            document.add(new Paragraph("Categorie :   "+facture.getCommande().getChambre().getCategorie()));
            document.add(new Paragraph("Nombre de lit :  "+facture.getCommande().getChambre().getNbreLit()));
            document.add(new Paragraph("Nombre de pièces :  "+facture.getCommande().getChambre().getNbrePiece()));
            document.add(new Paragraph("Prix :  "+facture.getCommande().getChambre().getPrix()));
            document.close();
            System.out.println("the job is done!!!");

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        facture.setBillTrace("/downloadFile/"+facture.getNumCmd()+facture.getId()+".pdf");
        factureRepository.save(facture);
        return "redirect:"+facture.getBillTrace();
    }


}
