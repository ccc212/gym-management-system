CREATE TABLE IF NOT EXISTS gym_db.special_arrangements
(
    id             bigint auto_increment
        primary key,
    venue_id       bigint                              not null,
    venue_name     varchar(255)                        not null,
    venue_type     varchar(255)                        not null,
    date           date                                not null,
    time_range     varchar(50)                         not null,
    purpose        varchar(50)                         not null,
    remarks        text                                null,
    created_by     varchar(50)                         not null,
    created_time   datetime    default CURRENT_TIMESTAMP null,
    notify_users   bit         default b'1'            not null,
    status         varchar(20) default 'ACTIVE'        null,
    created_at     datetime    default CURRENT_TIMESTAMP null,
    updated_at     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_special_arrangement_venue
        foreign key (venue_id) references gym_db.venues (id)
)
    row_format = DYNAMIC; 