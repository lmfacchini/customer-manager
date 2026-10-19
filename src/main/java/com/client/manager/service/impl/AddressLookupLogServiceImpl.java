package com.client.manager.service.impl;

import com.client.manager.domain.AddressLookupLog;
import com.client.manager.dto.AddressResponse;
import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.repository.AddressLookupLogRepository;
import com.client.manager.service.AddressLookupLogService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddressLookupLogServiceImpl implements AddressLookupLogService {

    private final AddressLookupLogRepository repository;

    @Override
    public void record(AddressLookupEvent event) {
        AddressResponse response = event.address();
        AddressLookupLog log = new AddressLookupLog();
        log.setEventId(event.eventId());
        log.setConsultedAt(event.consultedAt());
        log.setCep(event.cep());
        log.setSource(event.source());
        log.setLogradouro(response.logradouro());
        log.setComplemento(response.complemento());
        log.setUnidade(response.unidade());
        log.setBairro(response.bairro());
        log.setLocalidade(response.localidade());
        log.setUf(response.uf());
        log.setEstado(response.estado());
        log.setRegiao(response.regiao());
        log.setIbge(response.ibge());
        log.setGia(response.gia());
        log.setDdd(response.ddd());
        log.setSiafi(response.siafi());
        log.setErro(response.erro());
        repository.saveIfAbsent(log);
    }
}
