package it.chiara.vinylshop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    //relazione uno-a-molti
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserRole> roles = new ArrayList<>();


    @Column(nullable = true)
    private String nome;

    @Column(nullable = true)
    private String cognome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String telefono;

    @Column(nullable = true)
    private String indirizzo;

    @Column(nullable = true)
    @Temporal(TemporalType.DATE)
    private Date dataDiNascita;

    // Costruttore user
    public User(Long id, String username, String password, String nome, String cognome, String email, String telefono, String indirizzo, Date dataDiNascita) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
        this.indirizzo = indirizzo;
        this.dataDiNascita = dataDiNascita;
    }
}
