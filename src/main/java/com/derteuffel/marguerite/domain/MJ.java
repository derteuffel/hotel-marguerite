package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class MJ implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private int qty;
    private String date;
    private String commentaire;

    @ManyToOne
    private Stock stock;
}
