--create index for search by required fileds
CREATE INDEX account_idx
    ON public.accounts (last_name, first_name, middle_name);

--create unique indexes to prevent client duplicates
CREATE UNIQUE INDEX account_bank_id_unique_idx
    ON public.accounts (bank_id);

CREATE UNIQUE INDEX account_passport_unique_idx
    ON public.accounts (passport_number);

CREATE UNIQUE INDEX account_phone_unique_idx
    ON public.accounts (phone);

CREATE UNIQUE INDEX account_email_unique_idx
    ON public.accounts (email);

