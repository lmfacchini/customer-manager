package com.client.manager.service.impl;

import com.client.manager.client.ScoresApiClient;
import com.client.manager.constants.ClassificationType;
import com.client.manager.dto.*;
import com.client.manager.dto.*;
import com.client.manager.exception.NotFoundException;
import com.client.manager.mapper.CustomerMapper;
import com.client.manager.mq.event.CustomerCreatedEvent;
import com.client.manager.mq.publisher.CustomerCreatedPublisher;
import com.client.manager.repository.CustomerRepository;
import com.client.manager.service.CustomerService;
import com.client.manager.service.validation.CustomerValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper mapper;

    private final CustomerValidationService customerValidationService;

    private final CustomerRepository customerRepository;

    private final CustomerCreatedPublisher eventPublisher;

    private final ScoresApiClient scoresApiClient;


    @Override
    public Long create(CreateCustomerRequest request) {
        customerValidationService.validate(request);
        var entity = mapper.toClient(request);
        entity.setCpf(entity.getCpf().strip());
        entity.setEmail(entity.getEmail().strip().toLowerCase());
        entity = customerRepository.save(entity);
        CustomerCreatedEvent event =
                new CustomerCreatedEvent(entity.getId());

        eventPublisher.publish(event);
        return entity.getId();
    }

    @Override
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        var optional = customerRepository.findById(id);
        var entity = optional.orElseThrow(NotFoundException::new);
        customerValidationService.validate(request);
        if(StringUtils.hasText(request.name())) entity.setName(request.name());
        if(StringUtils.hasText(request.cpf()))entity.setCpf(request.cpf().strip());
        if(request.status() != null)entity.setStatus(request.status());
        if(StringUtils.hasText(request.email()))entity.setEmail(request.email().strip().toLowerCase());
        entity = customerRepository.save(entity);
        return mapper.toResponse(entity);
    }

    @Override
    public Pagenable<CustomerResponse> filter(CustomerFilter filter) {
        var result = customerRepository.filter(filter);
        return Pagenable.<CustomerResponse>builder().result(result.result().stream().map(mapper::toResponse).toList()).hasNext(result.hasNext()).build();
    }

    @Override
    public CustomerResponse findById(Long id) {
        var optional = customerRepository.findById(id);
        return mapper.toResponse(optional.orElseThrow(NotFoundException::new));
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerScoreResponse score(Long id) {
        var optional = customerRepository.findById(id);
        var customer = optional.orElseThrow(NotFoundException::new);

        int score = scoresApiClient.score(customer.getCpf());
        ClassificationType classification;
        if(score < 500){
            classification = ClassificationType.HIGH_RISK;
        } else if(score < 750){
            classification = ClassificationType.MEDIUM_RISK;
        }else{
            classification = ClassificationType.LOW_RISK;
        }
        return CustomerScoreResponse.builder().cpf(customer.getCpf()).score(score).classification(classification).build();
    }


}
