package com.derteuffel.marguerite.domain;

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
public class Facture implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private ArrayList<String> articles = new ArrayList<>();
    private ArrayList<Float> prices = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();
    private String numeroTable;
    private String numCmd;
    private Double montantT;
    private Double montantVerse;
    private Double remboursement;
    private String date;
    private String billTrace;

    @OneToOne
    private Commande commande;
}
