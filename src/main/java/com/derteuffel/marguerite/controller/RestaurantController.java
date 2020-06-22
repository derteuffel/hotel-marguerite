package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.*;
import com.derteuffel.marguerite.enums.ESecteur;
import com.derteuffel.marguerite.helpers.CompteRegistrationDto;
import com.derteuffel.marguerite.repository.*;
import com.derteuffel.marguerite.services.CompteService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by user on 31/03/2020.
 */
@Controller
@RequestMapping("/restaurants")
public class RestaurantController {
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private ChambreRepository chambreRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private  TauxRepository tauxRepository;
    @Autowired
    private CompteService compteService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private FactureRepository factureRepository;

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
        return "restaurants/registration";
    }
    @GetMapping("/access-denied")
    public String access_denied(){
        return "restaurants/access-denied";
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
            return "restaurants/registration";
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
        return "redirect:/restaurants/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "restaurants/login";
    }

    //---------- Articles methodes -------//
    @GetMapping("/articles/all")
    public String findAll(Model model){
        model.addAttribute("articles", articleRepository.findAll());

        return "restaurants/articles/all";
    }

    @GetMapping("/articles/form/{id}")
    public String form(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "restaurants/articles/formArticle";
    }

    @GetMapping("/articles/form/boisson/{id}")
    public String formBoisson(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "restaurants/commandes/add-boisson";
    }
    @GetMapping("/articles/form/aliment/{id}")
    public String formAliment(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "restaurants/commandes/add-aliment";
    }

    @PostMapping("/articles/save/{type}/{id}")
    public String save(Article article, @PathVariable Long id, RedirectAttributes redirectAttributes, @PathVariable String type) {
        Commande commande = commandeRepository.getOne(id);
        Taux taux = tauxRepository.findFirstByOrderByIdDesc();
        if (article.getPrixU().toString().length()<5){
            System.out.println("je suis la");
            article.setPrixU(article.getPrixU() * taux.getTaux());
        }else {
            article.setPrixU(article.getPrixU());
        }
        article.setPrixT(article.getPrixU() * article.getQty());
        System.out.println(commande.getMontantT());
        commande.setMontantT(commande.getMontantT() + article.getPrixT());
        article.setCommande(commande);
        article.setDate(commande.getDate());
        article.setType(type.toUpperCase());
        articleRepository.save(article);
        commandeRepository.save(commande);
        redirectAttributes.addFlashAttribute("success","You've added successfully your article");
        return "redirect:/restaurants/commandes/detail/"+commande.getId();
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

        return "redirect:/restaurants/commandes/detail/"+commande.getId();
    }

    @GetMapping("/articles/remove/{id}")
    public String removeById(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Article article = articleRepository.getOne(id);
        article.setQty(article.getQty() - 1);
        article.setPrixT(article.getQty() * article.getPrixU());
        Commande commande = commandeRepository.getOne(article.getCommande().getId());
        commande.setMontantT(commande.getMontantT() - article.getPrixU());
        commandeRepository.save(commande);
        articleRepository.save(article);

        return "redirect:/restaurants/commandes/detail/"+commande.getId();
    }


    @GetMapping("/articles/orders/{id}")
    public String orders(@PathVariable Long id,Model model){
        Commande commande = commandeRepository.getOne(id);
        Optional<Bon> existDrinks = orderRepository.findBySecteurAndCommande_Id("DRINK",id);
        Optional<Bon> existFoods = orderRepository.findBySecteurAndCommande_Id("FOOD",id);

        model.addAttribute("commande",commande);
        /*if (existDrinks.isPresent()){
            model.addAttribute("drinks",existDrinks.get());
        }else {*/
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
        //}

        /*if (existFoods.isPresent()){
            model.addAttribute("foods",existFoods.get());
        }else {*/
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
        //}


        return "restaurants/commandes/orders";

    }

    @GetMapping("/articles/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Article article = articleRepository.getOne(id);
        model.addAttribute("article", article);
        model.addAttribute("commandes", commandeRepository.findAll());
        return "restaurants/commandes/edit";
    }



    @GetMapping("/articles/delete/{id}")
    public String deleteArticle(Model model, @PathVariable Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article id:" +id));
        articleRepository.deleteById(id);
        model.addAttribute("articles", articleRepository.findAll());
        return "redirect:/restaurants/articles/all";
    }

    @GetMapping("/articles/orders/pdf/{id}")
    public String pdfGenerator(@PathVariable Long id){
        Bon bon = orderRepository.getOne(id);
        Document document = new Document(PageSize.NOTE, 10, 10, 10, 10);
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+bon.getSecteur().toLowerCase()+"_"+bon.getId()+".pdf").toString())));
            document.open();
            Paragraph para1 = new Paragraph("HÔTEL MARGUERITE");
            para1.setAlignment(Paragraph.ALIGN_CENTER);
            para1.setFont(new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD,
                    BaseColor.GREEN));
            para1.setSpacingAfter(10);
            document.add(para1);

            Paragraph paragraph = new Paragraph("Ident. Nat.: 5-714-K 21286                                                                       N.R.C: 13680 KIN\n" +
                    "Adresse: N°62, Av. Kabinda, Q/Boom,   C/Kinshasa, Réf. : Croisement Av. Kabinda et Av. Bokassa\n" +
                    "Tél : +243 999950570, +243 998386650, +243 816896454, e-mail : margueritehotel@yahoo.fr\n");
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);
            paragraph.setFont(new Font(Font.FontFamily.TIMES_ROMAN,6,Font.BOLD));
            document.add(paragraph);
            Paragraph line = new Paragraph("----------------------------------------------------------------");
            line.setAlignment(Element.ALIGN_CENTER);
            document.add(line);
            document.add(new Paragraph("Secteur :   "+bon.getCommande().getSecteur()));
            document.add(new Paragraph("Numero Table :  "+bon.getCommande().getNumTable()));
            document.add(new Paragraph("Numero du bon :  "+bon.getNumBon()));
            document.add(new Paragraph("Listes des articles et quantites "));

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100.0f);
            table.setWidths(new float[] {3.0f, 2.0f, 2.0f});
            table.setSpacingBefore(10);
            table.setSpacingAfter(20f);
            table.setSpacingBefore(20f);
            // define font for table header row
            Font font = FontFactory.getFont(FontFactory.HELVETICA);
            font.setColor(BaseColor.WHITE);
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

        bon.setPdfTrace("/downloadFile/"+bon.getSecteur().toLowerCase()+"_"+bon.getId()+".pdf");
        orderRepository.save(bon);

        return "redirect:/restaurants/articles/orders/"+bon.getCommande().getId();
    }

    @GetMapping("/articles/orders/print/{id}")
    public String buildPdf(@PathVariable Long id){
        return "redirect:"+orderRepository.getOne(id).getPdfTrace();
    }


    private void addTableHeaderArticle(PdfPTable table) {
        RapportController.addTableHeader(table);
    }



    @GetMapping("/commandes/orders")
    public String restaurant(Model model){
        model.addAttribute("secteur", ESecteur.RESTAURANT.toString());
        List<Commande> commandes = commandeRepository.findAllBySecteurAndStatus(ESecteur.RESTAURANT.toString(),true);
        model.addAttribute("commandes", commandes);
        return "restaurants/commandes/all-2";
    }

    @GetMapping("/commandes/save/{id}")
    public String save(Model model, HttpServletRequest request, @PathVariable Long id) {
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

        return "redirect:/restaurants/commandes/detail/"+commande.getId();
    }

    @GetMapping("/places/orders")
    public String place(Model model,HttpServletRequest request){
        Principal principal = request.getUserPrincipal();
        Compte compte = compteService.findByUsername(principal.getName());
        request.getSession().setAttribute("compte",compte);
        model.addAttribute("secteur",ESecteur.RESTAURANT.toString());
        model.addAttribute("lists", placeRepository.findAllBySecteur(ESecteur.RESTAURANT.toString()));
        return "restaurants/places/all-2";
    }

    @GetMapping("/commandes/detail/{id}")
    public String findById(@PathVariable Long id, Model model){
        Commande commande = commandeRepository.getOne(id);
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commande);
        return "restaurants/commandes/detail";
    }



    @GetMapping("/commandes/rembourse/{id}")
    public String rembourse(@PathVariable Long id, String verser){
        Commande commande = commandeRepository.getOne(id);
        commande.setRembourse(Double.parseDouble(verser) - commande.getMontantT());
        commande.setMontantV(Double.parseDouble(verser));
        commandeRepository.save(commande);
        return "redirect:/restaurants/commandes/detail/"+commande.getId();
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
            System.out.println(commande.getMontantV());
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
        return "restaurants/commandes/facture";

    }

    @GetMapping("/billViewer/{id}")
    public String viewBillPdf(@PathVariable Long id, Model model){
        Facture facture = factureRepository.getOne(id);
        model.addAttribute("item",facture);
        return "restaurants/pdfViewer";
    }

    @GetMapping("/bon/{id}")
    public String viewBonPdf(@PathVariable Long id, Model model){
        Bon bon = orderRepository.getOne(id);
        model.addAttribute("item",bon);
        return "restaurants/bonViewer";
    }

    @GetMapping("/commandes/bills/{id}")
    public String billPdfGenerator(@PathVariable Long id, Model model){
        Facture facture = factureRepository.getOne(id);
        Document document = new Document(PageSize.A6, 10, 10, 10, 10);
        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+facture.getNumCmd()+facture.getId()+".pdf").toString())));
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

            document.add(table);

            /*
            Paragraph para6 = new Paragraph("Montant verse : "+facture.getMontantVerse());
            para6.setAlignment(Paragraph.ALIGN_RIGHT);
            para6.setSpacingAfter(3);
            document.add(para6);

            Paragraph para7 = new Paragraph("Montant rembourssé : "+facture.getRemboursement());
            para7.setAlignment(Paragraph.ALIGN_RIGHT);
            para7.setSpacingAfter(3);
            document.add(para7);*/

            Paragraph para8 = new Paragraph("Montant total à payer : "+facture.getMontantT());
            para8.setAlignment(Paragraph.ALIGN_LEFT);
            para8.setSpacingAfter(6);
            document.add(para8);

            document.add(new Paragraph("Merci d'etre passe chez nous "));
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
        return "restaurants/commandes/facture";
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


    @GetMapping("/commandes/{numTable}/{status}")
    public String findByStatusAnNumTable(@PathVariable String numTable, @PathVariable Boolean status, Model model){
        Commande commande = commandeRepository.findByNumTableAndStatus(numTable,status);
        model.addAttribute("article", new Article());
        model.addAttribute("commande",commande);
        return "restaurants/commandes/detail";
    }

}
