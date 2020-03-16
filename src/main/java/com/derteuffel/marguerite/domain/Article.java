package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Article implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String nom;
    private String date;
    private Float prixU;
    private Float prixT;
    private  int qty;
    private String type;
    @ManyToOne
    @JsonIgnoreProperties("articles")
    private Commande commande;
}
