package com.litvintsev.accounts.service.validation;

import com.litvintsev.accounts.dto.AccountDto;

public interface ValidationService {

    void validate(String source, AccountDto account);
}
