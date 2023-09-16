package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class MobileSource implements SourceHandler {

    //TODO implement phone format validation
    @NotBlank(message = "Mobile phone number is not set.")
    @Pattern(regexp = "7\\d{10}", message = "Phone number must be in the format 7XXXXXXXXXX")
    private String phone;

    @Override
    public void init(AccountDto dto) {
        this.phone = dto.getPhone();
    }
}
//@Pattern(regexp="^[0-9]+$", message="the value must be positive integer")
