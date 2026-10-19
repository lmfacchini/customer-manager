package com.client.manager.config;

import com.client.manager.client.ScoresApiClient;
import com.client.manager.client.impl.ScoresApiClientImpl;
import com.client.manager.client.AddressApiClient;
import com.client.manager.client.impl.ViaCepAddressApiClient;
import com.client.manager.service.AddressService;
import com.client.manager.service.AddressLookupLogService;
import com.client.manager.service.impl.AddressServiceImpl;
import com.client.manager.service.impl.AddressLookupLogServiceImpl;
import com.client.manager.service.transactional.AddressServiceTransactional;
import com.client.manager.service.transactional.AddressLookupLogServiceTransactional;
import com.client.manager.repository.AddressRepository;
import com.client.manager.repository.AddressLookupLogRepository;
import com.client.manager.mq.publisher.AddressLookupPublisher;
import com.client.manager.mapper.CustomerMapper;
import com.client.manager.mq.publisher.CustomerCreatedPublisher;
import com.client.manager.repository.CustomerRepository;
import com.client.manager.repository.ProcessedMessageRepository;
import com.client.manager.service.CustomerService;
import com.client.manager.service.ProcessedMessageService;
import com.client.manager.service.impl.CustomerServiceImpl;
import com.client.manager.service.impl.ProcessedMessageServiceImpl;
import com.client.manager.service.transactional.CustomerServiceTransactional;
import com.client.manager.service.transactional.ProcessedMessageServiceTransactional;
import com.client.manager.service.validation.CustomerValidationService;
import com.client.manager.service.validation.impl.CustomerValidationServiceImpl;
import com.customer.adapter.output.score.generated.api.ScoresApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BeansConfiguration {


    @Bean
    public TransactionOperations transactionOperations(
            PlatformTransactionManager transactionManager
    ) {
        TransactionTemplate template =
                new TransactionTemplate(transactionManager);

        template.setPropagationBehavior(
                TransactionDefinition.PROPAGATION_REQUIRED
        );

        return template;
    }

    @Bean
    public CustomerService customerService(
            CustomerMapper mapper,
            CustomerValidationService customerValidationService,
            CustomerRepository customerRepository,
            TransactionOperations transactionOperations,
            CustomerCreatedPublisher eventPublisher,
            ScoresApiClient scoresApiClient
    ){
        return new CustomerServiceTransactional(new CustomerServiceImpl(mapper,customerValidationService,customerRepository, eventPublisher, scoresApiClient),transactionOperations);
    }

    @Bean
    public CustomerValidationService customerValidationService(CustomerRepository customerRepository){
        return new CustomerValidationServiceImpl(customerRepository);
    }

    @Bean
    public ProcessedMessageService processedMessageService(ProcessedMessageRepository repository, TransactionOperations transactionOperations){
        return new ProcessedMessageServiceTransactional(new ProcessedMessageServiceImpl(repository), transactionOperations);
    }

    @Bean
    public ScoresApiClient scoresApiClient(){
        return new ScoresApiClientImpl(new ScoresApi());
    }

    @Bean
    public AddressApiClient addressApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${application.clients.viacep.base-url:https://viacep.com.br}") String baseUrl
    ) {
        return new ViaCepAddressApiClient(
                restClientBuilder.baseUrl(baseUrl).build()
        );
    }

    @Bean
    public AddressService addressService(
            AddressRepository repository,
            AddressApiClient addressApiClient,
            AddressLookupPublisher eventPublisher,
            TransactionOperations transactionOperations
    ) {
        return new AddressServiceTransactional(
                new AddressServiceImpl(repository, addressApiClient, eventPublisher),
                transactionOperations
        );
    }

    @Bean
    public AddressLookupLogService addressLookupLogService(
            AddressLookupLogRepository repository,
            TransactionOperations transactionOperations
    ) {
        return new AddressLookupLogServiceTransactional(
                new AddressLookupLogServiceImpl(repository),
                transactionOperations
        );
    }
}
