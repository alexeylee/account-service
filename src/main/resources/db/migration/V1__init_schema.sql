create table public.accounts(

    id varchar(36) PRIMARY KEY,
    bank_id varchar(36),
    last_name varchar(250),
    first_name varchar(250),
    middle_name varchar(250),
    birth_date date,
    passport_number varchar(11),
    birth_place text,
    phone varchar(11),
    email varchar(254),
    registration_address text,
    actual_address text
);
