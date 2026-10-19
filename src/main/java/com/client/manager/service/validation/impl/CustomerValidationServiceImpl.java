package com.client.manager.service.validation.impl;

import com.client.manager.dto.CustomerWriteRequest;
import com.client.manager.exception.BusinessException;
import com.client.manager.repository.CustomerRepository;
import com.client.manager.service.validation.CustomerValidationService;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CustomerValidationServiceImpl implements CustomerValidationService {


    private final CustomerRepository customerRepository;

    @Override
    public void validate(CustomerWriteRequest request) {
        if(customerRepository.findByCpf(request.cpf()) != null){
            throw new BusinessException("B00000001", request.cpf());
        }

        if(customerRepository.findByEmail(request.email()) != null){
            throw new BusinessException("B00000002", request.email());
        }
    }


}
