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
    private ArrayList<Integer> prices = new ArrayList<>();
    private int numeroTable;
    private int numCmd;
    private Float montantT;
    private Float montantVerse;
    private Float remboursement;
    private String date;

    @OneToOne
    private Commande commande;
}
