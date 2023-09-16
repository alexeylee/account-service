package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;

public interface SourceHandler {

    void init(AccountDto dto);
}
