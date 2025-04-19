create
    database if not exists gym_db;

use gym_db;

drop table if exists competition;
create table `competition`
(
    id                  bigint                  not null auto_increment primary key,
    name                varchar(255) default '' not null unique comment '赛事名称',
    type                tinyint      default 0  not null comment '赛事类型(0为乒乓球赛，1为篮球赛)',
    category            tinyint      default 0  not null comment '赛事类别(0为淘汰赛，1为循环赛)',
    hoster              varchar(255) default '' not null comment '举办方',
    start_time          datetime     default current_timestamp comment '赛事开始时间',
    end_time            datetime     default current_timestamp comment '赛事结束时间',
    sign_up_deadline    datetime     default current_timestamp comment '报名截止时间',
    sign_up_num         int          default 0  not null comment '已报人数',
    max_sign_up_num     int          default 0  not null comment '最大报名人数',
    status              tinyint      default 0  not null comment '赛事状态(0为未开始，1为正在进行，2为已结束，3为报名已截止)',
    is_team_competition tinyint      default 0  not null comment '是否为团体赛事',
    team_max_num        int          default 0  not null comment '每队人数上限',
    team_min_num        int          default 0  not null comment '每队人数下限',
    requirement         text comment '参赛要求',
    description         text comment '赛事描述',
    create_time         datetime     default current_timestamp comment '创建时间',
    update_time         datetime     default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='赛事表';

drop table if exists competition_item;
create table `competition_item`
(
    id                  bigint                  not null auto_increment primary key,
    name                varchar(255) default '' not null unique comment '项目名',
    type                tinyint      default 0  not null comment '赛事类型(0为乒乓球赛，1为篮球赛)',
    category            tinyint      default 0  not null comment '赛事类别(0为淘汰赛，1为循环赛)',
    is_team_competition tinyint      default 0  not null comment '是否为团体赛事',
    create_time         datetime     default current_timestamp comment '创建时间',
    update_time         datetime     default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='赛事项目表';

insert into competition_item (name, type, category, is_team_competition)
values ('乒乓球男子单打淘汰赛', 0, 0, 0),
       ('乒乓球男子单打循环赛', 0, 1, 0),

       ('乒乓球女子单打淘汰赛', 0, 0, 0),
       ('乒乓球女子单打循环赛', 0, 1, 0),

       ('乒乓球男子双打淘汰赛', 0, 0, 1),
       ('乒乓球男子双打循环赛', 0, 1, 1),

       ('乒乓球女子双打淘汰赛', 0, 0, 1),
       ('乒乓球女子双打循环赛', 0, 1, 1),

       ('乒乓球混合双打淘汰赛', 0, 0, 1),
       ('乒乓球混合双打循环赛', 0, 1, 1),

       ('篮球男子组淘汰赛', 1, 0, 1),
       ('篮球男子组循环赛', 1, 1, 1),

       ('篮球女子组淘汰赛', 1, 0, 1),
       ('篮球女子组循环赛', 1, 1, 1),

       ('篮球混合友谊赛', 1, 1, 1),

       ('篮球公开组淘汰赛', 1, 0, 1),
       ('篮球公开组循环赛', 1, 1, 1);

drop table if exists competition_item_relation;
create table `competition_item_relation`
(
    id                  bigint not null auto_increment primary key,
    competition_id      bigint not null comment '赛事id',
    competition_item_id bigint not null comment '赛事项目id'
) engine = innodb
  default charset = utf8mb4 comment ='赛事与项目关联表';

drop table if exists competition_venue_relation;
create table `competition_venue_relation`
(
    id             bigint             not null auto_increment primary key,
    competition_id bigint             not null comment '赛事id',
    venue_id       bigint             not null comment '场地id',
    start_time     datetime default current_timestamp comment '开始时间',
    end_time       datetime default current_timestamp comment '结束时间',
    status         tinyint  default 0 not null comment '状态(0为预约中，1为预约成功，2为预约失败)'
) engine = innodb
  default charset = utf8mb4 comment ='赛事与场地关联表';

drop table if exists competition_equipment_relation;
create table `competition_equipment_relation`
(
    id             bigint             not null auto_increment primary key,
    competition_id bigint             not null comment '赛事id',
    equipment_id   bigint             not null comment '器材id',
    start_time     datetime default current_timestamp comment '开始时间',
    end_time       datetime default current_timestamp comment '结束时间',
    status         tinyint  default 0 not null comment '状态(0为预约中，1为预约成功，2为预约失败)'
) engine = innodb
  default charset = utf8mb4 comment ='赛事与器材关联表';

drop table if exists competition_sign_up_user;
create table `competition_sign_up_user`
(
    id                  bigint                  not null auto_increment primary key,
    competition_id      bigint                  not null comment '赛事id',
    user_id             bigint                  not null comment '用户id',
    name                varchar(255) default '' not null comment '姓名',
    user_number         varchar(255) default '' not null comment '学号或职工号',
    sex                 varchar(255) default '' not null comment '性别',
    phone               varchar(255) default '' not null comment '电话',
    email               varchar(255) default '' not null comment '邮箱',
    depart_id           int                     not null comment '部门id',
    competition_item_id bigint                  not null comment '赛事项目id',
    user_stuorfac       varchar(255)            not null comment '区分学生或教职工 0 学生 1教师',
    remark              text comment '备注',
    status              tinyint      default 0  not null comment '状态(0为报名中，1为报名成功，2为报名失败)'
) engine = innodb
  default charset = utf8mb4 comment ='赛事个人报名表';

drop table if exists competition_sign_up_team;
create table `competition_sign_up_team`
(
    id                  bigint                  not null auto_increment primary key,
    competition_id      bigint                  not null comment '赛事id',
    team_id             bigint                  not null comment '团队id',
    team_name           varchar(255) default '' not null comment '团队名字',
    leader_name         varchar(255) default '' not null comment '领队姓名',
    leader_phone        varchar(255) default '' not null comment '联系电话',
    depart_id           int                     not null comment '部门id',
    competition_item_id bigint                  not null comment '赛事项目id',
    remark              text comment '备注',
    status              tinyint      default 0  not null comment '状态(0为报名中，1为报名成功，2为报名失败)'
) engine = innodb
  default charset = utf8mb4 comment ='赛事团体报名表';

drop table if exists team;
create table `team`
(
    id           bigint                  not null auto_increment primary key,
    team_name    varchar(255) default '' not null unique comment '团队名字',
    leader_name  varchar(255) default '' not null comment '领队姓名',
    leader_phone varchar(255) default '' not null comment '联系电话',
    depart_id    int                     not null comment '部门id',
    create_time  datetime     default current_timestamp comment '创建时间',
    update_time  datetime     default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='团队表';

drop table if exists team_member_relation;
create table `team_member_relation`
(
    id          bigint not null auto_increment primary key,
    team_id     bigint not null comment '团队id',
    user_id     bigint not null comment '用户id',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='团队与队员关联表';

drop table if exists competition_stage;
create table `competition_stage`
(
    id                  bigint                  not null auto_increment primary key,
    competition_id      bigint                  not null comment '赛事id',
    competition_item_id bigint                  not null comment '赛事项目id',
    venue_id            bigint                  not null comment '场地id',
    stage_name          varchar(255) default '' not null unique comment '阶段名称',
    start_time          datetime     default current_timestamp comment '开始时间',
    end_time            datetime     default current_timestamp comment '结束时间',
    create_time         datetime     default current_timestamp comment '创建时间',
    update_time         datetime     default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='赛事阶段表';

drop table if exists competition_stage_referee_relation;
create table `competition_stage_referee_relation`
(
    id                  bigint not null auto_increment primary key,
    competition_id      bigint not null comment '赛事id',
    competition_item_id bigint not null comment '赛事项目id',
    stage_id            bigint not null comment '阶段id',
    referee_id          bigint not null comment '裁判id',
    create_time         datetime default current_timestamp comment '创建时间',
    update_time         datetime default current_timestamp on update current_timestamp comment '更新时间'
) engine = innodb
  default charset = utf8mb4 comment ='赛事阶段与裁判关联表';