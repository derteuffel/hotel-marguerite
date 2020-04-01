package com.derteuffel.marguerite.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Appartement implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private Float prix;
    private Long nombreDePiece;
    private Boolean status;
    private String adresse;
    private String ville;
    private String description;
    private String image;


    @OneToMany(mappedBy = "appartement")
    private Collection<Reservation>reservations;
}
