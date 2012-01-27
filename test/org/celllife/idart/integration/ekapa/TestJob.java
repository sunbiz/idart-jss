package org.celllife.idart.integration.ekapa;

import org.celllife.idart.integration.eKapa.EKapa;
import org.celllife.idart.integration.eKapa.EKapa.NumberType;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class TestJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0)
	throws JobExecutionException {
		try {
			EKapa.search("8012210539083",
					NumberType.RSAID);
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}