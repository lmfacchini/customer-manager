package com.datum.client.manager.config;

import com.datum.client.manager.client.ScoresApiClient;
import com.datum.client.manager.client.impl.ScoresApiClientImpl;
import com.datum.client.manager.mapper.CustomerMapper;
import com.datum.client.manager.mq.publisher.CustomerCreatedPublisher;
import com.datum.client.manager.repository.CustomerRepository;
import com.datum.client.manager.repository.ProcessedMessageRepository;
import com.datum.client.manager.service.CustomerService;
import com.datum.client.manager.service.ProcessedMessageService;
import com.datum.client.manager.service.impl.CustomerServiceImpl;
import com.datum.client.manager.service.impl.ProcessedMessageServiceImpl;
import com.datum.client.manager.service.transactional.CustomerServiceTransactional;
import com.datum.client.manager.service.transactional.ProcessedMessageServiceTransactional;
import com.datum.client.manager.service.validation.CustomerValidationService;
import com.datum.client.manager.service.validation.impl.CustomerValidationServiceImpl;
import com.datum.customer.adapter.output.score.generated.api.ScoresApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

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
}
