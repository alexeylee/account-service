package com.litvintsev.accounts.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AccountDto {

    private String id;
    private String bankId;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String passportNumber;
    private String birthPlace;
    private String phone;
    private String email;
    private String registrationAddress;
    private String actualAddress;
}
