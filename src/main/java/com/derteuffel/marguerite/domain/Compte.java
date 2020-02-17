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
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Compte implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Boolean status;
    private String avatar;

    @OneToOne
    private User user;
    @OneToMany(mappedBy = "compte")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Stock> stocks;

    @OneToMany(mappedBy = "compte")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Reservation> reservations;

    @OneToMany(mappedBy = "compte")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Rapport> rapports;

    @OneToMany(mappedBy = "compte")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Collection<Distribution> distributions;
}
