package com.litvintsev.accounts.service;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.BadRequestException;
import com.litvintsev.accounts.exception.NotFoundException;
import com.litvintsev.accounts.repository.AccountRepository;
import com.litvintsev.accounts.repository.entity.AccountEntity;
import com.litvintsev.accounts.service.validation.ValidationService;
import com.litvintsev.accounts.utils.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceGetMethodTest {

    @Mock
    private AccountRepository repository;

    @Mock
    private ValidationService validationService;

    @Autowired
    private AccountServiceImpl accountService;

    private final String clientId = "dc115177-67d4-49ef-8c7e-95f2d977e516";
    private AccountEntity entity1;
    private AccountEntity entity2;
    private AccountDto account1;
    private AccountDto account2;

    @BeforeEach
    public void setUp() {

        accountService = new AccountServiceImpl(validationService, repository);

        account1 = AccountDto.builder()
                .id(clientId)
                .bankId("aef04278-9ec3-4c5b-888b-e587e5993ce7")
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .birthDate(LocalDate.of(1980, 8,8))
                .birthPlace("г. Москва")
                .passportNumber("7600 552552")
                .phone("79109109010")
                .email("mail@mail.ru")
                .registrationAddress("г. Москва")
                .actualAddress("г. Москва")
                .build();
        account2 = AccountDto.builder().lastName("Петров").build();
        entity1 = Converter.toEntity(account1);
        entity2 = Converter.toEntity(account2);
    }

    @AfterEach
    public void tearDown() {
        accountService = null;
        account2 = null;
        entity1 = null;
        entity2 = null;
    }

    @Test
    @DisplayName("Method get(): client with existing id was found")
    void givenIdShouldReturnClientWithThisId() {

        doReturn(Optional.ofNullable(entity1)).when(repository).findById(clientId);

        AccountDto persisted = accountService.get(clientId);
        assertThat(persisted.getBankId()).isEqualTo(entity1.getBankId());
        verify(repository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Method get(): client with invalid id was not found")
    void givenNotExistingIdShouldReturnNotFound() {

        doReturn(Optional.empty()).when(repository).findById(clientId);
        assertThatThrownBy(() -> accountService.get(clientId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Account with id = " + clientId + " not found");
        verify(repository, times(1)).findById(clientId);
    }

    @Test
    @DisplayName("Method get(): invalid format id causes BadRequestException")
    void givenInvalidIdShouldReturnBadRequest() {

        assertThatThrownBy(() -> accountService.get("not-a-uuid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Parameter 'id' has incorrect format. Id must be an UUID");
        verify(repository, times(0)).findById(clientId);
    }

    @Test
    @DisplayName("Method search(): find different accounts with one query")
    void givenNameShouldReturnPageOfClients() {

        String notExistingEmail = "notExisting@email";
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<AccountEntity> page = new PageImpl<>(List.of(entity1, entity2), pageRequest, 10);
        repository.save(entity1);
        repository.save(entity2);

        doReturn(page).when(repository).findByParameters(account1.getFirstName(), account2.getLastName(),
                null, null, notExistingEmail, pageRequest);

        //searching accounts with given name, given surname and email should return 2 records
        Page<AccountEntity> responsePage = repository.findByParameters(account1.getFirstName(), account2.getLastName(),
                null, null, notExistingEmail, pageRequest);

        verify(repository, times(2)).save(any());
        verify(repository, times(1)).findByParameters(any(), any(), any(), any(), any(), any());
        assertEquals(2, responsePage.getContent().size());
        assertEquals(page.getContent().get(0).getFirstName(), account1.getFirstName());
        assertEquals(page.getContent().get(1).getLastName(), account2.getLastName());
    }
}

