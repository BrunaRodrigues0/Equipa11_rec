package com.upt.lp.gestao_despesas_api.model;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "utilizador", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Despesa> despesas = new ArrayList<>();

    public User() {}

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<Despesa> getDespesas() { return despesas; }
    public void setDespesas(List<Despesa> despesas) { this.despesas = despesas; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; } 
}