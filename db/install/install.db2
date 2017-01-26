
--
-- DB2 DDL plugin script
--

CREATE TABLE tp_todo_list (
    id varchar(32) not null,
    user_id varchar(32) not null,
    name varchar(255) not null,
    estimate bigint,
    notes varchar(1024),
    complete smallint,
    created bigint,
    completed_on bigint,
    primary key (id)
) IN identityiq_pl_ts;

CREATE TABLE tp_flagged_user (
    id varchar(32) not null,
    user_id varchar(32) not null,
    username varchar(255) not null,
    num_todos bigint,
    created bigint,
    primary key (id)
) IN identityiq_pl_ts;

