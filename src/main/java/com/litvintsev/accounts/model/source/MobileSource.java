package com.litvintsev.accounts.model.source;

import com.litvintsev.accounts.dto.AccountDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class MobileSource implements SourceHandler {

    //TODO implement phone format validation
    //@Pattern(regexp = "7\\d{10}", message = "Phone number must be in the format 7XXXXXXXXXX")
    @NotBlank(message = "Mobile phone number is not set.")
    private String phone;

    @Override
    public void init(AccountDto dto) {
        this.phone = dto.getPhone();
    }
}

