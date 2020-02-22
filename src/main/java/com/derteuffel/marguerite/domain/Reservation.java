package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Reservation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date_debut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timer;
    private int nbre_nuits;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date_fin;
    private Boolean status;
    private Float prixT;
    private Date dateJour = new Date();

    @ManyToOne
    @JsonIgnoreProperties("reservations")
    private Chambre chambre;
    @ManyToOne
    @JsonIgnoreProperties("reservations")
    private Compte compte;

}