package com.derteuffel.marguerite.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "taux")
public class Taux {

    @Id
    @GeneratedValue
    private  Long id;

    private int taux;
}
