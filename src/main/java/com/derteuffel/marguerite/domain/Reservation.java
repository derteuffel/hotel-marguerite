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
    @GeneratedValue
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateDebut;
    private String nomClient;
    private String telephone;
    private String email;
    private int nbreNuits;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateFin;
    private Boolean status;
    private String numReservation;
    private String billTrace;
    private Double prixU;
    private Double prixT;
    private Date dateJour = new Date();

    @ManyToOne
    private Chambre chambre;
    @ManyToOne
    private Compte compte;
    @ManyToOne
    private Appartement Appartement;

}
