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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String categorie;
    private String type;
    private Float prixU;
    private Float prixT;
    private  Integer qty;
    @ManyToOne
    @JsonIgnoreProperties("articles")
    private Commande commande;
}
