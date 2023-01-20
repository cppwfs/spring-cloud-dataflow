/*
 * Copyright 2021 the original author or authors.
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

package org.springframework.cloud.dataflow.server.batch;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mariadb.jdbc.MariaDbDataSource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.dataflow.server.config.apps.CommonApplicationProperties;
import org.springframework.cloud.dataflow.server.configuration.JobDependencies;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.Assert.assertEquals;


@SpringBootTest(classes = { JobDependencies.class,
		PropertyPlaceholderAutoConfiguration.class, BatchProperties.class })
@EnableConfigurationProperties({ CommonApplicationProperties.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
public class Batch5ReadDataTests {

	@Container
	private static final MariaDBContainer mariaDB = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"));
	private MariaDbDataSource dataSource;

	@Autowired
	SimpleJobServiceFactoryBean simpleJobServiceFactoryBean;

	@DynamicPropertySource
	static void mariaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", () -> mariaDB.getJdbcUrl());
		registry.add("spring.datasource.username", () -> mariaDB.getUsername());
		registry.add("spring.datasource.password", () -> mariaDB.getPassword());
	}

	@BeforeAll
	static void setUp() throws Exception{

	}

	@Test
	void testLoadDB() throws Exception {

		dataSource = new MariaDbDataSource();
		dataSource.setUser(mariaDB.getUsername());
		dataSource.setPassword(mariaDB.getPassword());
		dataSource.setUrl(mariaDB.getJdbcUrl());
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

		databasePopulator.addScript(new DefaultResourceLoader().getResource("file:/home/mark/projects/spring-cloud-dataflow/spring-cloud-dataflow-server-core/src/test/resources/schema-mariadb.sql"));
		//databasePopulator.addScript(new ClassPathResource("/org/springframework/cloud/task/schema-mariadb.sql"));
		databasePopulator.addScript(new DefaultResourceLoader().getResource("file:/home/mark/projects/spring-cloud-dataflow/spring-cloud-dataflow-server-core/src/test/resources/singleJobSingleStepJobParam.sql"));
		databasePopulator.execute(dataSource);

		JdbcTemplate template  = new JdbcTemplate(dataSource);
		List<Map<String,Object>> result = template.queryForList("select count(*) as mycount from BATCH5_JOB_INSTANCE");
		assertEquals(1L, result.get(0).get("mycount"));
		result = template.queryForList("select count(*) as mycount from BATCH5_STEP_EXECUTION");
		assertEquals(1L, result.get(0).get("mycount"));
//		result = template.queryForList("select count(*) as mycount from TASK_EXECUTION");
//		assertEquals(1L, result.get(0).get("mycount"));


		simpleJobServiceFactoryBean.setTablePrefix("BATCH5_");
		SimpleJobService simpleJobService = (SimpleJobService) simpleJobServiceFactoryBean.getObject();
		System.out.println("job exec count " + simpleJobService.countJobExecutions());
		Collection<JobExecution> jobExecutions = simpleJobService.listJobExecutions(0, 1);
		for (JobExecution jobExecution : jobExecutions) {
			System.out.println(jobExecution);
		}

	}
}
