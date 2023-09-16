package com.litvintsev.accounts.service;

import com.litvintsev.accounts.config.SourceHandlersConfiguration;
import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.AlreadyExistsException;
import com.litvintsev.accounts.exception.BadRequestException;
import com.litvintsev.accounts.repository.AccountRepository;
import com.litvintsev.accounts.repository.entity.AccountEntity;
import com.litvintsev.accounts.service.validation.SourceHandlerFactory;
import com.litvintsev.accounts.service.validation.ValidationService;
import com.litvintsev.accounts.service.validation.ValidationServiceImpl;
import com.litvintsev.accounts.utils.Converter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = SourceHandlersConfiguration.class)
@EnableAutoConfiguration
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccountServiceCreateMethodTest {

    private static final String EMAIL_ERROR = "Email is not set";
    private static final String CLIENT_NAME_ERROR = "Client name is not set";
    private static final String CLIENT_PATRONYMIC_ERROR = "Client patronymic name is not set";
    private static final String CLIENT_SURNAME_ERROR = "Client surname is not set";
    private static final String FULL_MOBILE_ERROR = "Invalid input parameters for source 'mobile'. Cause: Mobile phone number is not set.";
    private static final String BIRTHDAY_ERROR = "Date of birth is not set";
    private static final String BIRTHPLACE_ERROR = "Place of birth is not set";
    private static final String PASSPORT_ERROR = "Passport number is not set";
    private static final String BANK_ID_ERROR = "Bank ID is not set";
    private static final String ADDRESS_ERROR = "Address of registration is not set";
    private static final String INVALID_SOURCE = "Can not validate input parameters. Invalid source application: unknown";
    private static final String ALREADY_EXISTS = "Client with one of the same unique parameters already exists: bankId, passport number, phone, email.";

    @Mock
    private AccountRepository repository;
    @Autowired
    private SourceHandlersConfiguration sourceHandlersConfiguration;

    private ValidationService validationService;
    private AccountServiceImpl accountService;
    private AccountDto validAccount;
    private AccountDto emptyAccount;
    private AccountEntity entity;
    private List<AccountEntity> entities;

    @BeforeEach
    public void setUp() {

        SourceHandlerFactory sourceHandlerFactory = new SourceHandlerFactory(sourceHandlersConfiguration);
        validationService = new ValidationServiceImpl(sourceHandlerFactory);
        accountService = new AccountServiceImpl(validationService, repository);

        validAccount = AccountDto.builder()
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
        emptyAccount = AccountDto.builder().build();

        entity = Converter.toEntity(validAccount);
        entities = List.of(entity);
    }

    @AfterEach
    public void tearDown() {
        validationService = null;
        accountService = null;
        validAccount = null;
        emptyAccount = null;
        entity = null;
    }

    @Test
    @DisplayName("Account created via Gosuslugi")
    void successfulAccountCreationViaGosuslugi() {

        String source = "gosuslugi";
        when(repository.save(any(AccountEntity.class))).thenReturn(entity);

        AccountDto createdDto = accountService.create(validAccount, source);
        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals(validAccount.getBankId(), createdDto.getBankId());
        verify(repository, times(1)).save(any(AccountEntity.class));

    }

    @Test
    @DisplayName("Account created via mail")
    void successfulAccountCreationViaMail() {

        String source = "mail";
        when(repository.save(any(AccountEntity.class))).thenReturn(entity);

        AccountDto createdDto = accountService.create(validAccount, source);
        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals(validAccount.getEmail(), createdDto.getEmail());
        assertEquals(validAccount.getFirstName(), createdDto.getFirstName());
        verify(repository, times(1)).save(any(AccountEntity.class));

    }

    @Test
    @DisplayName("Account created via mobile")
    void successfulAccountCreationViaMobile() {

        String source = "mobile";
        when(repository.save(any(AccountEntity.class))).thenReturn(entity);

        AccountDto createdDto = accountService.create(validAccount, source);
        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals(validAccount.getPhone(), createdDto.getPhone());
        verify(repository, times(1)).save(any(AccountEntity.class));

    }

    @Test
    @DisplayName("Account created via bank")
    void successfulAccountCreationViaBank() {

        String source = "bank";
        when(repository.save(any(AccountEntity.class))).thenReturn(entity);

        AccountDto createdDto = accountService.create(validAccount, source);
        assertNotNull(createdDto);
        assertNotNull(createdDto.getId());
        assertEquals(validAccount.getBankId(), createdDto.getBankId());
        verify(repository, times(1)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Failed account creation via Gosuslugi")
    void failedAccountCreationViaGosuslugi() {

        String source = "gosuslugi";
        assertThatThrownBy(() -> accountService.create(emptyAccount, source))
                .isInstanceOf(BadRequestException.class)
                .hasMessageStartingWith("Invalid input parameters for source 'gosuslugi'")
                .hasMessageContaining(CLIENT_NAME_ERROR)
                .hasMessageContaining(CLIENT_SURNAME_ERROR)
                .hasMessageContaining(CLIENT_PATRONYMIC_ERROR)
                .hasMessageContaining(BIRTHDAY_ERROR)
                .hasMessageContaining(PASSPORT_ERROR)
                .hasMessageContaining(BANK_ID_ERROR)
                .hasMessageContaining(BIRTHPLACE_ERROR)
                .hasMessageContaining(ADDRESS_ERROR);
    }

    @Test
    @DisplayName("Failed account creation via mail")
    void failedAccountCreationViaMail() {

        String source = "mail";
        assertThatThrownBy(() -> accountService.create(emptyAccount, source))
                .isInstanceOf(BadRequestException.class)
                .hasMessageStartingWith("Invalid input parameters for source 'mail'")
                .hasMessageContaining(EMAIL_ERROR)
                .hasMessageContaining(CLIENT_NAME_ERROR);
        verify(repository, times(0)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Failed account creation via mobile")
    void failedAccountCreationViaMobile() {

        String source = "mobile";
        assertThatThrownBy(() -> accountService.create(emptyAccount, source))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(FULL_MOBILE_ERROR);
        verify(repository, times(0)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Failed account creation via bank")
    void failedAccountCreationViaBank() {

        String source = "bank";
        assertThatThrownBy(() -> accountService.create(emptyAccount, source))
                .isInstanceOf(BadRequestException.class)
                .hasMessageStartingWith("Invalid input parameters for source 'bank'")
                .hasMessageContaining(CLIENT_NAME_ERROR)
                .hasMessageContaining(CLIENT_SURNAME_ERROR)
                .hasMessageContaining(CLIENT_PATRONYMIC_ERROR)
                .hasMessageContaining(BIRTHDAY_ERROR)
                .hasMessageContaining(PASSPORT_ERROR)
                .hasMessageContaining(BANK_ID_ERROR);
        verify(repository, times(0)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Fail to create account from unknown application")
    void failToCreateAccountFromUnknownApplication() {

        String source = "unknown";
        assertThatThrownBy(() -> accountService.create(emptyAccount, source))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(INVALID_SOURCE);
        verify(repository, times(0)).save(any(AccountEntity.class));
    }

    @Test
    @DisplayName("Client already exists")
    void clientAlreadyExistsTest() {

        String source = "bank";
        doReturn(entities).when(repository).findByUniqueParameters(
                any(String.class), any(String.class), any(String.class), any(String.class));

        assertThatThrownBy(() -> accountService.create(validAccount, source))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(ALREADY_EXISTS);
    }
}
