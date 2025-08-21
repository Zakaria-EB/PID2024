package com.reservations.reservations.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private String email;
    private String langue;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    // --- Rôle unique, stocké en base comme texte ("ADMIN" / "MEMBER")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    // (Tu peux garder cette relation si elle existe déjà ailleurs,
    // elle n'est pas utilisée pour l'authentification)
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "representation_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "representation_id")
    )
    private List<Representation> representations = new ArrayList<>();

    public User() {}

    public User(String login, String firstname, String lastname) {
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.created_at = LocalDateTime.now();
    }

    // -------- Getters / Setters de base --------
    public Long getId() { return id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    // -------- Rôle (enum) --------
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public boolean hasRole(UserRole expected) {
        return this.role == expected;
    }

    // -------- Divers --------
    public List<Role> getRoles() { return roles; }

    public List<Representation> getRepresentations() { return representations; }

    public User addRepresentation(Representation representation) {
        if (!this.representations.contains(representation)) {
            this.representations.add(representation);
            representation.getUsers().add(this);
        }
        return this;
    }

    public User removeRepresentation(Representation representation) {
        if (this.representations.contains(representation)) {
            this.representations.remove(representation);
            representation.getUsers().remove(this);
        }
        return this;
    }

    public String getUsername() { return this.login; }

    @Override
    public String toString() {
        return login + " (" + firstname + " " + lastname + ") - role=" + role;
    }

    public void addRole(String role) {
    role = "user";
    }
}
