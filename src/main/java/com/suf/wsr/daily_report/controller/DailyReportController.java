package com.suf.wsr.daily_report.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
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

@Controller
public class DailyReportController {

	@Autowired
	private @Qualifier(value = "dailyReport") DailyReportIntf dailyReport;

	@RequestMapping(value = "/login")
	@ResponseBody
	boolean displayDocs1(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("Authentication.............");
		boolean status = false;

		try {
			status = dailyReport.autentication(username, password);
		} catch (DailyReportException e) {
			e.printStackTrace();
		}

		if (status) {
			HttpSession session = request.getSession();
			session.setAttribute("username", username);
			session.setAttribute("password", password);
		}
		return status;
	}

	@RequestMapping("/")
	String home(Model model) {
		return "index";
	}

	@RequestMapping("/popup")
	String popup(Model model) {
		return "popup";
	}
	
	@RequestMapping("/logOut")
	@ResponseBody
	String logOut(HttpServletRequest request,HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("username");
		session.removeAttribute("password");
		return "logout";
	}

	@RequestMapping(value = "/logWork")
	@ResponseBody
	String logWork(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "timeSpent", required = true) String timeSpent,
			@RequestParam(value = "remainingEst", required = true) String remainingEst,
			@RequestParam(value = "manualEst", required = true) String manualEst,
			@RequestParam(value = "comments", required = true) String comments,
			@RequestParam(value = "ticket", required = true) String ticket
			){

		String status = dailyReport.logWork(request, timeSpent, remainingEst, manualEst, comments, ticket);

		return status;

	}

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


	@ResponseStatus(HttpStatus.OK)
	@RequestMapping("/generateExcel")
	public HttpEntity<byte[]> generateExcel(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String username = (String) request.getSession().getAttribute("username");
		if (username == null) {
			response.sendRedirect("");
			return null;
		}

		response = dailyReport.generateExcel(request, response);

		FileInputStream fis = new FileInputStream(new File("DailyReport.xlsx"));
		byte[] excelContent = IOUtils.toByteArray(fis);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=DailyReport.xlsx");
		header.setContentLength(excelContent.length);

		return new HttpEntity<byte[]>(excelContent, header);


	}




}
