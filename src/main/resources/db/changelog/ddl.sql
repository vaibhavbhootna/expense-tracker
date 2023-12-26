create table public.invoice
(
    retry               integer,
    status              smallint
        constraint invoice_status_check
            check ((status >= 0) AND (status <= 2)),
    id                  bigserial
        primary key,
    invoice_upload_date timestamp(6),
    ocr_api             varchar(255),
    ocr_raw_response    varchar,
    ocr_status          varchar(255),
    upload_source       varchar(255),
    upload_type         varchar(255),
    uploaded_by         varchar(255)
);

alter table public.invoice
    owner to postgres;

create table public.image
(
    file_size  bigint,
    id         bigserial
        primary key,
    invoice_id bigint
        unique
        constraint fkpdwaloxvvd0x2qbdc2et7m8rs
            references public.invoice,
    file_data  varchar,
    file_name  varchar(255) unique ,
    file_type  varchar(255)
);

alter table public.image
    owner to postgres;

create table public.invoice_details
(
    rounding           double precision,
    sub_total          double precision,
    total_amount       double precision,
    id                 bigserial
        primary key,
    invoice_date_time  timestamp(6),
    invoice_id         bigint
        unique
        constraint fkpc7xa72mljy7weoct7uubgjy7
            references public.invoice,
    currency           varchar(255),
    invoice_number     varchar(255),
    payment_mode       varchar(255),
    store_address      varchar(255),
    store_name         varchar(255),
    store_phone_number varchar(255),
    store_type         varchar(255)
);

alter table public.invoice_details
    owner to postgres;

create table public.invoice_item
(
    item_price         double precision,
    item_quantity      integer,
    item_sub_total     double precision,
    id                 bigserial
        primary key,
    invoice_details_id bigint
        constraint fkr2va2ppqp5tjqc5qakv216j6x
            references public.invoice_details,
    item_name          varchar(255)
);

alter table public.invoice_item
    owner to postgres;

