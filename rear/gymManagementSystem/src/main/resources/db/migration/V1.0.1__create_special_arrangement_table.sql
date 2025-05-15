CREATE TABLE IF NOT EXISTS gym_db.special_arrangements
(
    id             bigint auto_increment
        primary key,
    venue_id       bigint                              not null,
    venue_name     varchar(255)                        not null,
    venue_type     varchar(255)                        not null,
    date           date                                not null,
    start_time     varchar(50)                         not null,
    end_time       varchar(50)                         not null,
    status         varchar(20) default 'ACTIVE'        not null,
    remarks        text                                null,
    created_at     datetime    default CURRENT_TIMESTAMP null,
    updated_at     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint fk_special_arrangement_venue
        foreign key (venue_id) references gym_db.venues (id)
)
    row_format = DYNAMIC; 