package com.litvintsev.accounts.model;

import com.litvintsev.accounts.exception.BadRequestException;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class AccountFilter {

    private String lastName;
    private String firstName;
    private String middleName;
    private String phone;
    private String email;

    public static AccountFilterBuilder builder() {
        return new CustomAccountFilterBuilder();
    }

    private static class CustomAccountFilterBuilder extends AccountFilterBuilder {
        @Override
        public AccountFilter build() {
            if(StringUtils.isAllBlank(super.lastName, super.firstName, super.middleName, super.phone, super.email))
                throw new BadRequestException("No parameters were passed. At least one parameter must be passed.");
            return super.build();
        }
    }
}
