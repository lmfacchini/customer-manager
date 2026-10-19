package com.client.manager.service.impl;

import com.client.manager.client.AddressApiClient;
import com.client.manager.domain.Address;
import com.client.manager.dto.AddressResponse;
import com.client.manager.dto.PrioritizedAddressRequest;
import com.client.manager.exception.BusinessException;
import com.client.manager.exception.NotFoundException;
import com.client.manager.mq.event.AddressLookupEvent;
import com.client.manager.mq.publisher.AddressLookupPublisher;
import com.client.manager.repository.AddressRepository;
import com.client.manager.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;
    private final AddressApiClient addressApiClient;
    private final AddressLookupPublisher eventPublisher;

    @Override
    public AddressResponse findByCep(String cep) {
        String normalizedCep = normalizeCep(cep);
        AddressResponse response;
        String source;

        var priorityAddress = repository.findByCep(normalizedCep);
        if (priorityAddress.isPresent()) {
            response = toResponse(priorityAddress.get());
            source = "INTERNAL";
        } else {
            response = addressApiClient.findByCep(normalizedCep);
            source = "EXTERNAL";
        }

        eventPublisher.publish(new AddressLookupEvent(normalizedCep, source, response));
        if (Boolean.TRUE.equals(response.erro())) {
            throw new NotFoundException();
        }
        return response;
    }

    @Override
    public AddressResponse savePrioritized(PrioritizedAddressRequest request) {
        String cep = normalizeCep(request.cep());
        Address address = repository.findByCep(cep).orElseGet(Address::new);
        address.setCep(cep);
        address.setLogradouro(clean(request.logradouro()));
        address.setComplemento(clean(request.complemento()));
        address.setUnidade(clean(request.unidade()));
        address.setBairro(clean(request.bairro()));
        address.setLocalidade(clean(request.localidade()));
        address.setUf(clean(request.uf()).toUpperCase());
        address.setEstado(clean(request.estado()));
        address.setRegiao(clean(request.regiao()));
        address.setIbge(clean(request.ibge()));
        address.setGia(clean(request.gia()));
        address.setDdd(clean(request.ddd()));
        address.setSiafi(clean(request.siafi()));
        return toResponse(repository.save(address));
    }

    @Override
    public void deletePrioritized(String cep) {
        repository.deleteByCep(normalizeCep(cep));
    }

    private static String normalizeCep(String cep) {
        if (!StringUtils.hasText(cep)) {
            throw new BusinessException("B00000003");
        }
        String normalized = cep.strip();
        if (normalized.matches("\\d{5}-\\d{3}")) {
            normalized = normalized.replace("-", "");
        }
        if (!normalized.matches("\\d{8}")) {
            throw new BusinessException("B00000003");
        }
        return normalized;
    }

    private static String clean(String value) {
        return value == null ? null : value.strip();
    }

    private static AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getCep(),
                address.getLogradouro(),
                address.getComplemento(),
                address.getUnidade(),
                address.getBairro(),
                address.getLocalidade(),
                address.getUf(),
                address.getEstado(),
                address.getRegiao(),
                address.getIbge(),
                address.getGia(),
                address.getDdd(),
                address.getSiafi(),
                false
        );
    }
}
