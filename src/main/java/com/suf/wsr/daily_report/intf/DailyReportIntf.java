package com.suf.wsr.daily_report.intf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.suf.wsr.daily_report.controller.JiraDTO;

/**
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

public interface DailyReportIntf {
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	List<String> getTickets(String username, String password);
	
	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws DailyReportException
	 */
	boolean autentication(String username, String password) throws DailyReportException;

	/**
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	List<JiraDTO> getJiraTickets(String username, String password, HttpServletRequest request)
			throws URISyntaxException, InterruptedException, ExecutionException;

	/**
	 * 
	 * @param request
	 * @param timeSpent
	 * @param remainingEst
	 * @param manualEst
	 * @param comments
	 * @param ticket
	 * @param excelDP
	 * @param excelEstComm
	 * @return
	 */
	String logWork(HttpServletRequest request, String timeSpent, String remainingEst, String manualEst, String comments,
			String ticket, String excelDP, String excelEstComm);

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	HttpServletResponse generateExcel(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException;

	/**
	 * 
	 * @param ticket
	 * @return
	 */
	JiraDTO getWorkLogDetails(String ticket);

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	Map<String, List<JiraDTO>> getWorkLogToday(HttpServletRequest request, HttpServletResponse response);
	

}
