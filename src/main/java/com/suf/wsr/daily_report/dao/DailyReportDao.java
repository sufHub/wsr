package com.suf.wsr.daily_report.dao;

import java.sql.Connection;
import java.util.List;

import com.suf.wsr.daily_report.controller.JiraDTO;

public interface DailyReportDao {
	
	public Connection connect();
	
	public void addTicket(List<JiraDTO> dto);
	
	public void checkTicket(JiraDTO dto);
	
	public List<JiraDTO> getAllTicket();

	public List<String> getAllTicketKeys();
	
	public void updateWorkLog(String ticket, String date, String excelDP, String excelEstComm, String timeSpent);
	
	public JiraDTO getWorkLogDetails(String ticket);

	public List<String> getWorkLogForToday(String ticket);

}
