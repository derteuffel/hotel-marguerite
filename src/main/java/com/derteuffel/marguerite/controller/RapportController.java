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



    @GetMapping("/rapport/pdf")
    public String rapport( String date, Model model){
        Rapport rapport = new Rapport();
        Date date1 = new Date();
        List<Article> articles = articleRepository.findAllByDate(date);
        rapport.setTitle("Rapport Journalier " +date1);
        List<Article> lounges = new ArrayList<>();
        List<Article> restaurants = new ArrayList<>();
        List<Article> terrases = new ArrayList<>();
        List<Article> autres = new ArrayList<>();


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

        }

        for(Article article : lounges){
            rapport.getNomLounge().add(article.getNom());
            rapport.getQuantitiesLounge().add(article.getQty());
            rapport.getMontantLounge().add(article.getPrixT());

        }


        for(Article article : restaurants){
            rapport.getNomRestaurant().add(article.getNom());
            rapport.getQuantitiesRestaurant().add(article.getQty());
            rapport.getMontantRestaurant().add(article.getPrixT());


        }

        for(Article article : terrases){
            rapport.getNomTerrasse().add(article.getNom());
            rapport.getQuantitiesTerrasse().add(article.getQty());
            rapport.getMontantTerrasse().add(article.getPrixT());

        }

        for(Article article : autres){

            rapport.getNomAutre().add(article.getNom());
            rapport.getQuantitiesAutre().add(article.getQty());
            rapport.getMontantAutre().add(article.getPrixT());

        }
         rapportRepository.save(rapport);

        return "rapports/rapport";
    }

    @GetMapping("/rapport/pdf/{id}")
    public String rapportPdfGenerator(@PathVariable Long id){
        Rapport rapport = rapportRepository.getOne(id);
        Document document = new Document();

        try{
            PdfWriter.getInstance(document,new FileOutputStream(new File((fileStorage+rapport.getId()+".pdf").toString())));
            document.open();
            document.add(new Paragraph("Marguerite Hotel"));
            document.add(new Paragraph("Secteur :   "+rapport));
            document.add(new Paragraph("Date du jour :  "+rapport.getDate()));
            document.add(new Paragraph("Listes des articles et quantites "));

            PdfPTable table = new PdfPTable(4);
            table.setSpacingBefore(20f);
            table.setSpacingAfter(20f);
            addTableHeader(table);
            int total=0;
            for (int i = 0; i<rapport.getArticles().size();i++){
                table.addCell(""+(i+1));
                table.addCell(""+rapport.getArticles().get(i)+" CDF");
                table.addCell(""+rapport+" CDF");
                table.addCell(""+rapport+" CDF");
                //total=+rapport.getQuantities().get(i);
                System.out.println("inside the table");
            }
            table.addCell("Total");
            table.addCell("");
            table.addCell(""+total);
            //table.addCell(""+rapport.getMontantT()+" CDF");

            document.add(table);
            document.close();
            System.out.println("the job is done!!!");

        } catch (FileNotFoundException | DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rapportRepository.save(rapport);
        return "";
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
