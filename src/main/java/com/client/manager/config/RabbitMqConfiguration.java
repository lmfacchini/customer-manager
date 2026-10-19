package com.client.manager.config;

import com.client.manager.dto.CustomerRabbitProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CustomerRabbitProperties.class)
public class RabbitMqConfiguration {

    @Bean
    public TopicExchange customerExchange(
            CustomerRabbitProperties properties
    ) {
        return new TopicExchange(
                properties.exchange(),
                true,
                false
        );
    }

    @Bean
    public TopicExchange customerDeadLetterExchange(
            CustomerRabbitProperties properties
    ) {
        return new TopicExchange(
                properties.deadLetterExchange(),
                true,
                false
        );
    }

    @Bean
    public Queue customerCreatedQueue(
            CustomerRabbitProperties properties
    ) {
        return QueueBuilder
                .durable(properties.queues().customerCreated())
                .build();
    }

    @Bean
    public Queue customerStatusChangeQueue(
            CustomerRabbitProperties properties
    ) {
        return QueueBuilder
                .durable(properties.queues().customerStatusChange())
                .deadLetterExchange(properties.deadLetterExchange())
                .deadLetterRoutingKey(properties.routingKeys().deadLetter())
                .build();
    }

    @Bean
    public Queue customerDeadLetterQueue(
            CustomerRabbitProperties properties
    ) {
        return QueueBuilder
                .durable(properties.queues().deadLetter())
                .build();
    }

    @Bean
    public Queue addressLookupQueue(CustomerRabbitProperties properties) {
        return QueueBuilder
                .durable(properties.queues().addressLookup())
                .deadLetterExchange(properties.deadLetterExchange())
                .deadLetterRoutingKey(properties.routingKeys().deadLetter())
                .build();
    }

    @Bean
    public Binding customerCreatedBinding(
            Queue customerCreatedQueue,
            TopicExchange customerExchange,
            CustomerRabbitProperties properties
    ) {
        return BindingBuilder
                .bind(customerCreatedQueue)
                .to(customerExchange)
                .with(properties.routingKeys().customerCreated());
    }

    @Bean
    public Binding customerStatusChangeBinding(
            Queue customerStatusChangeQueue,
            TopicExchange customerExchange,
            CustomerRabbitProperties properties
    ) {
        return BindingBuilder
                .bind(customerStatusChangeQueue)
                .to(customerExchange)
                .with(properties.routingKeys().customerStatusChange());
    }

    @Bean
    public Binding customerDeadLetterBinding(
            Queue customerDeadLetterQueue,
            TopicExchange customerDeadLetterExchange,
            CustomerRabbitProperties properties
    ) {
        return BindingBuilder
                .bind(customerDeadLetterQueue)
                .to(customerDeadLetterExchange)
                .with(properties.routingKeys().deadLetter());
    }

    @Bean
    public Binding addressLookupBinding(
            Queue addressLookupQueue,
            TopicExchange customerExchange,
            CustomerRabbitProperties properties
    ) {
        return BindingBuilder
                .bind(addressLookupQueue)
                .to(customerExchange)
                .with(properties.routingKeys().addressLookup());
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
