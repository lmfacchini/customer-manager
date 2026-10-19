package com.client.manager.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "priority_addresses")
@Data
public class Address {

    @Id
    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "logradouro", nullable = false, length = 200)
    private String logradouro;

    @Column(name = "complemento", length = 200)
    private String complemento;

    @Column(name = "unidade", length = 100)
    private String unidade;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "localidade", nullable = false, length = 100)
    private String localidade;

    @Column(name = "uf", nullable = false, length = 2)
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
}
