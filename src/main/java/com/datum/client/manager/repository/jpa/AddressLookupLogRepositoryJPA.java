package com.datum.client.manager.repository.jpa;

import com.datum.client.manager.domain.AddressLookupLog;
import com.datum.client.manager.repository.AddressLookupLogRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AddressLookupLogRepositoryJPA implements AddressLookupLogRepository {

    private final EntityManager entityManager;

    @Override
    public void saveIfAbsent(AddressLookupLog log) {
        entityManager.createNativeQuery("""
                INSERT INTO address_lookup_logs (
                    event_id, consulted_at, cep, source, logradouro, complemento,
                    unidade, bairro, localidade, uf, estado, regiao, ibge, gia,
                    ddd, siafi, erro
                ) VALUES (
                    :eventId, :consultedAt, :cep, :source, :logradouro,
                    :complemento, :unidade, :bairro, :localidade, :uf, :estado,
                    :regiao, :ibge, :gia, :ddd, :siafi, :erro
                )
                ON CONFLICT (event_id) DO NOTHING
                """)
                .setParameter("eventId", log.getEventId())
                .setParameter("consultedAt", log.getConsultedAt())
                .setParameter("cep", log.getCep())
                .setParameter("source", log.getSource())
                .setParameter("logradouro", log.getLogradouro())
                .setParameter("complemento", log.getComplemento())
                .setParameter("unidade", log.getUnidade())
                .setParameter("bairro", log.getBairro())
                .setParameter("localidade", log.getLocalidade())
                .setParameter("uf", log.getUf())
                .setParameter("estado", log.getEstado())
                .setParameter("regiao", log.getRegiao())
                .setParameter("ibge", log.getIbge())
                .setParameter("gia", log.getGia())
                .setParameter("ddd", log.getDdd())
                .setParameter("siafi", log.getSiafi())
                .setParameter("erro", log.getErro())
                .executeUpdate();
    }
}
