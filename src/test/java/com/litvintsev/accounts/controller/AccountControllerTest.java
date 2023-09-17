package com.litvintsev.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.*;
import com.litvintsev.accounts.model.AccountFilter;
import com.litvintsev.accounts.service.AccountService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@ContextConfiguration(classes = {AccountController.class, ControllerExceptionHandler.class})
class AccountControllerTest {

    private static final String ERROR_MESSAGE = "This is mocked error message";
    private static final String BAD_REQUEST_MESSAGE = "No parameters were passed. At least one parameter must be passed.";
    private static final String ACCOUNTS_URI = "/v1/accounts";
    private static final String GET_URI = "/v1/accounts/id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AccountService accountService;

    private AccountDto requestDto;
    private AccountDto requestDto2;

    @BeforeEach
    public void setUp() {
        requestDto = AccountDto.builder()
                .id(UUID.randomUUID().toString())
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

        requestDto2 = AccountDto.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Пётр")
                .phone("78887776655")
                .build();
    }

    @AfterEach
    public void tearDown() {
        requestDto = null;
    }

    @Test
    @SneakyThrows
    @DisplayName("Account created via Create method")
    void whenCreateThenShouldCreateAccountResource() {

        //given
        when(this.accountService.create(any(AccountDto.class), any(String.class))).thenReturn(requestDto);

        //when
        ResultActions response = mockMvc.perform(post(ACCOUNTS_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.bankId", is(requestDto.getBankId())))
                .andExpect(jsonPath("$.lastName", is(requestDto.getLastName())))
                .andExpect(jsonPath("$.firstName", is(requestDto.getFirstName())))
                .andExpect(jsonPath("$.middleName", is(requestDto.getMiddleName())))
                .andExpect(jsonPath("$.birthDate").value(requestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.birthPlace", is(requestDto.getBirthPlace())))
                .andExpect(jsonPath("$.passportNumber", is(requestDto.getPassportNumber())))
                .andExpect(jsonPath("$.phone", is(requestDto.getPhone())))
                .andExpect(jsonPath("$.email", is(requestDto.getEmail())))
                .andExpect(jsonPath("$.registrationAddress", is(requestDto.getRegistrationAddress())))
                .andExpect(jsonPath("$.actualAddress", is(requestDto.getActualAddress())));

        verify(accountService, times(1)).create(any(AccountDto.class), any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Create method returns BadRequest status")
    void whenCreateThenShouldReturnBadRequest() {

        AccountDto requestDto = AccountDto.builder().build();
        when(this.accountService.create(any(AccountDto.class), any(String.class)))
                .thenThrow(new BadRequestException(ERROR_MESSAGE));

        ResultActions response = mockMvc.perform(post(ACCOUNTS_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(ACCOUNTS_URI)))
                .andExpect(jsonPath("$.code").value(ExceptionCode.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.error", is(ERROR_MESSAGE)));

        verify(accountService, times(1)).create(any(AccountDto.class), any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Create method returns resource conflict status")
    void whenCreateThenShouldReturnResourceConflict() {

        when(this.accountService.create(any(AccountDto.class), any(String.class)))
                .thenThrow(new AlreadyExistsException(ERROR_MESSAGE));

        ResultActions response = mockMvc.perform(post(ACCOUNTS_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        response.andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(ACCOUNTS_URI)))
                .andExpect(jsonPath("$.code").value(ExceptionCode.ALREADY_EXISTS.name()))
                .andExpect(jsonPath("$.error", is(ERROR_MESSAGE)));

        verify(accountService, times(1)).create(any(AccountDto.class), any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get method returns account")
    void getByIdShouldReturnAccount() {

        String id = requestDto.getId();
        when(this.accountService.get(any(String.class))).thenReturn(requestDto);

        ResultActions response = mockMvc.perform(get("/v1/accounts/id", id));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId())))
                .andExpect(jsonPath("$.bankId", is(requestDto.getBankId())))
                .andExpect(jsonPath("$.lastName", is(requestDto.getLastName())))
                .andExpect(jsonPath("$.firstName", is(requestDto.getFirstName())))
                .andExpect(jsonPath("$.middleName", is(requestDto.getMiddleName())))
                .andExpect(jsonPath("$.birthDate").value(requestDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.birthPlace", is(requestDto.getBirthPlace())))
                .andExpect(jsonPath("$.passportNumber", is(requestDto.getPassportNumber())))
                .andExpect(jsonPath("$.phone", is(requestDto.getPhone())))
                .andExpect(jsonPath("$.email", is(requestDto.getEmail())))
                .andExpect(jsonPath("$.registrationAddress", is(requestDto.getRegistrationAddress())))
                .andExpect(jsonPath("$.actualAddress", is(requestDto.getActualAddress())));

        verify(accountService, times(1)).get(any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get method returns 'not found'")
    void getByIdShouldReturnNotFound() {

        String id = requestDto.getId();
        when(this.accountService.get(any(String.class))).thenThrow(new NotFoundException(ERROR_MESSAGE));

        ResultActions response = mockMvc.perform(get("/v1/accounts/id", id));
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(GET_URI)))
                .andExpect(jsonPath("$.code").value(ExceptionCode.NOT_FOUND.name()))
                .andExpect(jsonPath("$.error", is(ERROR_MESSAGE)));
        verify(accountService, times(1)).get(any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get method returns unexpected error")
    void getByIdShouldReturnUnexpected() {

        String id = requestDto.getId();
        when(this.accountService.get(any(String.class))).thenThrow(new RuntimeException(ERROR_MESSAGE));

        ResultActions response = mockMvc.perform(get("/v1/accounts/id", id));
        response.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(GET_URI)))
                .andExpect(jsonPath("$.code").value(ExceptionCode.UNEXPECTED_EXCEPTION.name()))
                .andExpect(jsonPath("$.error", is(RuntimeException.class.getSimpleName() +": " + ERROR_MESSAGE)));
        verify(accountService, times(1)).get(any(String.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get method with filter parameters returns page of accounts")
    void getByFilterShouldReturnPageOfAccounts() {

        int pageNumber = 0;
        int size = 10;
        MultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
        queryMap.add("pageNumber", String.valueOf(pageNumber));
        queryMap.add("size", String.valueOf(size));
        queryMap.add("lastName", "Иванов");
        queryMap.add("firstName", "Иван");
        queryMap.add("middleName", "Иванович");
        queryMap.add("phone", "79109108070");
        queryMap.add("email", "mail@mail.ru");

        PageRequest pageRequest = PageRequest.of(pageNumber, size);
        Page<AccountDto> pageResponse = new PageImpl<>(List.of(requestDto, requestDto2), pageRequest, 2);

        doReturn(pageResponse).when(accountService).search(any(AccountFilter.class), any(Pageable.class));

        ResultActions response = mockMvc.perform(get("/v1/accounts").queryParams(queryMap));
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.number").value(pageNumber))
                .andExpect(jsonPath("$.content.[0].lastName", is(requestDto.getLastName())))
                .andExpect(jsonPath("$.content.[1].phone", is(requestDto2.getPhone())));

        verify(accountService, times(1)).search(any(AccountFilter.class), any(Pageable.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Get method with empty filter returns bad request")
    void getWithEmptyFilterReturnsBadRequest() {

        when(this.accountService.search(any(AccountFilter.class), any(Pageable.class)))
                .thenThrow(new BadRequestException(BAD_REQUEST_MESSAGE));

        ResultActions response = mockMvc.perform(get("/v1/accounts"));
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(ACCOUNTS_URI)))
                .andExpect(jsonPath("$.code").value(ExceptionCode.BAD_REQUEST.name()))
                .andExpect(jsonPath("$.error", is(BAD_REQUEST_MESSAGE)));
        verify(accountService, times(0)).search(any(AccountFilter.class), any(Pageable.class));
    }
}

