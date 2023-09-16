package com.litvintsev.accounts.repository;

import com.litvintsev.accounts.repository.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<AccountEntity, String> {

    @Query("""
        select a from AccountEntity a
        where (:lastName is null or lower(a.lastName) = lower(:lastName)) 
            and (:firstName is null or lower(a.firstName) = lower(:firstName)) 
            and (:middleName is null or lower(a.middleName) = lower(:middleName)) 
            and (:phone is null or a.phone = :phone) 
            and (:email is null or lower(a.email) = lower(:email)) """)
    Page<AccountEntity> findByParameters(String lastName, String firstName, String middleName,
                                         String phone, String email, Pageable pageable);

    @Query("""
        select a from AccountEntity a
        where (:bankId is null or lower(a.bankId) = lower(:bankId))
            or (:passport is null or a.passportNumber = :passport)
            or (:phone is null or a.phone = :phone)
            or (:email is null or lower(a.email) = lower(:email))
    """)
    List<AccountEntity> findByUniqueParameters(String bankId, String passport, String phone, String email);
}
