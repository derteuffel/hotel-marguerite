package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.*;
import com.derteuffel.marguerite.enums.ECategoryChambre;
import com.derteuffel.marguerite.enums.ESecteur;
import com.derteuffel.marguerite.helpers.CompteRegistrationDto;
import com.derteuffel.marguerite.repository.*;
import com.derteuffel.marguerite.services.CompteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.text.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private CompteService compteService;

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private ArticleRepository articleRepository;


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RapportRepository rapportRepository;

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
        return "admin/registration";
    }
    @GetMapping("/access-denied")
    public String access_denied(){
        return "admin/access-denied";
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
            return "admin/registration";
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
        return "redirect:/admin/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "admin/login";
    }

//-------- Commandes methods ------////
    @GetMapping("/commandes/all")
    public String findAll(Model model, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("commandes", commandeRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        if (compte.getRoles().size() <= 1 && compte.getRoles().contains(roleRepository.findByName("ROLE_SELLER"))){
            return "redirect:/hotel/commandes/orders";
        }else {
            return "admin/commandes/all";
        }
    }


    @GetMapping("/commandes/restaurant/all")
    public String restaurantAdmin(Model model){
        model.addAttribute("secteur",ESecteur.RESTAURANT.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.RESTAURANT.toString(),true);
        model.addAttribute("commandes", commandes);
        return "admin/commandes/all";
    }

    @GetMapping("/commandes/lounge_bar/all")
    public String lounge_barAdmin(Model model){
        model.addAttribute("secteur",ESecteur.LOUNGE_BAR.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.LOUNGE_BAR.toString(),true);
        model.addAttribute("commandes", commandes);
        return "admin/commandes/all";
    }

    @GetMapping("/commandes/terasse/all")
    public String terasseAdmin(Model model){
        model.addAttribute("secteur",ESecteur.TERASSE.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.TERASSE.toString(),true);
        model.addAttribute("commandes", commandes);
        return "admin/commandes/all";
    }



    @GetMapping("/commandes/save/{id}")
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

            return "redirect:/admin/commandes/detail/"+commande.getId();
    }

    @GetMapping("/commandes/detail/{id}")
    public String findById(@PathVariable Long id, Model model){
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commande);
        return "admin/commandes/detail";
    }


    @GetMapping("/commandes/rembourse/{id}")
    public String rembourse(@PathVariable Long id, String verser){
        Commande commande = commandeRepository.getOne(id);
        commande.setRembourse(Double.parseDouble(verser) - commande.getMontantT());
        commande.setMontantV(Double.parseDouble(verser));
        commandeRepository.save(commande);
        return "redirect:/admin/commandes/detail/"+commande.getId();
    }

    @GetMapping("/commandes/bill/{id}")
    public String getBill(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes){
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
            System.out.println("Je suis ici "+existFacture.getNumCmd());
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
                facture.setNumCmd(commande.getNumTable());
                facture.setNumeroTable(commande.getNumTable());
                factureRepository.save(facture);
                model.addAttribute("facture", facture);
        }
        place.setStatus(false);
        commande.setStatus(false);
        placeRepository.save(place);
        commandeRepository.save(commande);
        return "admin/commandes/facture";

    }

    @GetMapping("/commandes/bills/{id}")
    public String billPdfGenerator(@PathVariable Long id, Model model){
        Facture facture = factureRepository.getOne(id);
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+facture.getNumCmd()+facture.getId()+".pdf").toString())));
            document.open();
            Paragraph para1 = new Paragraph("Marguerite Hotel");
            para1.setAlignment(Paragraph.ALIGN_CENTER);
            para1.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD,
                    BaseColor.GREEN));
            para1.setSpacingAfter(50);
            document.add(para1);


            Paragraph para2 = new Paragraph("Secteur :   "+facture.getCommande().getSecteur());
            para2.setAlignment(Paragraph.ALIGN_LEFT);
            para2.setSpacingAfter(3);
            document.add(para2);
            Paragraph para3 = new Paragraph("Commande Numero :   "+facture.getNumCmd());
            para3.setAlignment(Paragraph.ALIGN_LEFT);
            para3.setSpacingAfter(3);
            document.add(para3);
            Paragraph para4 = new Paragraph("Table Numero :  "+facture.getNumeroTable());
            para4.setAlignment(Paragraph.ALIGN_LEFT);
            para4.setSpacingAfter(3);
            document.add(para4);
            Paragraph para5 = new Paragraph("Date du jour :  "+facture.getDate());
            para5.setAlignment(Paragraph.ALIGN_LEFT);
            para5.setSpacingAfter(3);
            document.add(para5);

            PdfPTable table = new PdfPTable(4);
            table.setSpacingBefore(20f);
            table.setSpacingAfter(20f);
            addTableHeader(table);
            int total=0;
            for (int i = 0; i<facture.getArticles().size();i++){
                table.addCell(""+(i+1));
                table.addCell(""+facture.getArticles().get(i));
                table.addCell(""+facture.getQuantities().get(i));
                table.addCell(""+facture.getPrices().get(i)+" CDF");
                total=+facture.getQuantities().get(i);
                System.out.println("inside the table");
            }
            table.addCell("Total");
            table.addCell("");
            table.addCell(""+total);
            table.addCell(""+facture.getMontantT()+" CDF");

            document.add(table);
           /* Paragraph para6 = new Paragraph("Montant verse : "+facture.getMontantVerse());
            para6.setAlignment(Paragraph.ALIGN_RIGHT);
            para6.setSpacingAfter(3);
            document.add(para6);

            Paragraph para7 = new Paragraph("Montant rembourssé : "+facture.getRemboursement());
            para7.setAlignment(Paragraph.ALIGN_RIGHT);
            para7.setSpacingAfter(3);
            document.add(para7);*/

            Paragraph para8 = new Paragraph("Montant à payer : "+facture.getMontantT());
            para8.setAlignment(Paragraph.ALIGN_RIGHT);
            para8.setSpacingAfter(6);
            document.add(para8);

            document.add(new Paragraph("Bien vouloir livrer ces articles a la table cite en haut "));
            document.close();
            System.out.println("the job is done!!!");
            facture.setBillTrace("/downloadFile/"+facture.getNumCmd()+facture.getId()+".pdf");
            factureRepository.save(facture);
        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("facture",facture);
        return "admin/commandes/facture";
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

    @GetMapping("/commandes/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid commande id:" +id));
        commandeRepository.deleteById(id);

        return "redirect:/admin/commandes/all";
    }

    @GetMapping("/commandes/{numTable}/{status}")
    public String findByStatusAnNumTable(@PathVariable String numTable, @PathVariable Boolean status, Model model){
        Commande commande = commandeRepository.findByNumTableAndStatus(numTable,status);
        model.addAttribute("article", new Article());
        model.addAttribute("commande",commande);
        return "admin/commandes/detail";
    }


    //---------- Articles methodes -------//
    @GetMapping("/articles/all")
    public String findAll(Model model){
        model.addAttribute("articles", articleRepository.findAll());

        return "admin/articles/all";
    }

    @GetMapping("/articles/form/{id}")
    public String form(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "admin/articles/formArticle";
    }

    @PostMapping("/articles/save/{type}/{id}")
    public String save(Article article, @PathVariable Long id, RedirectAttributes redirectAttributes, @PathVariable String type) {
        Commande commande = commandeRepository.getOne(id);
        article.setPrixT(article.getPrixU() * article.getQty());
        System.out.println(commande.getMontantT());
        commande.setMontantT(commande.getMontantT() + article.getPrixT());
        article.setCommande(commande);
        article.setDate(commande.getDate());
        article.setType(type.toUpperCase());
        articleRepository.save(article);
        commandeRepository.save(commande);
        redirectAttributes.addFlashAttribute("success","You've added successfully your article");
        return "redirect:/admin/commandes/detail/"+commande.getId();
    }

    @GetMapping("/articles/add/{id}")
    public String findById(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Article article = articleRepository.getOne(id);
        article.setQty(article.getQty() + 1);
        article.setPrixT(article.getQty() * article.getPrixU());
        Commande commande = commandeRepository.getOne(article.getCommande().getId());
        commande.setMontantT(commande.getMontantT() + article.getPrixU());
        commandeRepository.save(commande);
        articleRepository.save(article);

        return "redirect:/admin/commandes/detail/"+commande.getId();
    }


    @GetMapping("/articles/orders/{id}")
    public String orders(@PathVariable Long id,Model model){
        Commande commande = commandeRepository.getOne(id);
        Optional<Bon> existDrinks = orderRepository.findBySecteurAndCommande_Id("DRINK",id);
        Optional<Bon> existFoods = orderRepository.findBySecteurAndCommande_Id("FOOD",id);

        model.addAttribute("commande",commande);
        if (existDrinks.isPresent()){
            model.addAttribute("drinks",existDrinks.get());
        }else {
            List<Bon> nullDrinks = orderRepository.findAllBySecteur(null);
            if (!(nullDrinks.isEmpty())) {
                orderRepository.deleteAll(nullDrinks);
            }
            Bon drinks = new Bon();
            for (Article item : commande.getArticles()) {
                if (item.getType().equals("DRINK")) {
                    drinks.setSecteur(item.getType());
                    drinks.setCommande(commande);
                    drinks.setNumBon("BD"+(orderRepository.findAllBySecteur("DRINK").size()+1));
                    drinks.getItems().add(item.getNom());
                    drinks.getQuantities().add(item.getQty());
                    drinks.setNumTable(commande.getNumTable());
                }
            }
            orderRepository.save(drinks);
            model.addAttribute("drinks", drinks);
        }

        if (existFoods.isPresent()){
            model.addAttribute("foods",existFoods.get());
        }else {
            List<Bon> nullFoods = orderRepository.findAllBySecteur(null);
            if (!(nullFoods.isEmpty())) {
                orderRepository.deleteAll(nullFoods);
            }
            Bon foods = new Bon();
            for (Article item : commande.getArticles()) {
                if (item.getType().equals("FOOD")) {
                    foods.setSecteur(item.getType());
                    foods.setCommande(commande);
                    foods.setNumBon("BF"+(orderRepository.findAllBySecteur("FOOD").size()+1));
                    foods.getItems().add(item.getNom());
                    foods.getQuantities().add(item.getQty());
                    foods.setNumTable(commande.getNumTable());
                }
            }
            orderRepository.save(foods);
            model.addAttribute("foods", foods);
        }


        return "admin/commandes/orders";

    }

    @GetMapping("/articles/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Article article = articleRepository.getOne(id);
        model.addAttribute("article", article);
        model.addAttribute("commandes", commandeRepository.findAll());
        return "admin/commandes/edit";
    }



    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(Model model, @PathVariable Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article id:" +id));
        articleRepository.deleteById(id);
        model.addAttribute("articles", articleRepository.findAll());
        return "redirect:/admin/articles/all";
    }

    @GetMapping("/articles/orders/pdf/{id}")
    public String pdfGenerator(@PathVariable Long id){
        Bon bon = orderRepository.getOne(id);
        Document document = new Document();
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+bon.getSecteur()+bon.getId()+".pdf").toString())));
            document.open();
            document.add(new Paragraph("Marguerite Hotel"));
            document.add(new Paragraph("Secteur :   "+bon.getCommande().getSecteur()));
            document.add(new Paragraph("Numero Table :  "+bon.getCommande().getNumTable()));
            document.add(new Paragraph("Numero du bon :  "+bon.getNumBon()));
            document.add(new Paragraph("Listes des articles et quantites "));

            PdfPTable table = new PdfPTable(3);
            table.setSpacingAfter(20f);
            table.setSpacingBefore(20f);
            addTableHeader(table);
            for (int i = 0; i<bon.getItems().size();i++){
                table.addCell(""+(i+1));
                table.addCell(""+bon.getItems().get(i));
                table.addCell(""+bon.getQuantities().get(i));
                System.out.println("inside the table");
            }

            document.add(table);
            document.add(new Paragraph("Bien vouloir livrer ces articles a la table citer en haut "));
            document.close();
            System.out.println("the job is done!!!");

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bon.setPdfTrace("/downloadFile/"+bon.getSecteur()+bon.getId()+".pdf");
        orderRepository.save(bon);

        return "redirect:/admin/articles/orders/"+bon.getCommande().getId();
    }

    @GetMapping("/articles/orders/print/{id}")
    public String buildPdf(@PathVariable Long id){
        return "redirect:"+orderRepository.getOne(id).getPdfTrace();
    }


    private void addTableHeaderArticle(PdfPTable table) {
        RapportController.addTableHeader(table);
    }


    //------- Chambre methods -----//

    @GetMapping("/chambres/all")
    public String findAllchambre(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        model.addAttribute("chambres", chambreRepository.findAll());
        /*for (int i=0;i<10;i++){
            Chambre chambre = new Chambre();
            chambre.setStatus(false);
            chambre.setCategorie(ECategoryChambre.CLASSIC.toString());
            chambre.setNumero("10"+i);
            chambre.setPrix(35F);
            chambreRepository.save(chambre);
        }
        for (int i=0;i<10;i++){
            Chambre chambre = new Chambre();
            chambre.setStatus(false);
            chambre.setCategorie(ECategoryChambre.CLASSIC.toString());
            chambre.setNumero("21"+i);
            chambre.setPrix(35F);
            chambreRepository.save(chambre);
        }
        for (int i=0;i<10;i++){
            Chambre chambre = new Chambre();
            chambre.setStatus(false);
            chambre.setCategorie(ECategoryChambre.CLASSIC.toString());
            chambre.setNumero("32"+i);
            chambre.setPrix(35F);
            chambreRepository.save(chambre);
        }
        for (int i=0;i<10;i++){
            Chambre chambre = new Chambre();
            chambre.setStatus(false);
            chambre.setCategorie(ECategoryChambre.CLASSIC.toString());
            chambre.setNumero("43"+i);
            chambre.setPrix(35F);
            chambreRepository.save(chambre);
        }*/
        //model.addAttribute("chambre", new Chambre());
        return "admin/chambres/all";
    }


    @PostMapping("/chambres/save")
    public String save(Chambre chambre, RedirectAttributes redirectAttributes) {
        chambre.setStatus(false);
        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/admin/chambres/all";
    }

    @GetMapping("/chambres/detail/{id}")
    public String findChambre(@PathVariable Long id, Model model){

        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "admin/chambres/detail";
    }

    @GetMapping("/chambres/edit/{id}")
    public String updatechambre(Model model, @PathVariable Long id) {
        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre", chambre);
        return "admin/chambres/update";
    }

    @PostMapping("/chambres/update")
    public String update(Chambre chambre, RedirectAttributes redirectAttributes){

        chambreRepository.save(chambre);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/admin/chambres/all";

    }

    @GetMapping("/chambres/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Chambre chambre = chambreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid chambre id:" +id));
        chambreRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/admin/chambres/all";
    }

    //----- Distribution methods ------//

    @Autowired
    private DistributionRepository distributionRepository;


    @GetMapping("/distributions/all")
    public String findAllDistribution(Model model){
        model.addAttribute("distributions", distributionRepository.findAll());
        return "admin/distributions/all";
    }

    @GetMapping("/distributions/form/{id}")
    public String formDistribution(Model model,@PathVariable Long id){
        Stock stock = stockRepository.getOne(id);
        model.addAttribute("distribution", new Distribution());
        model.addAttribute("stock", stock);
        return "admin/distributions/new";
    }

    @PostMapping("/distributions/save/{id}")
    public String save(@Valid Distribution distribution, @PathVariable Long id){
        Stock stock = stockRepository.getOne(id);
        distribution.setStock(stock);
        distribution.setNom(stock.getNom());
        stock.setQty((stock.getQty() - distribution.getQty()));
        distributionRepository.save(distribution);
        stockRepository.save(stock);
        return "redirect:/admin/distributions/all"  ;
    }





    @GetMapping("/distributions/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model, HttpSession session) {
        Distribution distribution = distributionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid distribution id:" +id));
        System.out.println("distribution id: " + distribution.getNom());
        distributionRepository.delete(distribution);
        model.addAttribute("distributions", distributionRepository.findAll());
        return "redirect:/admin/distributions/all" ;
    }

    //------ Piscine methods ------//

    @Autowired
    private PiscineRepository piscineRepository;

    @GetMapping("/piscines/all")
    public String all(Model model){
        model.addAttribute("lists",piscineRepository.findAll(Sort.by(Sort.Direction.DESC,"id")));
        return "admin/piscines/all";
    }

    @GetMapping("/piscines/form")
    public String formPiscine(Model model){
        model.addAttribute("piscine", new Piscine());
        return "admin/piscines/form";
    }

    @PostMapping("/piscines/save")
    public String save(Piscine piscine, RedirectAttributes redirectAttributes) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        piscine.setDate(sdf.format(new Date()));
        piscine.setPrixT(piscine.getPrixU() * piscine.getNbreHeure());
        piscineRepository.save(piscine);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/admin/piscines/all";
    }

    @GetMapping("/piscines/edit/{id}")
    public String updateFormPiscine(Model model, @PathVariable Long id) {
        Piscine piscine = piscineRepository.getOne(id);
        model.addAttribute("piscine", piscine);
        return "admin/piscines/update";
    }

    @PostMapping("/piscines/update")
    public String update(Piscine piscine, RedirectAttributes redirectAttributes){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        piscine.setDate(sdf.format(new Date()));
        piscine.setPrixT(piscine.getPrixU() * piscine.getNbreHeure());
        piscineRepository.save(piscine);
        redirectAttributes.addFlashAttribute("success","You've been save your data successfully");
        return "redirect:/admin/piscines/all";

    }

    @GetMapping("/piscines/delete/{id}")
    public String deletePiscine(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Piscine piscine = piscineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid piscine id:" +id));
        piscineRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success","You've delete data successfully");
        return "redirect:/admin/piscines/all";
    }

    //------ Places methods -----//


    @GetMapping("/places/restaurant/all")
    public String restaurantPlaces(Model model){
        model.addAttribute("secteur",ESecteur.RESTAURANT.toString());
        model.addAttribute("places", placeRepository.findAllBySecteur(ESecteur.RESTAURANT.toString()));
        /*for (int i=1;i<41;i++){
            Place place= new Place();
            place.setStatus(false);
            place.setSecteur(ESecteur.RESTAURANT.toString());
            place.setNumTable(""+i);
            placeRepository.save(place);
        }*/
        return "admin/places/all";
    }

    @GetMapping("/places/terasse/all")
    public String terassePlaces(Model model){
        model.addAttribute("secteur",ESecteur.TERASSE.toString());
        model.addAttribute("places", placeRepository.findAllBySecteur(ESecteur.TERASSE.toString()));
        /*for (int i=1;i<41;i++){
            Place place= new Place();
            place.setStatus(false);
            place.setSecteur(ESecteur.TERASSE.toString());
            place.setNumTable(""+i);
            placeRepository.save(place);
        }*/
        return "admin/places/all";
    }
    @GetMapping("/places/lounge_bar/all")
    public String lounge_barPlaces(Model model){
        model.addAttribute("secteur",ESecteur.LOUNGE_BAR.toString());
        model.addAttribute("places", placeRepository.findAllBySecteur(ESecteur.LOUNGE_BAR.toString()));
        /*for (int i=1;i<41;i++){
            Place place= new Place();
            place.setStatus(false);
            place.setSecteur(ESecteur.LOUNGE_BAR.toString());
            place.setNumTable(""+i);
            placeRepository.save(place);
        }*/
        return "admin/places/all";
    }


    @GetMapping("/places/form")
    public String formPlace(Model model){
        model.addAttribute("place", new Place());
        return "admin/places/form";
    }

    @GetMapping("/places/save/{secteur}")
    public String save( @PathVariable String secteur, RedirectAttributes redirectAttributes){

        Place place = new Place();
        if (secteur.contains("TERASSE")){
            place.setNumTable(("TR"+(placeRepository.findAllBySecteur("TERASSE").size()+1)).toUpperCase());
        }else if (secteur.contains("LOUNGE_BAR")){
            place.setNumTable(("LB"+(placeRepository.findAllBySecteur("LOUNGE_BAR").size()+1)).toUpperCase());
        }else {
            place.setNumTable(("RS"+(placeRepository.findAllBySecteur("RESTAURANT").size()+1)).toUpperCase());
        }
        place.setStatus(false);
        place.setSecteur(secteur);
        placeRepository.save(place);
        redirectAttributes.addFlashAttribute("success", "You've been save your data successfully");
        return "redirect:/admin/places/"+secteur.toLowerCase()+"/all";
    }

    @GetMapping("/places/delete/{id}")
    public String deleteById(@PathVariable Long id, Model model) {
        Place place =  placeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid place id:" +id));
        System.out.println("place id: " + place.getSecteur());
        placeRepository.delete(place);
        model.addAttribute("places", placeRepository.findAll());
        return "redirect:/admin/places/all" ;
    }

    @GetMapping("/places/detail/{id}")
    public String getTask(Model model, @PathVariable Long id){
        Place place =  placeRepository.findById(id).get();
        model.addAttribute("place", place);
        return "admin/places/detail";

    }

    //------ Rapport methods ----//

    @GetMapping("/rapports/rapport")
    public String rapport( Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Rapport rapport = new Rapport();
        Date date1 = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Optional<Rapport> existRepport = rapportRepository.findByDate(dateFormat.format(date1));
        List<Commande> commandes = commandeRepository.findAllByDate(dateFormat.format(date1));

        if (existRepport.isPresent()){
            List<Article> lounges = new ArrayList<>();
            List<Article> restaurants = new ArrayList<>();
            List<Article> terrases = new ArrayList<>();
            List<Article> autres = new ArrayList<>();

            for (Commande commande : commandes){
                if (commande.getSecteur().equals("LOUNGE_BAR")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            lounges.add(article);
                        }
                    }
                }else if (commande.getSecteur().equals("TERASSE")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            terrases.add(article);
                        }
                    }
                }else if (commande.getSecteur().equals("RESTAURANT")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            restaurants.add(article);
                        }
                    }
                }else {
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            autres.add(article);
                        }
                    }
                }
            }


            existRepport.get().getNomLounges().clear();
            existRepport.get().getQuantitiesLounges().clear();
            existRepport.get().getMontantLounges().clear();
            existRepport.get().getNomRestaurants().clear();
            existRepport.get().getQuantitiesRestaurants().clear();
            existRepport.get().getMontantRestaurants().clear();
            existRepport.get().getNomTerrasses().clear();
            existRepport.get().getQuantitiesTerrasses().clear();
            existRepport.get().getMontantTerrasses().clear();
            existRepport.get().setRecette(0F);
            for(Article article : lounges){
                existRepport.get().getNomLounges().add(article.getNom());
                existRepport.get().getQuantitiesLounges().add(article.getQty());
                existRepport.get().getMontantLounges().add(article.getPrixT());
                if (existRepport.get().getRecette() != null) {
                    existRepport.get().setRecette(existRepport.get().getRecette() + article.getPrixT());
                }

            }


            for(Article article : restaurants){
                existRepport.get().getNomRestaurants().add(article.getNom());
                existRepport.get().getQuantitiesRestaurants().add(article.getQty());
                existRepport.get().getMontantRestaurants().add(article.getPrixT());
                if (existRepport.get().getRecette() != null) {
                    existRepport.get().setRecette(existRepport.get().getRecette() + article.getPrixT());
                }else {
                    existRepport.get().setRecette(article.getPrixT());
                }            }

            for(Article article : terrases){
                existRepport.get().getNomTerrasses().add(article.getNom());
                existRepport.get().getQuantitiesTerrasses().add(article.getQty());
                existRepport.get().getMontantTerrasses().add(article.getPrixT());
                if (existRepport.get().getRecette() != null) {
                    existRepport.get().setRecette(existRepport.get().getRecette() + article.getPrixT());
                }
            }

            for(Article article : autres){
                existRepport.get().getNomAutres().add(article.getNom());
                existRepport.get().getQuantitiesAutres().add(article.getQty());
                existRepport.get().getMontantAutres().add(article.getPrixT());
                if (existRepport.get().getRecette() != null) {
                    existRepport.get().setRecette(existRepport.get().getRecette() + article.getPrixT());
                }
            }

            existRepport.get().setCompte(compte);
            rapportRepository.save(existRepport.get());
            model.addAttribute("rapport",existRepport.get());


        }else {
            rapport.setTitle("Rapport Journalier ");
            rapport.setDate(dateFormat.format(date1));
            List<Article> lounges = new ArrayList<>();
            List<Article> restaurants = new ArrayList<>();
            List<Article> terrases = new ArrayList<>();
            List<Article> autres = new ArrayList<>();
            rapport.setSecteur1("LOUNGE_BAR");
            rapport.setSecteur2("RESTAURANT");
            rapport.setSecteur3("TERASSE");
            rapport.setSecteur4("AUTRES");


            for (Commande commande : commandes){
                if (commande.getSecteur().equals("LOUNGE_BAR")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            lounges.add(article);
                        }
                    }
                }else if (commande.getSecteur().equals("TERASSE")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            terrases.add(article);
                        }
                    }
                }else if (commande.getSecteur().equals("RESTAURANT")){
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            restaurants.add(article);
                        }
                    }
                }else {
                    for (Article article : commande.getArticles()){
                        if (article.getCommande().getId() == commande.getId()){
                            autres.add(article);
                        }
                    }
                }
            }

            for (Article article : lounges) {
                rapport.getNomLounges().add(article.getNom());
                rapport.getQuantitiesLounges().add(article.getQty());
                rapport.getMontantLounges().add(article.getPrixT());
                for (Float montant : rapport.getMontantLounges()){
                    rapport.setRecette(rapport.getRecette()+ montant);
                }
            }


            for (Article article : restaurants) {
                rapport.getNomRestaurants().add(article.getNom());
                rapport.getQuantitiesRestaurants().add(article.getQty());
                rapport.getMontantRestaurants().add(article.getPrixT());
                for (Float montant : rapport.getMontantRestaurants()){
                    rapport.setRecette(rapport.getRecette()+montant);
                }
            }

            for (Article article : terrases) {
                rapport.getNomTerrasses().add(article.getNom());
                rapport.getQuantitiesTerrasses().add(article.getQty());
                rapport.getMontantTerrasses().add(article.getPrixT());
                for (Float montant : rapport.getMontantTerrasses()){
                    rapport.setRecette(rapport.getRecette()+montant);
                }
            }

            for (Article article : autres) {

                rapport.getNomAutres().add(article.getNom());
                rapport.getQuantitiesAutres().add(article.getQty());
                rapport.getMontantAutres().add(article.getPrixT());
                for (Float montant : rapport.getMontantAutres()){
                    rapport.setRecette(rapport.getRecette()+montant);
                }
            }

            rapport.setCompte(compte);
            rapportRepository.save(rapport);
            model.addAttribute("rapport", rapport);
        }

        return "admin/rapports/detail";
    }




    static void addTableHeaderRapport(PdfPTable table) {
        Stream.of("Index", "Produit", "Quantite")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }


    //---------- reservation methods ------//
    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reservations/all")
    public String findAllReservations(Model model, HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        model.addAttribute("reservations", reservationRepository.findAll());
            return "admin/reservations/reservations";
    }


    @GetMapping("/reservations/form/{id}")
    public String formReservation(Model model, @PathVariable Long id){
        Chambre chambre = chambreRepository.getOne(id);
        model.addAttribute("chambre",chambre);
        model.addAttribute("reservation", new Reservation());
        return "admin/reservations/form";
    }

    @PostMapping("/reservations/save")
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
            return "redirect:/admin/reservations/detail/"+reservation.getId();
        }else {
            model.addAttribute("error", "There are no room with the provided number :"+num);
            return "admin/reservations/form";
        }

    }

    @GetMapping("/reservations/edit/{id}")
    public String updatedReservation(Model model, @PathVariable Long id){
        Reservation reservation =  reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "admin/reservations/edit";
    }

    @PostMapping("/reservations/update")
    public String update(@Valid Reservation reservation, String num, HttpServletRequest request, Model model){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        Chambre chambre = chambreRepository.findByNumero(num);
        reservation.setPrixT(reservation.getPrixU() * reservation.getNbreNuits());
        if (chambre != null){
            reservation.setChambre(chambre);
            reservation.setCompte(compte);
            reservationRepository.save(reservation);
            return "redirect:/admin/reservations/all";
        }else {
            model.addAttribute("reservation",reservationRepository.getOne(reservation.getId()));
            model.addAttribute("error", "There are no room with the provided number :"+num);
            return "admin/reservations/edit";
        }

    }

    @GetMapping("/reservations/delete/{id}")
    public String deleteReservation(@PathVariable Long id, Model model, HttpSession session) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reservation id:" +id));
        System.out.println("reservation id: " + reservation.getId());
        reservationRepository.delete(reservation);
        return "redirect:/admin/reservations/all" ;
    }

    @GetMapping("/reservations/detail/{id}")
    public String getReservation(Model model, @PathVariable Long id){
        Reservation reservation = reservationRepository.findById(id).get();
        model.addAttribute("reservation", reservation);
        return "admin/reservations/detail";

    }


    @GetMapping("/reservations/pdf/generate/{id}")
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
        model.addAttribute("reservation", reservation);
        return "admin/reservations/facture";
    }

    //-------- gestion stocks methods -------///

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private MJRepository mjRepository;

    @GetMapping("/stocks/all")
    public String findAllStock(Model model){

        model.addAttribute("aliments",stockRepository.findAllByCategorie("ALIMENTS"));
        model.addAttribute("boissons",stockRepository.findAllByCategorie("BOISSONS"));
        model.addAttribute("couverts",stockRepository.findAllByCategorie("COUVERTS"));
        model.addAttribute("lingeries",stockRepository.findAllByCategorie("LINGERIES"));
        model.addAttribute("autres",stockRepository.findAllByCategorie("AUTRES"));
        model.addAttribute("stocks", stockRepository.findAll());
        return "admin/stocks/all";
    }

    @GetMapping("/stocks/form/{category}")
    public String form(Model model, @PathVariable String category){
        model.addAttribute("stock", new Stock());
        model.addAttribute("category",category);
        return "admin/stocks/form";
    }

    @PostMapping("/stocks/save/{category}")
    public String save(Stock stock, RedirectAttributes redirectAttributes, HttpServletRequest request, @PathVariable String category){
        Principal principal = request.getUserPrincipal();
        Stock stock1 = stockRepository.findByNom(stock.getNom().toUpperCase());
        if (stock1 != null){
            stock1.setQty(stock1.getQty()+stock.getQty());
            stockRepository.save(stock1);
        }else {
            stock.setCompte(compteService.findByUsername(principal.getName()));
            stock.setType(stock.getType().toString());
            stock.setCategorie(category);
            stock.setNom(stock.getNom().toUpperCase());
            stockRepository.save(stock);
        }
        redirectAttributes.addFlashAttribute("success", "You've save your stock successfully");
        return "redirect:/admin/stocks/all";
    }

    @GetMapping("/stocks/detail/{id}")
    public String getStock(@PathVariable Long id, Model model) {
        Optional<Stock> stock = stockRepository.findById(id);
        model.addAttribute("stock", stock.get());
        return "adminstocks/detail";
    }

    @GetMapping("/stocks/edit/{id}")
    public String updateFormStock(Model model, @PathVariable Long id) {
        Stock stock = stockRepository.findById(id).get();
        model.addAttribute("stock", stock);
        return "admin/stocks/update";
    }

    @PostMapping("/stocks/update")
    public String update(Stock stock, RedirectAttributes redirectAttributes, HttpServletRequest request){

        Principal principal = request.getUserPrincipal();
        stock.setCompte(compteService.findByUsername(principal.getName()));
        stock.setType(stock.getType());
        stock.setCategorie(stock.getCategorie());
        stockRepository.save(stock);
        redirectAttributes.addFlashAttribute("success", "You've save your stock successfully");

        return "redirect:/admin/stocks/all";
    }

    @DeleteMapping("/stocks/delete/{id}")
    public String delete(Model model, @PathVariable Long id, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock id:" +id));
        stockRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "You've deleted your element successfully");
        return "redirect:/admin/stocks/all";
    }

    @GetMapping("/stocks/categories/{categorie}")
    public String findAllByCategorie(@PathVariable String categorie, Model model, RedirectAttributes redirectAttributes) {
        List<Stock> stocks = new ArrayList<>();
        stockRepository.findAllByCategorie(categorie).forEach(stocks :: add);
        model.addAttribute("aliments",stockRepository.findAllByCategorie("ALIMENTS"));
        model.addAttribute("boissons",stockRepository.findAllByCategorie("BOISSONS"));
        model.addAttribute("couverts",stockRepository.findAllByCategorie("COUVERTS"));
        model.addAttribute("lingeries",stockRepository.findAllByCategorie("LINGERIES"));
        model.addAttribute("autres",stockRepository.findAllByCategorie("AUTRES"));
        if (stocks.isEmpty()){
            model.addAttribute("error","Aucun element trouver dans la categorie :"+categorie);
        }
        model.addAttribute("category",categorie);
        model.addAttribute("stocks",stocks);
        return "admin/stocks/all";


    }

    @GetMapping("/stocks/mjs/{id}")
    public String mjs(@PathVariable Long id, Model model){
        List<MJ> mjs = mjRepository.findAllByStock_Id(id);
        model.addAttribute("add", new MJ());
        model.addAttribute("update", new MJ());
        model.addAttribute("stock", stockRepository.getOne(id));
        model.addAttribute("lists",mjs);

        return "admin/stocks/mjs";
    }

    @PostMapping("/stocks/mjs/save/{id}")
    public String saveMjs(@PathVariable Long id, MJ mj, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.getOne(id);
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat();
        mj.setDate(dateFormat.format(date));
        mj.setNom(stock.getNom());
        mj.setStock(stock);
        stock.setQty(mj.getQty()+stock.getQty());
        stockRepository.save(stock);
        mjRepository.save(mj);
        redirectAttributes.addFlashAttribute("success","you've data saved successfully");
        return "redirect:/admin/stocks/mjs/"+stock.getId();

    }
    @PostMapping("/stocks/mjs/update/{id}")
    public String Mjs(@PathVariable Long id, MJ mj, RedirectAttributes redirectAttributes){
        Stock stock = stockRepository.getOne(id);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat();
        mj.setDate(dateFormat.format(date));
        mj.setNom(stock.getNom());
        mj.setStock(stock);
        stock.setQty(stock.getQty()+mj.getQty());
        mjRepository.save(mj);
        redirectAttributes.addFlashAttribute("success","you've data saved successfully");
        return "redirect:/admin/stocks/mjs/"+stock.getId();

    }

    @Autowired
    private TauxRepository tauxRepository;

    @GetMapping("/taux")
    public String getAll(Model model){
        List<Taux> lists = tauxRepository.findAll();

        model.addAttribute("lists",lists);
        return "admin/taux";
    }

    @GetMapping("/taux/update/{id}")
    public String formTaux(Model model, @PathVariable Long id){
        Taux taux = tauxRepository.getOne(id);
        model.addAttribute("taux",taux);
        return "admin/tauxForm";
    }

    @PostMapping("/taux/save")
    public String saveTaux(Taux taux){
        tauxRepository.save(taux);
        return "redirect:/admin/taux";
    }

}
