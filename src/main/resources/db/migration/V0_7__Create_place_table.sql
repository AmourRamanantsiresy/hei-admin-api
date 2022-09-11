create extension if not exists "uuid-ossp";

create table if not exists "place"
(
    id                varchar
    constraint place_pk primary key           default uuid_generate_v4(),
    label              varchar                  not null
    constraint place_name_unique unique
);
