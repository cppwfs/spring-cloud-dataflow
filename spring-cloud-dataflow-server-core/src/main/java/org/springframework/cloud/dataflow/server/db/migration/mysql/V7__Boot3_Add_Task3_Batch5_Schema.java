/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.server.db.migration.mysql;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.dataflow.common.flyway.SqlCommand;
import org.springframework.cloud.dataflow.server.db.migration.AbstractBoot3InitialSetupMigration;

/**
 * Adds the <a href="https://github.com/spring-cloud/spring-cloud-task/blob/main/spring-cloud-task-core/src/main/resources/org/springframework/cloud/task/schema-mysql.sql">spring-cloud-task V3</a>
 * and <a href="https://github.com/spring-projects/spring-batch/blob/main/spring-batch-core/src/main/resources/org/springframework/batch/core/schema-mysql.sql">spring-batch V5</a>
 * schemas to support Boot3 compatability.
 * <p>Schemas have added table prefix of {@code "BOOT3_"}.
 *
 * @author Chris Bono
 * @author Corneil du Plessis
 */
public class V7__Boot3_Add_Task3_Batch5_Schema extends AbstractBoot3InitialSetupMigration {

	public final static String CREATE_TASK_EXECUTION_TABLE =
			"CREATE TABLE BOOT3_TASK_EXECUTION\n" +
			"(\n" +
			"    TASK_EXECUTION_ID     BIGINT NOT NULL PRIMARY KEY,\n" +
			"    START_TIME            DATETIME(6) DEFAULT NULL,\n" +
			"    END_TIME              DATETIME(6) DEFAULT NULL,\n" +
			"    TASK_NAME             VARCHAR(100),\n" +
			"    EXIT_CODE             INTEGER,\n" +
			"    EXIT_MESSAGE          VARCHAR(2500),\n" +
			"    ERROR_MESSAGE         VARCHAR(2500),\n" +
			"    LAST_UPDATED          TIMESTAMP,\n" +
			"    EXTERNAL_EXECUTION_ID VARCHAR(255),\n" +
			"    PARENT_EXECUTION_ID   BIGINT\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_TASK_EXECUTION_PARAMS_TABLE =
			"CREATE TABLE BOOT3_TASK_EXECUTION_PARAMS\n" +
			"(\n" +
			"    TASK_EXECUTION_ID BIGINT NOT NULL,\n" +
			"    TASK_PARAM        VARCHAR(2500),\n" +
			"    constraint BOOT3_TASK_EXEC_PARAMS_FK foreign key (TASK_EXECUTION_ID)\n" +
			"        references BOOT3_TASK_EXECUTION (TASK_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_TASK_TASK_BATCH =
			"CREATE TABLE BOOT3_TASK_TASK_BATCH\n" +
			"(\n" +
			"    TASK_EXECUTION_ID BIGINT NOT NULL,\n" +
			"    JOB_EXECUTION_ID  BIGINT NOT NULL,\n" +
			"    constraint BOOT3_TASK_EXEC_BATCH_FK foreign key (TASK_EXECUTION_ID)\n" +
			"        references BOOT3_TASK_EXECUTION (TASK_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_TASK_LOCK_TABLE =
			"CREATE TABLE BOOT3_TASK_LOCK\n" +
			"(\n" +
			"    LOCK_KEY     CHAR(36)     NOT NULL,\n" +
			"    REGION       VARCHAR(100) NOT NULL,\n" +
			"    CLIENT_ID    CHAR(36),\n" +
			"    CREATED_DATE DATETIME(6)  NOT NULL,\n" +
			"    constraint BOOT3_LOCK_PK primary key (LOCK_KEY, REGION)\n" +
			") ENGINE=InnoDB";

	private final static String CREATE_TASK_SEQ_SEQUENCE =
			"CREATE TABLE BOOT3_TASK_SEQ\n" +
			"(\n" +
			"    ID         BIGINT  NOT NULL,\n" +
			"    UNIQUE_KEY CHAR(1) NOT NULL,\n" +
			"    constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)\n" +
			") ENGINE=InnoDB";
	private final static String INIT_TASK_SEQ =
			"INSERT INTO BOOT3_TASK_SEQ (ID, UNIQUE_KEY)\n" +
			"select *\n" +
			"from (select 0 as ID, '0' as UNIQUE_KEY) as tmp";

