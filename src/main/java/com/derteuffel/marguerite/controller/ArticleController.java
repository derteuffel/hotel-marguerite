package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Article;
import com.derteuffel.marguerite.domain.Commande;
import com.derteuffel.marguerite.domain.Bon;
import com.derteuffel.marguerite.domain.Rapport;
import com.derteuffel.marguerite.repository.ArticleRepository;
import com.derteuffel.marguerite.repository.CommandeRepository;
import com.derteuffel.marguerite.repository.OrderRepository;
import com.derteuffel.marguerite.repository.RapportRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Controller
@RequestMapping("/hotel/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RapportRepository rapportRepository;

    @Value("${file.upload-dir}")
    private String fileStorage;

    @GetMapping("/all")
    public String findAll(Model model){
        model.addAttribute("articles", commandeRepository.findAll());

        return "articles/articlesList";
    }

    @GetMapping("/form/{id}")
    public String form(Model model, @PathVariable Long id){
        model.addAttribute("article", new Article());
        model.addAttribute("commande", commandeRepository.getOne(id));
        return "articles/formArticle";
    }

    @PostMapping("/save/{type}/{id}")
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
        return "redirect:/hotel/commandes/detail/"+commande.getId();
    }

    @GetMapping("/add/{id}")
    public String findById(@PathVariable Long id, RedirectAttributes redirectAttributes){
        Article article = articleRepository.getOne(id);
        article.setQty(article.getQty() + 1);
        article.setPrixT(article.getQty() * article.getPrixU());
        Commande commande = commandeRepository.getOne(article.getCommande().getId());
        commande.setMontantT(commande.getMontantT() + article.getPrixU());
        commandeRepository.save(commande);
        articleRepository.save(article);

        return "redirect:/hotel/commandes/detail/"+commande.getId();
    }

    public String rapportPdfGenerator(@PathVariable Long id, Model model){
        Rapport rapport = rapportRepository.getOne(id);


        return "";
    }

    @GetMapping("/orders/{id}")
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


        return "commandes/orders";

    }

    @GetMapping("/edit/{id}")
    public String updateForm(Model model, @PathVariable Long id) {
        Article article = articleRepository.getOne(id);
        model.addAttribute("article", article);
        model.addAttribute("commandes", commandeRepository.findAll());
        return "commandes/edit";
    }

    @PostMapping("/update/{id}")
    public String update(Article article , @PathVariable Long id){
        Optional<Article> article1 = articleRepository.findById(id);

        if (article1.isPresent()){
            Article article2 = article1.get();
            article2.setNom(article.getNom());
            article2.setPrixT(article.getPrixT());
            article2.setPrixU(article.getPrixU());
            article2.setQty(article.getQty());
            article2.setCommande(article.getCommande());
            articleRepository.save(article2);
            return "redirect:/hotel/articles/all";
        }else {
            return "redirect:/hotel/articles/form";
        }

    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Long id){
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid article id:" +id));
        articleRepository.deleteById(id);
        model.addAttribute("articles", articleRepository.findAll());
        return "redirect:/hotel/articles/all";
    }

    @GetMapping("/orders/pdf/{id}")
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

        return "redirect:/hotel/articles/orders/"+bon.getCommande().getId();
    }

    @GetMapping("/orders/print/{id}")
    public String buildPdf(@PathVariable Long id){
        return "redirect:"+orderRepository.getOne(id).getPdfTrace();
    }


    private void addTableHeader(PdfPTable table) {
        RapportController.addTableHeader(table);
    }
}
