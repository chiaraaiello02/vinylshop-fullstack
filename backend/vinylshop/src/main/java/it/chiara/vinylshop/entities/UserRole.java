package it.chiara.vinylshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USER_ROLES",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_name"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relazione molti-a-uno verso User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", length = 20, nullable = false)
    private RoleName roleName;

    // costruttore utile per assegnare ruolo ad uno user
    public UserRole(User user, RoleName roleName) {
        this.user = user;
        this.roleName = roleName;
    }
}