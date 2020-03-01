package com.derteuffel.marguerite.domain;

import lombok.Data;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Collection;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    private String name;


}
