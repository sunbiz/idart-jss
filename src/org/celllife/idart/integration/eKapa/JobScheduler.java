package org.celllife.idart.integration.eKapa;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {

	private final Logger log = Logger.getLogger(JobScheduler.class.getName());
	private Scheduler sched;

	public boolean schedule(String name, String groupName, Class<? extends Job> jobClass,
			int minuteInterval) {
		if (!initScheduler()){
			return false;
		}
		
		log.info("Scheduling job: " + name);
		JobDetail jobDetail = new JobDetail(name, groupName, jobClass);
		//jobDetail.
		Trigger trigger = TriggerUtils.makeMinutelyTrigger(minuteInterval);
		trigger
		.setStartTime(TriggerUtils
				.getNextGivenMinuteDate(new Date(), 1));
		trigger.setName("trigger-" + name);

		try {
			sched.scheduleJob(jobDetail, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("Exception scheduling job", e);
			return false;
		}
	}
	
	public boolean scheduleOnceOff(String name, String groupName, Class<? extends Job> jobClass) {
		
		if (!initScheduler()){
			return false;
		}

		log.info("Scheduling job: " + name);
		JobDetail jobDetail = new JobDetail(name, groupName, jobClass);

		Trigger trigger = TriggerUtils.makeImmediateTrigger("trigger-" + name, 0, 0);
		trigger.setStartTime(new Date());
		
		try {
			sched.scheduleJob(jobDetail, trigger);
			return true;
		} catch (SchedulerException e) {
			log.error("Exception scheduling job", e);
			return false;
		}
	}

	private boolean initScheduler() {
		if (sched == null) {
			try {
				sched = StdSchedulerFactory.getDefaultScheduler();
				sched.start();
			} catch (SchedulerException e) {
				log.error("Exception starting scheduler", e);
				return false;
			}
		}
		return true;
	}

	public void shutdown() {
		if (sched != null) {
			try {
				sched.shutdown(true);
			} catch (SchedulerException e) {
				log.error("Exception shutting down scheduler", e);
			}
		}
	}

	public boolean hasJob(String groupName, String jobName) {
		if (initScheduler()){
			try {
				JobDetail jobDetail = sched.getJobDetail(jobName,groupName);
				return jobDetail != null;
			} catch (SchedulerException e) {
				return true;
			}
		}
		return true;
	}
}
