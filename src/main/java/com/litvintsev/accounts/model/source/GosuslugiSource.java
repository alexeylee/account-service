package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GosuslugiSource implements SourceHandler {

    @NotBlank(message = "Bank ID is not set.")
    private String bankId;

    @NotBlank(message = "Client surname is not set.")
    private String lastName;

    @NotBlank(message = "Client name is not set.")
    private String firstName;

    @NotBlank(message = "Client patronymic name is not set.")
    private String middleName;

    @NotNull(message = "Date of birth is not set.")
    private LocalDate birthDate;

    //TODO implement passport number format validation
    @NotBlank(message = "Passport number is not set.")
    private String passportNumber;

    @NotBlank(message = "Place of birth is not set.")
    private String birthPlace;

    //TODO implement phone format validation
    @NotBlank(message = "Mobile phone number is not set.")
    private String phone;

    @NotBlank(message = "Address of registration is not set.")
    private String registrationAddress;

    @Override
    public void init(AccountDto dto) {
        this.bankId = dto.getBankId();
        this.lastName = dto.getLastName();
        this.firstName = dto.getFirstName();
        this.middleName = dto.getMiddleName();
        this.birthDate = dto.getBirthDate();
        this.passportNumber = dto.getPassportNumber();
        this.birthPlace = dto.getBirthPlace();
        this.phone = dto.getPhone();
        this.registrationAddress = dto.getRegistrationAddress();
    }

}
