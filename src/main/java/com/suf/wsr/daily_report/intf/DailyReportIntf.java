package com.suf.wsr.daily_report.intf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.suf.wsr.daily_report.controller.JiraDTO;

public interface DailyReportIntf {
	
	List<String> getTickets(String username, String password);
	
	boolean autentication(String username, String password) throws DailyReportException;

	List<JiraDTO> getJiraTickets(String username, String password, HttpServletRequest request)
			throws URISyntaxException, InterruptedException, ExecutionException;

	String logWork(HttpServletRequest request, String timeSpent, String remainingEst, String manualEst, String comments,
			String ticket);

	HttpServletResponse generateExcel(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException;
	

}
