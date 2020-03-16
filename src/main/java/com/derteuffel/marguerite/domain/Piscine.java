package com.derteuffel.marguerite.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "piscine")
public class Piscine implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String nomClient;
    private Double prixU;
    private Double prixT;
    private int nbreHeure;
    private String date;
}
