package com.client.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PrioritizedAddressRequest(
        @NotBlank
        @Pattern(regexp = "^(\\d{8}|\\d{5}-\\d{3})$")
        String cep,
        @NotBlank @Size(max = 200) String logradouro,
        @Size(max = 200) String complemento,
        @Size(max = 100) String unidade,
        @NotBlank @Size(max = 100) String bairro,
        @NotBlank @Size(max = 100) String localidade,
        @NotBlank @Pattern(regexp = "^[A-Za-z]{2}$") String uf,
        @Size(max = 100) String estado,
        @Size(max = 100) String regiao,
        @Size(max = 20) String ibge,
        @Size(max = 20) String gia,
        @Size(max = 5) String ddd,
        @Size(max = 20) String siafi
) {
}
