/*
 * Copyright 2017-2024 the original author or authors.
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

package org.springframework.cloud.dataflow.composedtaskrunner;

import javax.sql.DataSource;

import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.composedtaskrunner.properties.ComposedTaskProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExplorer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configures the Job that will execute the Composed Task Execution.
 *
 * @author Glenn Renfro
 * @author Corneil du Plessis
 */
@EnableTask
@EnableConfigurationProperties(ComposedTaskProperties.class)
@Configuration
@Import(org.springframework.cloud.dataflow.composedtaskrunner.StepBeanDefinitionRegistrar.class)
public class ComposedTaskRunnerConfiguration {

	@Bean
	public TaskExecutionListener taskExecutionListener() {
		return new ComposedTaskRunnerTaskListener();
	}

	@Bean
	public StepExecutionListener composedTaskStepExecutionListener(TaskExplorer taskExplorer) {
		return new org.springframework.cloud.dataflow.composedtaskrunner.ComposedTaskStepExecutionListener(taskExplorer);
	}

	@Bean
	public ComposedRunnerJobFactory composedTaskJob(ComposedTaskProperties properties) {
		return new ComposedRunnerJobFactory(properties);
	}

	@Bean
	public TaskExecutor taskExecutor(ComposedTaskProperties properties) {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(properties.getSplitThreadCorePoolSize());
		taskExecutor.setMaxPoolSize(properties.getSplitThreadMaxPoolSize());
		taskExecutor.setKeepAliveSeconds(properties.getSplitThreadKeepAliveSeconds());
		taskExecutor.setAllowCoreThreadTimeOut(
			properties.isSplitThreadAllowCoreThreadTimeout());
		taskExecutor.setQueueCapacity(properties.getSplitThreadQueueCapacity());
		taskExecutor.setWaitForTasksToCompleteOnShutdown(
			properties.isSplitThreadWaitForTasksToCompleteOnShutdown());
		return taskExecutor;
	}

	/**
	 * Provides the {@link JobRepository} that is configured to be used by the composed task runner.
	 */
	@Bean
	public BeanPostProcessor jobRepositoryBeanPostProcessor(PlatformTransactionManager transactionManager,
															DataSource incrementerDataSource,
															ComposedTaskProperties composedTaskProperties) {
		return new JobRepositoryBeanPostProcessor(transactionManager, incrementerDataSource, composedTaskProperties);
	}

}
