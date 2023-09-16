package com.litvintsev.accounts.service.validation;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.BadRequestException;
import com.litvintsev.accounts.model.source.SourceHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@Service
public class ValidationServiceImpl implements ValidationService{

    private final Validator jsrValidator;
    private final SourceHandlerFactory sourceHandlerFactory;

    public ValidationServiceImpl(SourceHandlerFactory sourceHandlerFactory) {
        this.sourceHandlerFactory = sourceHandlerFactory;

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.jsrValidator = factory.getValidator();
    }

    @Override
    public void validate(String sourceLabel, AccountDto account) {

        //create source validation handler according to source application code
        SourceHandler handler = sourceHandlerFactory.createHandler(sourceLabel, account);
        //check if any violations of the validation rules exists
        checkHandlerViolations(handler, sourceLabel);
    }

    private void checkHandlerViolations(SourceHandler handler, String sourceLabel) {

        Set<ConstraintViolation<SourceHandler>> violations = jsrValidator.validate(handler);

        List<String> errorMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        if (!errorMessages.isEmpty()) {
            String message = String.format("Invalid input parameters for source '%s'. Cause: %s",
                    sourceLabel, String.join(StringUtils.SPACE, errorMessages));
            throw new BadRequestException(message);
        }
    }
}
