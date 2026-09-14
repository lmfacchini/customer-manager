package com.datum.client.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
        value = "classpath:message.properties",
        encoding = "UTF-8"
)
public class MessagePropertiesConfiguration {
}
