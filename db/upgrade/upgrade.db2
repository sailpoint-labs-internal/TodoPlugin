
--
-- DB2 DDL plugin script
--

ALTER TABLE tp_todo_list ADD notes varchar(1024);
REORG TABLE tp_todo_list;

CREATE TABLE tp_flagged_user (
    id varchar(32) not null,
    user_id varchar(32) not null,
    username varchar(255) not null,
    num_todos bigint,
    created bigint,
    primary key (id)
) IN identityiq_pl_ts;

