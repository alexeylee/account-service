package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class BankSource implements SourceHandler {

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

    @NotBlank(message = "Passport number is not set.")
    private String passportNumber;

    @Override
    public void init(AccountDto dto) {
        this.bankId = dto.getBankId();
        this.lastName = dto.getLastName();
        this.firstName = dto.getFirstName();
        this.middleName = dto.getMiddleName();
        this.birthDate = dto.getBirthDate();
        this.passportNumber = dto.getPassportNumber();
    }
}