	public final static String CREATE_BATCH_JOB_INSTANCE_TABLE =
			"CREATE TABLE BOOT3_BATCH_JOB_INSTANCE\n" +
			"(\n" +
			"    JOB_INSTANCE_ID BIGINT       NOT NULL PRIMARY KEY,\n" +
			"    VERSION         BIGINT,\n" +
			"    JOB_NAME        VARCHAR(100) NOT NULL,\n" +
			"    JOB_KEY         VARCHAR(32)  NOT NULL,\n" +
			"    constraint BOOT3_JOB_INST_UN unique (JOB_NAME, JOB_KEY)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_JOB_EXECUTION_TABLE =
			"CREATE TABLE BOOT3_BATCH_JOB_EXECUTION\n" +
			"(\n" +
			"    JOB_EXECUTION_ID BIGINT      NOT NULL PRIMARY KEY,\n" +
			"    VERSION          BIGINT,\n" +
			"    JOB_INSTANCE_ID  BIGINT      NOT NULL,\n" +
			"    CREATE_TIME      DATETIME(6) NOT NULL,\n" +
			"    START_TIME       DATETIME(6) DEFAULT NULL,\n" +
			"    END_TIME         DATETIME(6) DEFAULT NULL,\n" +
			"    STATUS           VARCHAR(10),\n" +
			"    EXIT_CODE        VARCHAR(2500),\n" +
			"    EXIT_MESSAGE     VARCHAR(2500),\n" +
			"    LAST_UPDATED     DATETIME(6),\n" +
			"    constraint BOOT3_JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)\n" +
			"        references BOOT3_BATCH_JOB_INSTANCE (JOB_INSTANCE_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_JOB_EXECUTION_PARAMS_TABLE =
			"CREATE TABLE BOOT3_BATCH_JOB_EXECUTION_PARAMS\n" +
			"(\n" +
			"    JOB_EXECUTION_ID BIGINT       NOT NULL,\n" +
			"    PARAMETER_NAME   VARCHAR(100) NOT NULL,\n" +
			"    PARAMETER_TYPE   VARCHAR(100) NOT NULL,\n" +
			"    PARAMETER_VALUE  VARCHAR(2500),\n" +
			"    IDENTIFYING      CHAR(1)      NOT NULL,\n" +
			"    constraint BOOT3_JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)\n" +
			"        references BOOT3_BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_STEP_EXECUTION_TABLE =
			"CREATE TABLE BOOT3_BATCH_STEP_EXECUTION\n" +
			"(\n" +
			"    STEP_EXECUTION_ID  BIGINT       NOT NULL PRIMARY KEY,\n" +
			"    VERSION            BIGINT       NOT NULL,\n" +
			"    STEP_NAME          VARCHAR(100) NOT NULL,\n" +
			"    JOB_EXECUTION_ID   BIGINT       NOT NULL,\n" +
			"    CREATE_TIME        DATETIME(6)  NOT NULL,\n" +
			"    START_TIME         DATETIME(6) DEFAULT NULL,\n" +
			"    END_TIME           DATETIME(6) DEFAULT NULL,\n" +
			"    STATUS             VARCHAR(10),\n" +
			"    COMMIT_COUNT       BIGINT,\n" +
			"    READ_COUNT         BIGINT,\n" +
			"    FILTER_COUNT       BIGINT,\n" +
			"    WRITE_COUNT        BIGINT,\n" +
			"    READ_SKIP_COUNT    BIGINT,\n" +
			"    WRITE_SKIP_COUNT   BIGINT,\n" +
			"    PROCESS_SKIP_COUNT BIGINT,\n" +
			"    ROLLBACK_COUNT     BIGINT,\n" +
			"    EXIT_CODE          VARCHAR(2500),\n" +
			"    EXIT_MESSAGE       VARCHAR(2500),\n" +
			"    LAST_UPDATED       DATETIME(6),\n" +
			"    constraint BOOT3_JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)\n" +
			"        references BOOT3_BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_STEP_EXECUTION_CONTEXT_TABLE =
			"CREATE TABLE BOOT3_BATCH_STEP_EXECUTION_CONTEXT\n" +
			"(\n" +
			"    STEP_EXECUTION_ID  BIGINT        NOT NULL PRIMARY KEY,\n" +
			"    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,\n" +
			"    SERIALIZED_CONTEXT TEXT,\n" +
			"    constraint BOOT3_STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)\n" +
			"        references BOOT3_BATCH_STEP_EXECUTION (STEP_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_JOB_EXECUTION_CONTEXT_TABLE =
			"CREATE TABLE BOOT3_BATCH_JOB_EXECUTION_CONTEXT\n" +
			"(\n" +
			"    JOB_EXECUTION_ID   BIGINT        NOT NULL PRIMARY KEY,\n" +
			"    SHORT_CONTEXT      VARCHAR(2500) NOT NULL,\n" +
			"    SERIALIZED_CONTEXT TEXT,\n" +
			"    constraint BOOT3_JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)\n" +
			"        references BOOT3_BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)\n" +
			") ENGINE=InnoDB";

