package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Stock implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private String categorie;
    private String type;
    private int qty;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date date;
    @ManyToOne
    private Compte compte;
    @OneToMany(mappedBy = "stock")
    private Collection<MJ>mjs;

    @OneToMany(mappedBy = "stock")
    private Collection<Distribution> distributions;


}
