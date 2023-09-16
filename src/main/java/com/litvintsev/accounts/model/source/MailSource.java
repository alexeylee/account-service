package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MailSource implements SourceHandler {

    //TODO implement email format validation
    @NotBlank(message = "Email is not set.")
    private String email;

    @NotBlank(message = "Client name is not set.")
    private String firstName;

    @Override
    public void init(AccountDto dto) {
        this.email = dto.getEmail();
        this.firstName = dto.getFirstName();
    }
}
