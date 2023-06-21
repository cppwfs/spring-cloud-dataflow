create sequence hibernate_sequence start with 1 increment by 1;

create table task_deployment (
  id bigint not null,
  object_version bigint,
  task_deployment_id varchar(255) not null,
  task_definition_name varchar(255) not null,
  platform_name varchar(255) not null,
  created_on timestamp
);

create index idx_rel_name on task_deployment (platform_name);

create table app_registration (
  id bigint not null,
  object_version bigint,
  default_version boolean,
  metadata_uri clob,
  name varchar(255),
  type integer,
  uri clob,
  version varchar(255),
  boot_version varchar(16),
  primary key (id)
);

create table audit_records (
  id bigint not null,
  audit_action bigint,
  audit_data varchar(4000),
  audit_operation bigint,
  correlation_id varchar(255),
  created_by varchar(255),
  created_on timestamp,
  platform_name varchar(255),
  primary key (id)
);

create table stream_definitions (
  definition_name varchar(255) not null,
  definition clob,
  original_definition clob,
  description varchar(255),
  primary key (definition_name)
);

create table task_definitions (
  definition_name varchar(255) not null,
  definition clob,
  description varchar(255),
  primary key (definition_name)
);

CREATE TABLE TASK_EXECUTION (
  TASK_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
  START_TIME TIMESTAMP DEFAULT NULL,
  END_TIME TIMESTAMP DEFAULT NULL,
  TASK_NAME  VARCHAR(100),
  EXIT_CODE INTEGER,
  EXIT_MESSAGE VARCHAR(2500),
  ERROR_MESSAGE VARCHAR(2500),
  LAST_UPDATED TIMESTAMP,
  EXTERNAL_EXECUTION_ID VARCHAR(255),
  PARENT_EXECUTION_ID BIGINT
);

CREATE TABLE TASK_EXECUTION_PARAMS (
  TASK_EXECUTION_ID BIGINT NOT NULL,
  TASK_PARAM VARCHAR(2500),
  constraint TASK_EXEC_PARAMS_FK foreign key (TASK_EXECUTION_ID)
  references TASK_EXECUTION(TASK_EXECUTION_ID)
);

CREATE TABLE TASK_TASK_BATCH (
  TASK_EXECUTION_ID BIGINT NOT NULL,
  JOB_EXECUTION_ID BIGINT NOT NULL,
  constraint TASK_EXEC_BATCH_FK foreign key (TASK_EXECUTION_ID)
  references TASK_EXECUTION(TASK_EXECUTION_ID)
);

CREATE SEQUENCE TASK_SEQ;

CREATE TABLE TASK_LOCK (
  LOCK_KEY CHAR(36) NOT NULL,
  REGION VARCHAR(100) NOT NULL,
  CLIENT_ID CHAR(36),
  CREATED_DATE TIMESTAMP NOT NULL,
  constraint LOCK_PK primary key (LOCK_KEY, REGION)
);

CREATE TABLE BATCH_JOB_INSTANCE (
  JOB_INSTANCE_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  VERSION BIGINT,
  JOB_NAME VARCHAR(100) NOT NULL,
  JOB_KEY VARCHAR(32) NOT NULL,
  constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
);

