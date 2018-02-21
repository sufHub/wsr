package com.suf.wsr.daily_report.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.suf.wsr.daily_report.intf.DailyReportException;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

/**
 * This Class acts as the Controller class
 * for all the requests sent to the Daily Report application
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

@Controller
public class DailyReportController {
	
	private static final Logger LOGGER = Logger.getLogger(DailyReportController.class);

	@Autowired
	private @Qualifier(value = "dailyReport") DailyReportIntf dailyReport;

	/**
	 * Login method validates the credentials by performing REST WS CALL to JIRA
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = "/login")
	@ResponseBody
	boolean displayDocs1(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		boolean status = false;
		try {
			status = dailyReport.autentication(username, password);
		} catch (DailyReportException e) {
//			LOGGER.info("Authentication FAILURE :"+ e.getMessage());
			e.printStackTrace();
		}

		if (status) {
//			LOGGER.info("Authentication : SUCCESS");
			HttpSession session = request.getSession();
			session.setAttribute("username", username);
			session.setAttribute("password", password);
		}else{
			
		}
		return status;
	}

	/**
	 * Default routing for all the inbound requests
	 * 
	 * @param model
	 * @return
	 */

	@RequestMapping("/")
	String home(Model model) {
		return "index";
	}

	/**
	 * Mapper to bring the WorkLog Popup
	 * 
	 * @param ticket
	 * @return
	 */

	@RequestMapping("/popup")
	ModelAndView popup(@RequestParam(value = "ticket", required = true) String ticket) {
		System.out.println(ticket);

		JiraDTO jira = dailyReport.getWorkLogDetails(ticket);

		ModelAndView view = new ModelAndView("popup");
		view.addObject("comments", jira.getExcelComments());
		view.addObject("estComments", jira.getExcelEstComments());

		return view;
	}

	/**
	 * Mapper to bring the WorkLogged today popup.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */

	@RequestMapping("/workLogToday")
	ModelAndView workLogToday(HttpServletRequest request, HttpServletResponse response) {

		Map<String, List<JiraDTO>> jira = dailyReport.getWorkLogToday(request, response);

		ModelAndView view = new ModelAndView("summary");
		view.addObject("summary", jira);

		return view;
	}

	/**
	 * Removes the user credentials from the session
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@RequestMapping("/logOut")
	@ResponseBody
	String logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("username");
		session.removeAttribute("password");
		return "logout";
	}

	/**
	 * Mapper brings the pop up to log work.
	 * 
	 * @param request
	 * @param response
	 * @param timeSpent
	 * @param remainingEst
	 * @param manualEst
	 * @param comments
	 * @param excelEstComm
	 * @param excelDP
	 * @param ticket
	 * @return
	 */

	@RequestMapping(value = "/logWork")
	@ResponseBody
	String logWork(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "timeSpent", required = true) String timeSpent,
			@RequestParam(value = "remainingEst", required = true) String remainingEst,
			@RequestParam(value = "manualEst", required = true) String manualEst,
			@RequestParam(value = "comments", required = true) String comments,
			@RequestParam(value = "excelEstComm", required = true) String excelEstComm,
			@RequestParam(value = "excelDP", required = true) String excelDP,
			@RequestParam(value = "ticket", required = true) String ticket) {

		String status = dailyReport.logWork(request, timeSpent, remainingEst, manualEst, comments, ticket, excelDP,
				excelEstComm);

		return status;

	}

	/**
	 * Mapper to display all the relevant ticket details.
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws RestClientException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws URISyntaxException
	 */

	@RequestMapping("/view")
	ModelAndView view(Model model, HttpServletRequest request, HttpServletResponse response)
			throws RestClientException, InterruptedException, ExecutionException, URISyntaxException {

		String username = (String) request.getSession().getAttribute("username");
		String password = (String) request.getSession().getAttribute("password");

		if (username == null) {
			return new ModelAndView("index");
		}

		ModelAndView view = new ModelAndView("view");
		view.addObject("username", request.getSession().getAttribute("username"));
		view.addObject("table", dailyReport.getJiraTickets(username, password, request));
		return view;
	}

	/**
	 * Mapper for Generating the Excel Report
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping("/generateExcel")
	public HttpEntity<byte[]> generateExcel(Model model, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		String username = (String) request.getSession().getAttribute("username");
		if (username == null) {
			response.sendRedirect("");
			return null;
		}

		response = dailyReport.generateExcel(request, response);

		FileInputStream fis = new FileInputStream(new File("DailyReport.xlsx"));
		byte[] excelContent = IOUtils.toByteArray(fis);
		String fileName = getFileName() + ".xlsx";

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		header.setContentLength(excelContent.length);

		return new HttpEntity<byte[]>(excelContent, header);

	}

	/**
	 * Utility Class to Generate File Name of the report in expected format.
	 * @return
	 */

	private String getFileName() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMMyyyy");
		String formatDateTime = now.format(formatter);
		return "StatusReport_" + formatDateTime;
	}

}
