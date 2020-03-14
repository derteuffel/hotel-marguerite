package com.derteuffel.marguerite.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

@Data
@Entity
@Table(name = "order")
public class Order implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String secteur;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();

    @ManyToOne
    private Commande commande;
}
