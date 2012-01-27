package org.celllife.idart.integration.ekapa;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.xml.DOMConfigurator;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.integration.eKapa.StoredProcs;
import org.celllife.idart.integration.eKapa.EKapa.NumberType;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

public class StoredProcTest {

	public static void main(String[] args) throws IOException,
	InterruptedException, SchedulerException, SQLException {
		DOMConfigurator.configure("log4j.xml");
		iDartProperties.setiDartProperties();
		// testMultiConnections();
		testLongConnection();
	}

	private static void testLongConnection() throws InterruptedException,
	SQLException {
		StoredProcs sp = new StoredProcs();
		sp.init();

		ResultSet rs = sp.search("8012210539083", NumberType.RSAID);

		System.out.println("sleeping");
		Thread.sleep(90 * 1000);
		System.out.println("awake");

		while (rs.next()) {
			System.out.println(rs.getString("ID_NUMBER"));
		}

		sp.closeConnection();
	}

	private static void testMultiConnections() throws SchedulerException,
	InterruptedException {
		Scheduler sched = StdSchedulerFactory.getDefaultScheduler();

		sched.start();

		schedule(sched, "testJob");
		schedule(sched, "testJob2");
		schedule(sched, "testJob3");
		schedule(sched, "testJob4");
		schedule(sched, "testJob5");
		for (int i = 0; i < 20; i++) {
			System.out.println(i);
			Thread.sleep(3000L);
		}
		sched.shutdown(true);
	}

	private static void schedule(Scheduler sched, String name)
	throws SchedulerException {
		JobDetail jobDetail = new JobDetail(name, null, TestJob.class);

		Trigger trigger = TriggerUtils.makeSecondlyTrigger();
		trigger
		.setStartTime(TriggerUtils
				.getNextGivenSecondDate(new Date(), 1));
		trigger.setName("Trigger-" + name);

		sched.scheduleJob(jobDetail, trigger);
	}
}
