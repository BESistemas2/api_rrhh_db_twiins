package com.fabribat.apiNomina.entities.security;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "refApi")
public class RefApi {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idApi", updatable = false, nullable = false)
    private UUID idApi;

    @Column(name = "nomApi", nullable = false, unique = true, length = 100)
    private String nomApi;

    @Column(name = "desApi", length = 255)
    private String desApi;

    @Column(name = "estApi", length = 1)
    private String estApi = "A";

    public RefApi() {
    }

    public RefApi(String nomApi, String desApi) {
        this.nomApi = nomApi;
        this.desApi = desApi;
        this.estApi = "A";
    }

    // Getters y Setters
    public UUID getIdApi() {
        return idApi;
    }

    public void setIdApi(UUID idApi) {
        this.idApi = idApi;
    }

    public String getNomApi() {
        return nomApi;
    }

    public void setNomApi(String nomApi) {
        this.nomApi = nomApi;
    }

    public String getDesApi() {
        return desApi;
    }

    public void setDesApi(String desApi) {
        this.desApi = desApi;
    }

    public String getEstApi() {
        return estApi;
    }

    public void setEstApi(String estApi) {
        this.estApi = estApi;
    }
}