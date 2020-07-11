package com.derteuffel.marguerite.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "bon")
public class Bon implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String secteur;
    private String numTable;
    private String pdfTrace;
    private String numBon;
    private String date;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();

    @ManyToOne
    private Commande commande;
}
