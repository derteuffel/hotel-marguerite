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
@Table(name = "place")
public class Place implements Serializable {


    @Id
    @GeneratedValue
    private Long id;

    private int numTable;
    private String secteur;
    private int nbrePlace;

    @OneToMany(mappedBy = "place")
    private Collection<Commande> commandes;
}
