package com.derteuffel.marguerite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Chambre implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String numero;
    @NotNull
    private String categorie;
    @NotNull
    private Float prix;
    private Boolean status;
    private String localisation;
    @OneToMany(mappedBy = "chambre")
    private Collection<Reservation>reservations;
    @OneToMany(mappedBy = "chambre")
    private Collection<Commande> commandes;
}