CREATE TABLE BATCH_JOB_EXECUTION (
  JOB_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  VERSION BIGINT,
  JOB_INSTANCE_ID BIGINT NOT NULL,
  CREATE_TIME TIMESTAMP NOT NULL,
  START_TIME TIMESTAMP DEFAULT NULL,
  END_TIME TIMESTAMP DEFAULT NULL,
  STATUS VARCHAR(10),
  EXIT_CODE VARCHAR(2500),
  EXIT_MESSAGE VARCHAR(2500),
  LAST_UPDATED TIMESTAMP,
  JOB_CONFIGURATION_LOCATION VARCHAR(2500) NULL,
  constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
  references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS (
  JOB_EXECUTION_ID BIGINT NOT NULL,
  TYPE_CD VARCHAR(6) NOT NULL,
  KEY_NAME VARCHAR(100) NOT NULL,
  STRING_VAL VARCHAR(250),
  DATE_VAL TIMESTAMP DEFAULT NULL,
  LONG_VAL BIGINT,
  DOUBLE_VAL DOUBLE PRECISION ,
  IDENTIFYING CHAR(1) NOT NULL ,
  constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
  references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION (
  STEP_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  VERSION BIGINT NOT NULL,
  STEP_NAME VARCHAR(100) NOT NULL,
  JOB_EXECUTION_ID BIGINT NOT NULL,
  START_TIME TIMESTAMP NOT NULL,
  END_TIME TIMESTAMP DEFAULT NULL,
  STATUS VARCHAR(10),
  COMMIT_COUNT BIGINT,
  READ_COUNT BIGINT,
  FILTER_COUNT BIGINT,
  WRITE_COUNT BIGINT,
  READ_SKIP_COUNT BIGINT,
  WRITE_SKIP_COUNT BIGINT,
  PROCESS_SKIP_COUNT BIGINT,
  ROLLBACK_COUNT BIGINT,
  EXIT_CODE VARCHAR(2500),
  EXIT_MESSAGE VARCHAR(2500),
  LAST_UPDATED TIMESTAMP,
  constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
  references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT (
  STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
  SHORT_CONTEXT VARCHAR(2500) NOT NULL,
  SERIALIZED_CONTEXT LONGVARCHAR,
  constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
  references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
);

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT (
  JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
  SHORT_CONTEXT VARCHAR(2500) NOT NULL,
  SERIALIZED_CONTEXT LONGVARCHAR,
  constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
  references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
);

CREATE TABLE TASK_EXECUTION_METADATA (
     ID BIGINT NOT NULL,
     TASK_EXECUTION_ID BIGINT NOT NULL,
     TASK_EXECUTION_MANIFEST CLOB,
     PRIMARY KEY (ID),
     CONSTRAINT TASK_METADATA_FK FOREIGN KEY (TASK_EXECUTION_ID)
     REFERENCES TASK_EXECUTION(TASK_EXECUTION_ID)
);

CREATE SEQUENCE TASK_EXECUTION_METADATA_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE BATCH_STEP_EXECUTION_SEQ;
CREATE SEQUENCE BATCH_JOB_EXECUTION_SEQ;
CREATE SEQUENCE BATCH_JOB_SEQ;
create index STEP_NAME_IDX on BATCH_STEP_EXECUTION (STEP_NAME);
create index TASK_EXECUTION_ID_IDX on TASK_EXECUTION_PARAMS (TASK_EXECUTION_ID);

CREATE TABLE BOOT3_TASK_EXECUTION  (
                               TASK_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY ,
                               START_TIME TIMESTAMP(9) DEFAULT NULL ,
                               END_TIME TIMESTAMP(9) DEFAULT NULL ,
                               TASK_NAME  VARCHAR(100) ,
                               EXIT_CODE INTEGER ,
                               EXIT_MESSAGE VARCHAR(2500) ,
                               ERROR_MESSAGE VARCHAR(2500) ,
                               LAST_UPDATED TIMESTAMP(9),
                               EXTERNAL_EXECUTION_ID VARCHAR(255),
                               PARENT_EXECUTION_ID BIGINT
);

CREATE TABLE BOOT3_TASK_EXECUTION_PARAMS  (
                                      TASK_EXECUTION_ID BIGINT NOT NULL ,
                                      TASK_PARAM VARCHAR(2500) ,
                                      constraint BOOT3_TASK_EXEC_PARAMS_FK foreign key (TASK_EXECUTION_ID)
                                        references BOOT3_TASK_EXECUTION(TASK_EXECUTION_ID)
) ;

CREATE TABLE BOOT3_TASK_EXECUTION_METADATA
(
    ID                      BIGINT NOT NULL,
    TASK_EXECUTION_ID       BIGINT NOT NULL,
    TASK_EXECUTION_MANIFEST LONGTEXT,
    primary key (ID),
    CONSTRAINT BOOT3_TASK_METADATA_FK FOREIGN KEY (TASK_EXECUTION_ID) REFERENCES BOOT3_TASK_EXECUTION (TASK_EXECUTION_ID)
);


CREATE SEQUENCE BOOT3_TASK_EXECUTION_METADATA_SEQ;


CREATE TABLE BOOT3_TASK_TASK_BATCH (
                               TASK_EXECUTION_ID BIGINT NOT NULL ,
                               JOB_EXECUTION_ID BIGINT NOT NULL ,
                               constraint BOOT3_TASK_EXEC_BATCH_FK foreign key (TASK_EXECUTION_ID)
                                 references BOOT3_TASK_EXECUTION(TASK_EXECUTION_ID)
) ;

CREATE SEQUENCE BOOT3_TASK_SEQ;

CREATE TABLE BOOT3_TASK_LOCK  (
                          LOCK_KEY CHAR(36) NOT NULL,
                          REGION VARCHAR(100) NOT NULL,
                          CLIENT_ID CHAR(36),
                          CREATED_DATE TIMESTAMP(9) NOT NULL,
                          constraint BOOT3_LOCK_PK primary key (LOCK_KEY, REGION)
);

CREATE TABLE BOOT3_BATCH_JOB_INSTANCE  (
                                   JOB_INSTANCE_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                   VERSION BIGINT ,
                                   JOB_NAME VARCHAR(100) NOT NULL,
                                   JOB_KEY VARCHAR(32) NOT NULL,
                                   constraint BOOT3_JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ;

CREATE TABLE BOOT3_BATCH_JOB_EXECUTION  (
                                    JOB_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                    VERSION BIGINT  ,
                                    JOB_INSTANCE_ID BIGINT NOT NULL,
                                    CREATE_TIME TIMESTAMP(9) NOT NULL,
                                    START_TIME TIMESTAMP(9) DEFAULT NULL ,
                                    END_TIME TIMESTAMP(9) DEFAULT NULL ,
                                    STATUS VARCHAR(10) ,
                                    EXIT_CODE VARCHAR(2500) ,
                                    EXIT_MESSAGE VARCHAR(2500) ,
                                    LAST_UPDATED TIMESTAMP(9),
                                    constraint BOOT3_JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
                                      references BOOT3_BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ;

CREATE TABLE BOOT3_BATCH_JOB_EXECUTION_PARAMS  (
                                           JOB_EXECUTION_ID BIGINT NOT NULL ,
                                           PARAMETER_NAME VARCHAR(100) NOT NULL ,
                                           PARAMETER_TYPE VARCHAR(100) NOT NULL ,
                                           PARAMETER_VALUE VARCHAR(2500) ,
                                           IDENTIFYING CHAR(1) NOT NULL ,
                                           constraint BOOT3_JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
                                             references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE BOOT3_BATCH_STEP_EXECUTION  (
                                     STEP_EXECUTION_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY ,
                                     VERSION BIGINT NOT NULL,
                                     STEP_NAME VARCHAR(100) NOT NULL,
                                     JOB_EXECUTION_ID BIGINT NOT NULL,
                                     CREATE_TIME TIMESTAMP(9) NOT NULL,
                                     START_TIME TIMESTAMP(9) DEFAULT NULL ,
                                     END_TIME TIMESTAMP(9) DEFAULT NULL ,
                                     STATUS VARCHAR(10) ,
                                     COMMIT_COUNT BIGINT ,
                                     READ_COUNT BIGINT ,
                                     FILTER_COUNT BIGINT ,
                                     WRITE_COUNT BIGINT ,
                                     READ_SKIP_COUNT BIGINT ,
                                     WRITE_SKIP_COUNT BIGINT ,
                                     PROCESS_SKIP_COUNT BIGINT ,
                                     ROLLBACK_COUNT BIGINT ,
                                     EXIT_CODE VARCHAR(2500) ,
                                     EXIT_MESSAGE VARCHAR(2500) ,
                                     LAST_UPDATED TIMESTAMP(9),
                                     constraint BOOT3_JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
                                       references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE TABLE BOOT3_BATCH_STEP_EXECUTION_CONTEXT  (
                                             STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                             SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                             SERIALIZED_CONTEXT LONGVARCHAR ,
                                             constraint BOOT3_STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
                                               references BOOT3_BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ;

CREATE TABLE BOOT3_BATCH_JOB_EXECUTION_CONTEXT  (
                                            JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                                            SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                                            SERIALIZED_CONTEXT LONGVARCHAR ,
                                            constraint BOOT3_JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
                                              references BOOT3_BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ;

CREATE SEQUENCE BOOT3_BATCH_STEP_EXECUTION_SEQ;
CREATE SEQUENCE BOOT3_BATCH_JOB_EXECUTION_SEQ;
CREATE SEQUENCE BOOT3_BATCH_JOB_SEQ;

CREATE VIEW AGGREGATE_TASK_EXECUTION AS
    SELECT TASK_EXECUTION_ID, START_TIME, END_TIME, TASK_NAME, EXIT_CODE, EXIT_MESSAGE, ERROR_MESSAGE, LAST_UPDATED, EXTERNAL_EXECUTION_ID, PARENT_EXECUTION_ID, 'boot2' AS SCHEMA_TARGET FROM TASK_EXECUTION
UNION ALL
    SELECT TASK_EXECUTION_ID, START_TIME, END_TIME, TASK_NAME, EXIT_CODE, EXIT_MESSAGE, ERROR_MESSAGE, LAST_UPDATED, EXTERNAL_EXECUTION_ID, PARENT_EXECUTION_ID, 'boot3' AS SCHEMA_TARGET FROM BOOT3_TASK_EXECUTION;

CREATE VIEW AGGREGATE_TASK_EXECUTION_PARAMS AS
    SELECT TASK_EXECUTION_ID, TASK_PARAM, 'boot2' AS SCHEMA_TARGET FROM TASK_EXECUTION_PARAMS
UNION ALL
    SELECT TASK_EXECUTION_ID, TASK_PARAM, 'boot3' AS SCHEMA_TARGET FROM BOOT3_TASK_EXECUTION_PARAMS;

CREATE VIEW AGGREGATE_JOB_EXECUTION AS
    SELECT JOB_EXECUTION_ID, VERSION, JOB_INSTANCE_ID, CREATE_TIME, START_TIME, END_TIME, STATUS, EXIT_CODE, EXIT_MESSAGE, LAST_UPDATED, 'boot2' AS SCHEMA_TARGET FROM BATCH_JOB_EXECUTION
UNION ALL
    SELECT JOB_EXECUTION_ID, VERSION, JOB_INSTANCE_ID, CREATE_TIME, START_TIME, END_TIME, STATUS, EXIT_CODE, EXIT_MESSAGE, LAST_UPDATED, 'boot3' AS SCHEMA_TARGET FROM BOOT3_BATCH_JOB_EXECUTION;

CREATE VIEW AGGREGATE_JOB_INSTANCE AS
    SELECT JOB_INSTANCE_ID, VERSION, JOB_NAME, JOB_KEY, 'boot2' AS SCHEMA_TARGET FROM BATCH_JOB_INSTANCE
UNION ALL
    SELECT JOB_INSTANCE_ID, VERSION, JOB_NAME, JOB_KEY, 'boot3' AS SCHEMA_TARGET FROM BOOT3_BATCH_JOB_INSTANCE;

CREATE VIEW AGGREGATE_TASK_BATCH AS
    SELECT TASK_EXECUTION_ID, JOB_EXECUTION_ID, 'boot2' AS SCHEMA_TARGET FROM TASK_TASK_BATCH
UNION ALL
    SELECT TASK_EXECUTION_ID, JOB_EXECUTION_ID, 'boot3' AS SCHEMA_TARGET FROM BOOT3_TASK_TASK_BATCH;

CREATE VIEW AGGREGATE_STEP_EXECUTION AS
    SELECT STEP_EXECUTION_ID, VERSION, STEP_NAME, JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS, COMMIT_COUNT, READ_COUNT, FILTER_COUNT, WRITE_COUNT, READ_SKIP_COUNT, WRITE_SKIP_COUNT, PROCESS_SKIP_COUNT, ROLLBACK_COUNT, EXIT_CODE, EXIT_MESSAGE, LAST_UPDATED, 'boot2' AS SCHEMA_TARGET FROM BATCH_STEP_EXECUTION
UNION ALL
    SELECT STEP_EXECUTION_ID, VERSION, STEP_NAME, JOB_EXECUTION_ID, START_TIME, END_TIME, STATUS, COMMIT_COUNT, READ_COUNT, FILTER_COUNT, WRITE_COUNT, READ_SKIP_COUNT, WRITE_SKIP_COUNT, PROCESS_SKIP_COUNT, ROLLBACK_COUNT, EXIT_CODE, EXIT_MESSAGE, LAST_UPDATED, 'boot3' AS SCHEMA_TARGET FROM BOOT3_BATCH_STEP_EXECUTION;