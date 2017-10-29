/*-
 * #%L
 * Home Automation
 * %%
 * Copyright (C) 2016 - 2017 Koen Serneels
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package be.error.rpi.heating;

import static be.error.rpi.config.RunConfig.getInstance;
import static be.error.rpi.heating.jobs.HeatingInfoPollerJob.JOB_CONFIG_KEY;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Map;

import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tuwien.auto.calimero.GroupAddress;

import be.error.rpi.ebus.EbusCommand;
import be.error.rpi.heating.jobs.HeatingInfoPollerJob;
import be.error.rpi.heating.jobs.JobConfig;

public class HeatingInfoPollerJobSchedulerFactory {

	private static final Logger logger = LoggerFactory.getLogger("heating");

	private final EbusDeviceAddress ebusDeviceAddress;
	private final Map<EbusCommand<?>, GroupAddress> map;

	public HeatingInfoPollerJobSchedulerFactory(EbusDeviceAddress ebusDeviceAddress, Map<EbusCommand<?>, GroupAddress> map) throws Exception {
		this.map = map;
		this.ebusDeviceAddress = ebusDeviceAddress;

		logger.debug("Scheduling HeatingInfoPollerJob");
		getInstance().getScheduler().scheduleJob(getJob(HeatingInfoPollerJob.class, ebusDeviceAddress, map),
				newTrigger().withSchedule(cronSchedule(new CronExpression("0 0/2 * * * ?"))).startNow().build());
	}

	public void triggerNow() throws SchedulerException {
		getInstance().getScheduler().scheduleJob(getJob(HeatingInfoPollerJob.class, ebusDeviceAddress, map),
				newTrigger().startAt(addSeconds(new Date(), 5)).withSchedule(simpleSchedule().withRepeatCount(0)).build());
	}

	private JobDetail getJob(Class<? extends Job> job, EbusDeviceAddress ebusDeviceAddress, Map<EbusCommand<?>, GroupAddress> map) {
		JobDetail jobDetail = newJob(job).build();
		JobConfig jobConfig = new JobConfig(ebusDeviceAddress, map);
		jobDetail.getJobDataMap().put(JOB_CONFIG_KEY, jobConfig);
		return jobDetail;
	}
}
