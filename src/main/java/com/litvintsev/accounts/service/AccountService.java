package com.litvintsev.accounts.service;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.model.AccountFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {

    AccountDto create(AccountDto account, String source);
    AccountDto get(String id);
    Page<AccountDto> search(AccountFilter filter, Pageable paging);

}
