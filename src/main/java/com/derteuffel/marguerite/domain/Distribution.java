package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Distribution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String secteur;
    private int qty;
    private Date date;

    @ManyToOne
    @JsonIgnoreProperties("distributions")
    private Compte compte;

    @ManyToOne
    @JsonIgnoreProperties("distributions")
    private Stock stock;


}
