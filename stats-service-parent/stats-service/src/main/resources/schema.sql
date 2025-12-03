create table if not exists endpoint_hits
(
    id        serial unique               not null,
    app       varchar                     not null,
    uri       varchar                     not null,
    ip        varchar                     not null,
    timestamp timestamp without time zone not null
);
