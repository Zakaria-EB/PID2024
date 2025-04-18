package com.reservations.reservations.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

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
    private LocalDateTime created_at;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "representation_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "representation_id")
    )
    private List<Representation> representations = new ArrayList<>();

    protected User() {}

    public User(String login, String firstname, String lastname) {
        this.login = login;
        this.firstname = firstname;
        this.lastname = lastname;
        this.created_at = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public User addRole(Role role) {
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            role.addUser(this);
        }
        return this;
    }

    public User removeRole(Role role) {
        if (this.roles.contains(role)) {
            this.roles.remove(role);
            role.getUsers().remove(this);
        }
        return this;
    }

    public List<Representation> getRepresentations() {
        return representations;
    }

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

    @Override
    public String toString() {
        return login + " (" + firstname + " " + lastname + ")";
    }
}