package com.litvintsev.accounts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.exception.*;
import com.litvintsev.accounts.service.AccountService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
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
    private static final String CREATE_URI = "/v1/accounts";
    private static final String GET_URI = "/v1/accounts/id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AccountService accountService;

    private AccountDto requestDto;

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
        ResultActions response = mockMvc.perform(post(CREATE_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));
        //then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.bankId", is(requestDto.getBankId())))
                .andExpect(jsonPath("$.lastName", is(requestDto.getLastName())))
                .andExpect(jsonPath("$.firstName", is(requestDto.getFirstName())))
                .andExpect(jsonPath("$.middleName", is(requestDto.getMiddleName())))
                //.andExpect(jsonPath("$.birthDate", is(requestDto.getBirthDate())))
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

        ResultActions response = mockMvc.perform(post(CREATE_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(CREATE_URI)))
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

        ResultActions response = mockMvc.perform(post(CREATE_URI)
                .header("x-Source", "mobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        response.andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.path", is(CREATE_URI)))
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
}
