package com.derteuffel.marguerite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String quartier;
    private String fonction;
    private String avatar;
}
