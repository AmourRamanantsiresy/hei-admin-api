create extension if not exists "uuid-ossp";

create table if not exists "event"
(
    id                varchar
    constraint event_pk primary key                 default uuid_generate_v4(),
    ref               varchar                  not null
    constraint event_ref_unique unique,
    name              varchar                  not null,
    starting_date     timestamp with time zone not null,
    ending_date       timestamp with time zone not null,
    responsible_id    varchar,
    place_id          varchar,
    constraint        fk_event_place        foreign key(place_id)        references "place"(id),
    constraint        fk_event_responsible  foreign key(responsible_id)  references "user"(id)
);

