package com.litvintsev.accounts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@TestConfiguration
@ConfigurationProperties(prefix = "application.sources")
//@TestPropertySource("classpath:application-test.yml")
public class SourceHandlersConfiguration {

    private Map<String, String> handlerMap;
}
