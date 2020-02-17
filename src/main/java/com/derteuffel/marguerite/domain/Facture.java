package com.derteuffel.marguerite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Facture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero_table;
    private Integer num_cmd;
    private Float montantT;
    private Float montantVerse;
    private Float remboursement;
    private Date date;
}