	public final static String CREATE_BATCH_STEP_EXECUTION_SEQUENCE =
			"CREATE TABLE BOOT3_BATCH_STEP_EXECUTION_SEQ\n" +
			"(\n" +
			"    ID         BIGINT  NOT NULL,\n" +
			"    UNIQUE_KEY CHAR(1) NOT NULL,\n" +
			"    constraint BOOT3_UNIQUE_KEY_UN unique (UNIQUE_KEY)\n" +
			") ENGINE=InnoDB";

	public final static String INIT_BATCH_STEP_EXECUTION_SEQ =
			"INSERT INTO BOOT3_BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY)\n" +
			"select *\n" +
			"from (select 0 as ID, '0' as UNIQUE_KEY) as tmp\n" +
			"where not exists(select * from BOOT3_BATCH_STEP_EXECUTION_SEQ)";

	public final static String CREATE_BATCH_JOB_EXECUTION_SEQUENCE =
			"CREATE TABLE BOOT3_BATCH_JOB_EXECUTION_SEQ\n" +
			"(\n" +
			"    ID         BIGINT  NOT NULL,\n" +
			"    UNIQUE_KEY CHAR(1) NOT NULL,\n" +
			"    constraint BOOT3_UNIQUE_KEY_UN unique (UNIQUE_KEY)\n" +
			") ENGINE=InnoDB";

	public final static String INIT_BATCH_JOB_EXECUTION_SEQ =
			"INSERT INTO BOOT3_BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY)\n" +
			"select *\n" +
			"from (select 0 as ID, '0' as UNIQUE_KEY) as tmp\n" +
			"where not exists(select * from BOOT3_BATCH_JOB_EXECUTION_SEQ)";

	public final static String CREATE_BATCH_JOB_SEQUENCE =
			"CREATE TABLE BOOT3_BATCH_JOB_SEQ\n" +
			"(\n" +
			"    ID         BIGINT  NOT NULL,\n" +
			"    UNIQUE_KEY CHAR(1) NOT NULL,\n" +
			"    constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)\n" +
			") ENGINE=InnoDB";

	public final static String INIT_BATCH_JOB_SEQ =
			"INSERT INTO BOOT3_BATCH_JOB_SEQ (ID, UNIQUE_KEY)\n" +
			"select *\n" +
			"from (select 0 as ID, '0' as UNIQUE_KEY) as tmp\n" +
			"where not exists(select * from BOOT3_BATCH_JOB_SEQ)";

	@Override
	public List<SqlCommand> createTask3Tables() {
		return Arrays.asList(
				SqlCommand.from(CREATE_TASK_EXECUTION_TABLE),
				SqlCommand.from(CREATE_TASK_EXECUTION_PARAMS_TABLE),
				SqlCommand.from(CREATE_TASK_TASK_BATCH),
				SqlCommand.from(CREATE_TASK_SEQ_SEQUENCE),
				SqlCommand.from(CREATE_TASK_LOCK_TABLE),
				SqlCommand.from(INIT_TASK_SEQ));
	}

	@Override
	public List<SqlCommand> createBatch5Tables() {
		return Arrays.asList(
				SqlCommand.from(CREATE_BATCH_JOB_INSTANCE_TABLE),
				SqlCommand.from(CREATE_BATCH_JOB_EXECUTION_TABLE),
				SqlCommand.from(CREATE_BATCH_JOB_EXECUTION_PARAMS_TABLE),
				SqlCommand.from(CREATE_BATCH_STEP_EXECUTION_TABLE),
				SqlCommand.from(CREATE_BATCH_STEP_EXECUTION_CONTEXT_TABLE),
				SqlCommand.from(CREATE_BATCH_JOB_EXECUTION_CONTEXT_TABLE),
				SqlCommand.from(CREATE_BATCH_STEP_EXECUTION_SEQUENCE),
				SqlCommand.from(CREATE_BATCH_JOB_EXECUTION_SEQUENCE),
				SqlCommand.from(CREATE_BATCH_JOB_SEQUENCE),
				SqlCommand.from(INIT_BATCH_STEP_EXECUTION_SEQ),
				SqlCommand.from(INIT_BATCH_JOB_EXECUTION_SEQ),
				SqlCommand.from(INIT_BATCH_JOB_SEQ));
	}
	@Override
	public List<SqlCommand> createAggregateViews() {
		return Arrays.asList(
				SqlCommand.from(CREATE_AGGREGATE_TASK_EXECUTION_VIEW),
				SqlCommand.from(CREATE_AGGREGATE_TASK_EXECUTION_PARAMS_VIEW),
				SqlCommand.from(CREATE_AGGREGATE_TASK_BATCH_VIEW),
				SqlCommand.from(CREATE_AGGREGATE_JOB_EXECUTION_VIEW),
				SqlCommand.from(CREATE_AGGREGATE_JOB_INSTANCE_VIEW),
				SqlCommand.from(CREATE_AGGREGATE_STEP_EXECUTION_VIEW));
	}
}
