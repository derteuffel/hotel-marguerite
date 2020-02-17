package com.derteuffel.marguerite.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Table implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numTable;
    private String secteur;
    private Integer nbre_place;

    @OneToMany(mappedBy = "table")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Commande> commandes;
}
