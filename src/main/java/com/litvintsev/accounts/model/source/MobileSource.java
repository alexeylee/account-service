package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MobileSource implements SourceHandler {

    //TODO implement phone format validation
    @NotBlank(message = "Mobile phone number is not set.")
    //@Pattern()
    private String phone;

    @Override
    public void init(AccountDto dto) {
        this.phone = dto.getPhone();
    }
}
