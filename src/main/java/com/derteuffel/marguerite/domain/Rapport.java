package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Rapport implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String date;
    private String recette;
    private String resume;
    private String pdfTrace;
    private ArrayList<String> articles = new ArrayList<>();
    private String secteur1, secteur2, secteur3;

    private ArrayList<String> nomLounge =new ArrayList<>();
    private ArrayList<Float> montantLounge =new ArrayList<>();
    private ArrayList<Integer> quantitiesLounge =new ArrayList<>();

    private ArrayList<String> nomRestaurant =new ArrayList<>();
    private ArrayList<Float> montantRestaurant =new ArrayList<>();
    private ArrayList<Integer> quantitiesRestaurant =new ArrayList<>();

    private ArrayList<String> nomTerrasse =new ArrayList<>();
    private ArrayList<Float> montantTerrasse =new ArrayList<>();
    private ArrayList<Integer> quantitiesTerrasse =new ArrayList<>();

    private ArrayList<String> nomAutre =new ArrayList<>();
    private ArrayList<Float> montantAutre =new ArrayList<>();
    private ArrayList<Integer> quantitiesAutre =new ArrayList<>();
    private ArrayList<Float> amounts = new ArrayList<>();

    ArrayList<String> commandes = new ArrayList<>();
    @ManyToOne
    private Compte compte;

}
