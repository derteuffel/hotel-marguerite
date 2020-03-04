package com.derteuffel.marguerite.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Compte implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean status;
    private String avatar;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "compte")
    private Collection<Reservation> reservations;

    @OneToMany(mappedBy = "compte")
    private Collection<Rapport> rapports;

    @OneToMany(mappedBy = "compte")
    private Collection<Distribution> distributions;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "comptes_roles",
            joinColumns = @JoinColumn(
                    name = "compte_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
}
