package com.derteuffel.marguerite.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(name = "place")
@OnDelete(action= OnDeleteAction.NO_ACTION)
public class Place implements Serializable {


    @Id
    @GeneratedValue
    private Long id;

    private String numTable;
    private String secteur;
    private Boolean status;

    @OneToMany(mappedBy = "place")
    private Collection<Commande> commandes;
}
