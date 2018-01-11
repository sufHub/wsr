package com.suf.wsr.daily_report.dao;

public class TicketDTO {
	
	private String key;
	private String assignee;
	private String reporter;
	private String comments;
	private String estComments;
	private String date;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getEstComments() {
		return estComments;
	}
	public void setEstComments(String estComments) {
		this.estComments = estComments;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

}
