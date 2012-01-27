package org.celllife.idart.test;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class LoggingTestListener extends TestListenerAdapter {

	@Override
	public void onConfigurationFailure(ITestResult tr) {
		System.out.println(getOutputString(tr));
		tr.getThrowable().printStackTrace();
	}

	@Override
	public void onConfigurationSkip(ITestResult tr) {
		System.out.println(getOutputString(tr));
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		System.out.println(getOutputString(tr));
		tr.getThrowable().printStackTrace();
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		System.out.println(getOutputString(tr));
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		System.out.println(getOutputString(tr));
	}

	private String getOutputString(ITestResult tr) {
		String s = "";
		String method = tr.getMethod().toString();
		Object[] params = tr.getParameters();
		switch (tr.getStatus()){
		case ITestResult.FAILURE:
			s+= "*** FAILED ***: ";
			break;
		case ITestResult.SKIP:
			s+= "SKIPPED: ";
			break;
		case ITestResult.STARTED:
			s+= "STARTED: ";
			break;
		case ITestResult.SUCCESS:
			s+= "PASSED: ";
			break;
		}
		if (params.length == 0) {
			s += method;
		} else {
			method = method.substring(0, method.indexOf('(') + 1);
			s += method;

			for (int i = 0; i < params.length - 1; i++) {
				s += params[i] + ", ";
			}
			s += params[params.length - 1] + ")";
		}
		return s;
	}

}
