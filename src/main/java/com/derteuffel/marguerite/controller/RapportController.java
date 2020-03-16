package com.derteuffel.marguerite.controller;

import com.derteuffel.marguerite.domain.Article;
import com.derteuffel.marguerite.domain.Rapport;
import com.derteuffel.marguerite.repository.ArticleRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/hotel/rapports")
public class RapportController {

    @Autowired
    private RapportRepository rapportRepository;

    @Autowired
    private ArticleRepository articleRepository;


    @Value("${file.upload-dir}")
    private String fileStorage;



    @GetMapping("/rapport")
    public String rapport( Model model){
        Rapport rapport = new Rapport();
        Date date1 = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        List<Article> articles = articleRepository.findAllByDate(dateFormat.format(date1));
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


        for (Article article : articles){
            if (article.getCommande().getSecteur().equals("LOUNGE_BAR")){
                lounges.add(article);
            } else if(article.getCommande().getSecteur().equals("TERASSE")){
                terrases.add(article);
            }else if (article.getCommande().getSecteur().equals("RESTAURANT")){
                restaurants.add(article);
            } else {
                autres.add(article);
            }
            rapport.setRecette(rapport.getRecette()+article.getPrixT());

        }

        for(Article article : lounges){
            rapport.getNomLounges().add(article.getNom());
            rapport.getQuantitiesLounges().add(article.getQty());
            rapport.getMontantLounges().add(article.getPrixT());

        }


        for(Article article : restaurants){
            rapport.getNomRestaurants().add(article.getNom());
            rapport.getQuantitiesRestaurants().add(article.getQty());
            rapport.getMontantRestaurants().add(article.getPrixT());
        }

        for(Article article : terrases){
            rapport.getNomTerrasses().add(article.getNom());
            rapport.getQuantitiesTerrasses().add(article.getQty());
            rapport.getMontantTerrasses().add(article.getPrixT());

        }

        for(Article article : autres){

            rapport.getNomAutres().add(article.getNom());
            rapport.getQuantitiesAutres().add(article.getQty());
            rapport.getMontantAutres().add(article.getPrixT());

        }


         rapportRepository.save(rapport);
        model.addAttribute("rapport",rapport);

        return "rapports/detail";
    }




    static void addTableHeader(PdfPTable table) {
        Stream.of("Index", "Denomination", "Quantite")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

}
