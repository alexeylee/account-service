package com.litvintsev.accounts.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "application.sources")
public class SourceHandlersConfiguration {

    private Map<String, String> handlerMap;
}
