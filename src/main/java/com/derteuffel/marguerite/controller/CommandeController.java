package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.*;
import com.derteuffel.marguerite.enums.ESecteur;
import com.derteuffel.marguerite.repository.*;
import com.derteuffel.marguerite.services.CompteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("/hotel/commandes")
public class CommandeController {

    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private CompteService compteService;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FactureRepository factureRepository;

    @Value("${file.upload-dir}")
    private String fileStorage;



    @GetMapping("/all")
    public String findAll(Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("commandes", commandeRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        if (compte.getRoles().size() <= 1 && compte.getRoles().contains(roleRepository.findByName("ROLE_SELLER"))){
            return "redirect:/hotel/commandes/orders";
        }else {
            return "commandes/all";
        }
    }

    @GetMapping("/restaurant/orders")
    public String restaurant(Model model){
        model.addAttribute("secteur",ESecteur.RESTAURANT.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.RESTAURANT.toString(),true);
        model.addAttribute("commandes", commandes);
        return "commandes/all-2";
    }

    @GetMapping("/lounge_bar/orders")
    public String lounge_bar(Model model){
        model.addAttribute("secteur",ESecteur.LOUNGE_BAR.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.LOUNGE_BAR.toString(),true);
        model.addAttribute("commandes", commandes);
        return "commandes/all-2";
    }

    @GetMapping("/terasse/orders")
    public String terasse(Model model){
        model.addAttribute("secteur",ESecteur.TERASSE.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.TERASSE.toString(),true);
        model.addAttribute("commandes", commandes);
        return "commandes/all-2";
    }



    @GetMapping("/save/{id}")
    public String save(Model model, HttpServletRequest request,@PathVariable Long id) {
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Commande commande = new Commande();
        Date date = new Date();
        Place place = placeRepository.getOne(id);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat format1 = new SimpleDateFormat("hh:mm");
        commande.setPlace(place);
        commande.setSecteur(place.getSecteur());
        commande.setNumTable(place.getNumTable());
        commande.setDate(format.format(date));
        commande.setHeure(format1.format(date));
        commande.setNumero("C"+commandeRepository.findAll().size()+commande.getNumTable());
        commande.setStatus(true);
        placeRepository.getOne(id).setStatus(true);
        placeRepository.save(place);
        commande.setMontantT(0.0);
        commande.setRembourse(0.0);
        commande.setCompte(compte);
        commandeRepository.save(commande);

            return "redirect:/hotel/commandes/detail/"+commande.getId();
    }

    @GetMapping("/detail/{id}")
    public String findById(@PathVariable Long id, Model model){
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commande);
        return "commandes/detail";
    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("commande", commande);
        model.addAttribute("places", placeRepository.findAll());
        model.addAttribute("chambres", chambreRepository.findAll());
        return "commandes/update";
    }

    @GetMapping("/rembourse/{id}")
    public String rembourse(@PathVariable Long id, String verser){
        Commande commande = commandeRepository.getOne(id);
        commande.setRembourse(Double.parseDouble(verser) - commande.getMontantT());
        commande.setMontantV(Double.parseDouble(verser));
        commandeRepository.save(commande);
        return "redirect:/hotel/commandes/detail/"+commande.getId();
    }

    @GetMapping("/bill/{id}")
    public String getBill(@PathVariable Long id, Model model){
        Commande commande = commandeRepository.getOne(id);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Facture facture = new Facture();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float> amounts = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        for (Article article : commande.getArticles()){
            names.add(article.getNom());
            amounts.add(article.getPrixT());
            quantities.add(article.getQty());
        }
        Place place = commande.getPlace();
        Facture existFacture = factureRepository.findByNumCmdAndCommande_Id(commande.getNumero(),id);
        if (existFacture != null){
            existFacture.setArticles(names);
            existFacture.setPrices(amounts);
            existFacture.setQuantities(quantities);
            existFacture.setCommande(commande);
            existFacture.setDate(dateFormat.format(date));
            existFacture.setMontantT(commande.getMontantT());
            existFacture.setMontantVerse(commande.getMontantV());
            existFacture.setRemboursement(commande.getRembourse());
            existFacture.setNumCmd(commande.getNumTable());
            existFacture.setNumeroTable(commande.getNumTable());
            factureRepository.save(existFacture);
            model.addAttribute("facture", existFacture);
        }else {
            facture.setArticles(names);
            facture.setPrices(amounts);
            facture.setQuantities(quantities);
            facture.setCommande(commande);
            facture.setDate(dateFormat.format(date));
            facture.setMontantT(commande.getMontantT());
            facture.setMontantVerse(commande.getMontantV());
            facture.setRemboursement(commande.getRembourse());
            facture.setNumCmd(commande.getNumTable());
            facture.setNumeroTable(commande.getNumTable());
            factureRepository.save(facture);
            model.addAttribute("facture", facture);
        }
        place.setStatus(false);
        commande.setStatus(false);
        placeRepository.save(place);
        commandeRepository.save(commande);
        return "commandes/facture";

    }

    @GetMapping("/bills/{id}")
    public String billPdfGenerator(@PathVariable Long id){
        Facture facture = factureRepository.getOne(id);
        Document document = new Document();
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+facture.getNumCmd()+facture.getId()+".pdf").toString())));
            document.open();
            document.add(new Paragraph("Marguerite Hotel"));
            document.add(new Paragraph("Secteur :   "+facture.getCommande().getSecteur()));
            document.add(new Paragraph("Commande Numero :   "+facture.getNumCmd()));
            document.add(new Paragraph("Table Numero :  "+facture.getNumeroTable()));
            document.add(new Paragraph("Date du jour :  "+facture.getDate()));
            document.add(new Paragraph("Listes des articles et quantites "));

            PdfPTable table = new PdfPTable(4);
            table.setSpacingBefore(20f);
            table.setSpacingAfter(20f);
            addTableHeader(table);
            int total=0;
            for (int i = 0; i<facture.getArticles().size();i++){
                table.addCell(""+(i+1));
                table.addCell(""+facture.getArticles().get(i)+" CDF");
                table.addCell(""+facture.getQuantities().get(i)+" CDF");
                table.addCell(""+facture.getPrices().get(i)+" CDF");
                total=+facture.getQuantities().get(i);
                System.out.println("inside the table");
            }
            table.addCell("Total");
            table.addCell("");
            table.addCell(""+total);
            table.addCell(""+facture.getMontantT()+" CDF");

            document.add(table);
            document.add(new Paragraph("Montant verse : "+facture.getMontantVerse()));
            document.add(new Paragraph("Montant rembourse : "+facture.getRemboursement()));
            document.add(new Paragraph("Bien vouloir livrer ces articles a la table citer en haut "));
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

    private void addTableHeader(PdfPTable table) {
        Stream.of("Index","Denomination", "Quantite", "Montant")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commande id:" +id));
        commandeRepository.deleteById(id);

        return "redirect:/hotel/commandes/all";
    }

    @GetMapping("/{numTable}/{status}")
    public String findByStatusAnNumTable(@PathVariable String numTable, @PathVariable Boolean status, Model model){
        Commande commande = commandeRepository.findByNumTableAndStatus(numTable,status);
        model.addAttribute("article", new Article());
        model.addAttribute("commande",commande);
        return "commandes/detail";
    }
}
