package com.derteuffel.marguerite.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class gitAppartement implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String localisation;
    private String nom;
    private Double dimension;
    private Long nombreDePiece;
    private String typeDeBien;
    private Boolean status;
    private String adresse;
    private String ville;
    private String description;
    private String image;
}
