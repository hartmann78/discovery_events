create table if not exists users
(
    id    serial primary key unique                 not null,
    name  varchar(250) check ( length(name) >= 2 )  not null,
    email varchar(254) check ( length(email) >= 6 ) not null unique
);

create table if not exists categories
(
    id   serial primary key unique not null,
    name varchar(50)               not null unique
);

create table if not exists events
(
    id                 serial primary key unique                         not null,
    title              varchar(120) check ( length(title) >= 3 )         not null,
    description        varchar(7000) check ( length(description) >= 20 ) not null,
    annotation         varchar(2000) check ( length(annotation) >= 20)   not null,
    event_date         varchar                                           not null,
    initiator_id       bigint references users (id)                      not null,
    category_id        bigint references categories (id)                 not null,
    lat                float8                                            not null,
    lon                float8                                            not null,
    participant_limit  integer check ( participant_limit >= 0 )          not null,
    paid               boolean                                           not null,
    request_moderation boolean                                           not null,
    confirmed_requests bigint check ( confirmed_requests >= 0 )          not null,
    views              bigint check ( views >= 0 )                       not null,
    created_on         varchar                                           not null,
    published_on       varchar                                           null,
    state              varchar                                           not null,
    comments_available boolean                                           not null,
    show_comments      boolean                                           not null
);

create table if not exists participation_requests
(
    id           serial primary key unique     not null,
    event_id     bigint references events (id) not null,
    requester_id bigint references users (id)  not null,
    created      varchar                       not null,
    status       varchar
);

create table if not exists compilations
(
    id     serial primary key unique not null,
    title  varchar(50)               not null,
    pinned boolean                   not null
);

create table if not exists events_compilations
(
    event_id       bigint references events (id)       not null,
    compilation_id bigint references compilations (id) not null
);

create table if not exists comments
(
    id                   serial primary key unique                       not null,
    text                 varchar(2000)                                   not null,
    from_event_initiator boolean                                         not null,
    author_id            bigint references users (id) on delete cascade  not null,
    event_id             bigint references events (id) on delete cascade not null,
    created_on           varchar                                         not null,
    updated_on           varchar                                         null
);
