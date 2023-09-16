package com.litvintsev.accounts.service.validation;

import com.litvintsev.accounts.config.SourceHandlersConfiguration;
import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.BadRequestException;
import com.litvintsev.accounts.model.source.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
@RequiredArgsConstructor
public class SourceHandlerFactory {

    private final SourceHandlersConfiguration configuration;

    public SourceHandler createHandler(String sourceType, AccountDto account) {

        String className = configuration.getHandlerMap().get(sourceType);
        //check if the source application code is valid
        if (className == null)
            throw new BadRequestException("Can not validate input parameters. Invalid source application: " + sourceType);
        return constructHandler(className, account);
    }

    @SneakyThrows
    private SourceHandler constructHandler(String className, AccountDto account) {

        //create instance of the handler by given class name, then initialize handler's properties
        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor = clazz.getConstructor();
        Object object = ctor.newInstance();
        SourceHandler handler = (SourceHandler)object;
        handler.init(account);
        return handler;
    }
}
