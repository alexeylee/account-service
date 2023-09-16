package com.litvintsev.accounts.controller;

import com.litvintsev.accounts.dto.AccountDto;
import com.litvintsev.accounts.model.AccountFilter;
import com.litvintsev.accounts.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto accountDto,
                                             HttpServletRequest request) {
        String source = request.getHeader("x-Source");
        AccountDto createdAccount = accountService.create(accountDto, source);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping(value = "/{id}",
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> get(@PathVariable String id) {

        AccountDto account = accountService.get(id);
        return ResponseEntity.ok(account);
    }

    //TODO use custom PageResponse class insted of Page
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<AccountDto>> search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {

        AccountFilter filter = AccountFilter.builder()
                .lastName(lastName)
                .firstName(firstName)
                .middleName(middleName)
                .phone(phone)
                .email(email)
                .build();

        Pageable paging = PageRequest.of(page, size);
        Page<AccountDto> accounts = accountService.search(filter, paging);
        return ResponseEntity.ok(accounts);
    }
    //TODO implement POST search method with filter DTO
}
