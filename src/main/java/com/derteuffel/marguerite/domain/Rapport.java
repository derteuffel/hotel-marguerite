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
    private String secteur1, secteur2, secteur3,secteur4;

    private ArrayList<String> nomLounges =new ArrayList<>();
    private ArrayList<Float> montantLounges =new ArrayList<>();
    private ArrayList<Integer> quantitiesLounges =new ArrayList<>();

    private ArrayList<String> nomRestaurants =new ArrayList<>();
    private ArrayList<Float> montantRestaurants =new ArrayList<>();
    private ArrayList<Integer> quantitiesRestaurants =new ArrayList<>();

    private ArrayList<String> nomTerrasses =new ArrayList<>();
    private ArrayList<Float> montantTerrasses =new ArrayList<>();
    private ArrayList<Integer> quantitiesTerrasses =new ArrayList<>();

    private ArrayList<String> nomAutres =new ArrayList<>();
    private ArrayList<Float> montantAutres =new ArrayList<>();
    private ArrayList<Integer> quantitiesAutres =new ArrayList<>();

    @ManyToOne
    private Compte compte;

}
