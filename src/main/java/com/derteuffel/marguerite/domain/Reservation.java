package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Timer;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@OnDelete(action= OnDeleteAction.NO_ACTION)
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Chambre chambre;
    @ManyToOne(fetch = FetchType.LAZY)
    private Compte compte;


}
