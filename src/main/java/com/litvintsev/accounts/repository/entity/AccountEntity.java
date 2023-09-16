package com.litvintsev.accounts.repository.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "accounts", schema = "public")
public class AccountEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "bank_id")
    private String bankId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "passport_number")
    private String passportNumber;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "registration_address")
    private String registrationAddress;

    @Column(name = "actual_address")
    private String actualAddress;
}
