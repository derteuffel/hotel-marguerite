package com.derteuffel.marguerite.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Rapport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String recette;
    private String resume;
    @ManyToOne
    @JsonIgnoreProperties("raports")
    private Compte compte;
}
