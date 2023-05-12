/*
 * Copyright 2016-2023 the original author or authors.
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

package org.springframework.cloud.dataflow.server.controller;

import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.dataflow.core.Launcher;
import org.springframework.cloud.dataflow.core.TaskManifest;
import org.springframework.cloud.dataflow.core.TaskPlatform;
import org.springframework.cloud.dataflow.server.config.apps.CommonApplicationProperties;
import org.springframework.cloud.dataflow.server.configuration.JobDependencies;
import org.springframework.cloud.dataflow.server.job.LauncherRepository;
import org.springframework.cloud.dataflow.server.repository.DataflowTaskExecutionMetadataDao;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.cloud.task.repository.dao.TaskExecutionDao;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Ilayaperumal Gopinathan
 * @author Glenn Renfro
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { JobDependencies.class, PropertyPlaceholderAutoConfiguration.class, BatchProperties.class })
@EnableConfigurationProperties({ CommonApplicationProperties.class })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class TaskLogsControllerTests {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TaskLauncher taskLauncher;

	@Autowired
	private LauncherRepository launcherRepository;

	@Autowired
	TaskExecutionDao taskExecutionDao;

	@Autowired
	DataflowTaskExecutionMetadataDao dataflowTaskExecutionMetadataDao;

	@Autowired
	private TaskPlatform taskPlatform;

	@Before
	public void setupMockMVC() {
		Launcher launcher = new Launcher("default", "local", taskLauncher);
		launcherRepository.save(launcher);
		taskPlatform.setLaunchers(Collections.singletonList(launcher));
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
				.defaultRequest(get("/").accept(MediaType.APPLICATION_JSON)).build();
	}

	@Test
	public void testGetCurrentExecutionLog() throws Exception {
		when(taskLauncher.getLog("mytask1")).thenReturn("Log");
		mockMvc.perform(get("/tasks/logs/mytask1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetCurrentExecutionExternalIdLog() throws Exception {
		when(taskLauncher.getLog("mytask1")).thenReturn("Log");
		mockMvc.perform(get("/tasks/logs/mytask1").param("idType", "external").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void testGetCurrentExecutionInvalidTypeIdLog() throws Exception {
		when(taskLauncher.getLog("mytask1")).thenReturn("Log");
		mockMvc.perform(get("/tasks/logs/mytask1").param("idType", "adfads").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError());
	}
	@Test
	public void testGetCurrentExecutionWithInternalIDLog() throws Exception {
		TaskExecution taskExecution = this.taskExecutionDao.createTaskExecution("sampleTaskName", new Date(), Collections.emptyList(), "myTask2");
		TaskManifest taskManifest = new TaskManifest();
		taskManifest.setPlatformName("default");
		this.dataflowTaskExecutionMetadataDao.save(taskExecution, taskManifest);
		when(taskLauncher.getLog("myTask2")).thenReturn("Log");
		mockMvc.perform(get("/tasks/logs/" + taskExecution.getExecutionId()).param("idType", "internal").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
