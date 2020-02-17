package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Commande implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer numero;
    @NotNull
    private Float montantT;
    private Integer numTable;

    @ManyToOne
    @JsonIgnoreProperties("commandes")
    private Chambre chambre;
    @ManyToOne
    @JsonIgnoreProperties("commandes")
    private Table table;

    @OneToMany(mappedBy = "commande")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Article> articles;
    @OneToOne
    private Facture facture;
}
