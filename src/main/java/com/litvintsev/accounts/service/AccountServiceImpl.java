package com.litvintsev.accounts.service;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.AlreadyExistsException;
import com.litvintsev.accounts.exception.BadRequestException;
import com.litvintsev.accounts.exception.NotFoundException;
import com.litvintsev.accounts.model.AccountFilter;
import com.litvintsev.accounts.repository.AccountRepository;
import com.litvintsev.accounts.repository.entity.AccountEntity;
import com.litvintsev.accounts.service.validation.ValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.litvintsev.accounts.utils.Converter.*;


@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final ValidationService validationService;
    private final AccountRepository repository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AccountDto create(AccountDto account, String source) {

        //validate input data using factory, that creates validation handlers
        validationService.validate(source, account);

        //check if clients with the same unique attributes exists
        List<AccountEntity> existingAccounts = repository.findByUniqueParameters(
                account.getBankId(), account.getPassportNumber(), account.getPhone(), account.getEmail());
        if (!existingAccounts.isEmpty())
            throw new AlreadyExistsException("Client with one of the same unique parameters already exists.");

        AccountEntity persistedAccount = repository.save(toEntity(account));
        return toDto(persistedAccount);
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public AccountDto get(String id) {

        checkIdIsUuid(id);
        AccountEntity persistedAccount = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account with id = " + id + " not found"));
        return toDto(persistedAccount);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Page<AccountDto> search(AccountFilter filter, Pageable paging) {

        Page<AccountEntity> entitiesPage = repository.findByParameters(
                filter.getLastName(),
                filter.getFirstName(),
                filter.getMiddleName(),
                filter.getPhone(),
                filter.getEmail(),
                paging
        );
        return new PageImpl<>(toDtos(entitiesPage.getContent()), paging, entitiesPage.getTotalElements());
    }

    private void checkIdIsUuid(String id) {
        try {
            UUID.fromString(id);
        } catch(IllegalArgumentException e) {
            throw new BadRequestException("Parameter 'id' has incorrect format. Id must be an UUID");
        }
    }
}
