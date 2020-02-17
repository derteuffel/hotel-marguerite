package com.derteuffel.marguerite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Chambre implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer numero;
    @NotNull
    private String categorie;
    @NotNull
    private Float prix;
    @NotNull
    private Integer nbre_place;
    @NotNull
    private Integer nbre_lit;
    private Integer nbre_piece;
    private Boolean status;
    @OneToMany(mappedBy = "chambre")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Reservation>reservations;
    @OneToMany(mappedBy = "chambre")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Commande> commandes;
}
