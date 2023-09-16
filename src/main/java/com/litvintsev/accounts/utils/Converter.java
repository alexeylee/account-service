package com.litvintsev.accounts.utils;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.repository.entity.AccountEntity;

import java.util.List;
import java.util.UUID;

public class Converter {

    public static AccountEntity toEntity(AccountDto dto) {

        AccountEntity entity = new AccountEntity();
        entity.setId(dto.getId() != null ? dto.getId() : UUID.randomUUID().toString());
        entity.setBankId(dto.getBankId());
        entity.setLastName(dto.getLastName());
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setBirthDate(dto.getBirthDate());
        entity.setBirthPlace(dto.getBirthPlace());
        entity.setPassportNumber(dto.getPassportNumber());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setRegistrationAddress(dto.getRegistrationAddress());
        entity.setActualAddress(dto.getActualAddress());
        return entity;
    }

    public static AccountDto toDto(AccountEntity entity) {

        return AccountDto.builder()
                .id(entity.getId())
                .bankId(entity.getBankId())
                .lastName(entity.getLastName())
                .firstName(entity.getFirstName())
                .middleName(entity.getMiddleName())
                .birthDate(entity.getBirthDate())
                .birthPlace(entity.getBirthPlace())
                .passportNumber(entity.getPassportNumber())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .registrationAddress(entity.getRegistrationAddress())
                .actualAddress(entity.getActualAddress())
                .build();
    }

    public static List<AccountDto> toDtos(List<AccountEntity> entites) {

        return entites.stream().map(e -> toDto(e)).toList();
    }

    private Converter() {
    }
}
