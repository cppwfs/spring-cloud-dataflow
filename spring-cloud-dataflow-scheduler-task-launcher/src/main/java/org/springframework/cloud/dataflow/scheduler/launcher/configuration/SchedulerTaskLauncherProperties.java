/*
 * Copyright 2019-2020 the original author or authors.
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

package org.springframework.cloud.dataflow.scheduler.launcher.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties used to configure the task launcher.
 *
 * @author Glenn Renfro
 **/
@ConfigurationProperties(prefix = "spring.cloud.scheduler.task.launcher")
public class SchedulerTaskLauncherProperties {
	/**
	 * The Spring Cloud Data Flow platform to use for launching tasks.
	 */
	private String platformName = "default";

	/**
	 * The task definition name that is to be launched.
	 */
	private String taskName;

	/**
	 * The URI of the dataflow server that the scheduler task launcher will send the task launch request.
	 */
	private String dataflowServerUri = "http://localhost:9393";

	/**
	 * The prefix to use for the properties that signify which properties should be sent with the task launch request.
	 */
	private String taskLauncherPropertyPrefix = "tasklauncher";

	/**
	 * The number of seconds to wait between checks to see if the task has completed.
	 */
	private long intervalTimeBetweenChecks = 10000;

	/**
	 * The maximum wait time for the SchedulerTaskLauncher to wait for a task to complete.
	 */
	private long maxWaitTime = 0;

	/**
	 * To check if the SchedulerTaskLauncher should wait for the task to complete.
	 */
	private boolean schedulerTaskLauncherWaitForTaskToComplete = true;

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDataflowServerUri() {
		return dataflowServerUri;
	}

	public void setDataflowServerUri(String dataflowServerUri) {
		this.dataflowServerUri = dataflowServerUri;
	}

	public String getTaskLauncherPropertyPrefix() {
		return taskLauncherPropertyPrefix;
	}

	public void setTaskLauncherPropertyPrefix(String taskLauncherPropertyPrefix) {
		this.taskLauncherPropertyPrefix = taskLauncherPropertyPrefix;
	}

	public long getIntervalTimeBetweenChecks() {
		return intervalTimeBetweenChecks;
	}

	public void setIntervalTimeBetweenChecks(long intervalTimeBetweenChecks) {
		this.intervalTimeBetweenChecks = intervalTimeBetweenChecks;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public boolean isSchedulerTaskLauncherWaitForTaskToComplete() {
		return schedulerTaskLauncherWaitForTaskToComplete;
	}

	public void setSchedulerTaskLauncherWaitForTaskToComplete(boolean schedulerTaskLauncherWaitForTaskToComplete) {
		this.schedulerTaskLauncherWaitForTaskToComplete = schedulerTaskLauncherWaitForTaskToComplete;
	}
}

