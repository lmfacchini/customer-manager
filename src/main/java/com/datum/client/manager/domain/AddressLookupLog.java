package com.datum.client.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "address_lookup_logs")
@Data
public class AddressLookupLog {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "consulted_at", nullable = false)
    private Instant consultedAt;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "source", nullable = false, length = 16)
    private String source;

    @Column(name = "logradouro", length = 200)
    private String logradouro;

    @Column(name = "complemento", length = 200)
    private String complemento;

    @Column(name = "unidade", length = 100)
    private String unidade;

    @Column(name = "bairro", length = 100)
    private String bairro;

    @Column(name = "localidade", length = 100)
    private String localidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @Column(name = "estado", length = 100)
    private String estado;

    @Column(name = "regiao", length = 100)
    private String regiao;

    @Column(name = "ibge", length = 20)
    private String ibge;

    @Column(name = "gia", length = 20)
    private String gia;

    @Column(name = "ddd", length = 5)
    private String ddd;

    @Column(name = "siafi", length = 20)
    private String siafi;

    @Column(name = "erro")
    private Boolean erro;
}
