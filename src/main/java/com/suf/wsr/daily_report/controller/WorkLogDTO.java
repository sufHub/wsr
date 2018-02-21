package com.suf.wsr.daily_report.controller;

/**
 * DTO class for the WorkLog instance fields
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

public class WorkLogDTO {

	private String workLoggedDate;
	private String timeSpent;

	public String getWorkLoggedDate() {
		return workLoggedDate;
	}
	public void setWorkLoggedDate(String workLoggedDate) {
		this.workLoggedDate = workLoggedDate;
	}
	public String getTimeSpent() {
		return timeSpent;
	}
	public void setTimeSpent(String timeSpent) {
		this.timeSpent = timeSpent;
	}


}
