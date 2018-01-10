package com.suf.wsr.daily_report.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInput;
import com.atlassian.jira.rest.client.api.domain.input.WorklogInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.suf.wsr.daily_report.controller.JiraDTO;
import com.suf.wsr.daily_report.intf.DailyReportException;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

public class DailyReportImpl implements DailyReportIntf {

	private static final String FILE_NAME = "DailyReport";
	private static final String FILE_EXT = ".xlsx";

	private static final Logger LOGGER = Logger.getLogger(DailyReportImpl.class);

	@Override
	public List<String> getTickets(String username, String password) {
		return null;
	}

	@Override
	public boolean autentication(String username, String password) throws DailyReportException {

		JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
		String jiraUserName = "";

		URI jiraServerUri = null;
		try {
			jiraServerUri = new URI("http://jira.lexisnexis.fr/");
			JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, username,
					password);
			jiraUserName = restClient.getSessionClient().getCurrentSession().get().getUsername();
		} catch (URISyntaxException e) {
			throw new DailyReportException(e + "Given URL invalid");
		} catch (RestClientException | InterruptedException | ExecutionException e) {
			LOGGER.error("Error While Accessing Rest Client : " + e.getMessage());
		}

		return (!jiraUserName.equals(""));
	}

	@Override
	public List<JiraDTO> getJiraTickets(String username, String password, HttpServletRequest request)
			throws URISyntaxException, InterruptedException, ExecutionException {

		JiraRestClient jiraClient = getJiraClient(username, password, request);

		Promise<SearchResult> results = jiraClient.getSearchClient().searchJql(
				"assignee in (UMMERFAS, RIZWANS, BASKARS2) AND Sprint in openSprints() ORDER BY assignee ASC, status ASC");
		// "assignee in (UMMERFAS) and key = 'BO-1428'");
		// TODO : Update the JQL

		List<JiraDTO> ticketList = new ArrayList<JiraDTO>();

		for (BasicIssue issue : results.get().getIssues()) {
			JiraDTO jira = new JiraDTO();
			String key = issue.getKey();
			Issue ticket = jiraClient.getIssueClient().getIssue(key).get();

			jira.setTicketNumber(ticket.getKey());

			jira.setAssignee(validate(ticket, "assignee"));
			jira.setReporter(validate(ticket, "reporter"));
			jira.setResolution(validate(ticket, "resoultion"));
			jira.setStatus(validate(ticket, "status"));
			jira.setSummary(validate(ticket, "summary"));

			jira.setEstimated(changeDisplayPattern(validate(ticket, "estimated")));
			jira.setLogged(changeDisplayPattern(validate(ticket, "logged")));
			jira.setRemaining(changeDisplayPattern(validate(ticket, "remaining")));

			jira.setComponents(nullCheckString(ticket.getComponents().toString()));
			jira.setDescription(nullCheckString(ticket.getDescription()));
			jira.setLabels(nullCheckString(ticket.getLabels().toString()));
			jira.setPriority(nullCheckString(ticket.getPriority().getName()));
			jira.setType(nullCheckString(ticket.getIssueType().getName()));

			jira.setCreated(removeTimeZone(nullCheckString(ticket.getCreationDate().toString())));
			jira.setUpdated(removeTimeZone(nullCheckString(ticket.getUpdateDate().toString())));

			ticketList.add(jira);
		}

		return ticketList;
	}

	@Override
	public String logWork(HttpServletRequest request, String timeSpent, String remainingEst, String manualEst,
			String comments, String ticket) {

		String returns = "";

		try {

			String username = (String) request.getSession().getAttribute("username");
			String password = (String) request.getSession().getAttribute("password");
			final WorklogInput worklogInput;

			if (username == null) {
				returns = "index";
			} else {

				String jiraTimeCheck = "invalid";

				jiraTimeCheck = validateJiraTimeStr(timeSpent);
				if(!remainingEst.equals("auto")){
					jiraTimeCheck =  validateJiraTimeStr(manualEst);
				}

				if(jiraTimeCheck.equals("valid")){

					JiraRestClient jiraClient = getJiraClient(username, password, request);
					final Issue issue = jiraClient.getIssueClient().getIssue(ticket).get();

					if (remainingEst.equals("auto")) {
						worklogInput = new WorklogInputBuilder(issue.getSelf()).setStartDate(new DateTime())
								.setComment(comments).setMinutesSpent(convertToMinutes(timeSpent)).setAdjustEstimateAuto()
								.build();
					} else {
						worklogInput = new WorklogInputBuilder(issue.getSelf()).setStartDate(new DateTime())
								.setComment(comments).setMinutesSpent(convertToMinutes(timeSpent))
								.setAdjustEstimateManual(convertToMinutes(manualEst)).build();
					}

					jiraClient.getIssueClient().addWorklog(issue.getWorklogUri(), worklogInput);

				}else{
					returns = "invalidTime"; 
				}

			}

		} catch (InterruptedException | ExecutionException | URISyntaxException e) {
			return "error";
		}
		return returns;
	}

	@Override
	public HttpServletResponse generateExcel(HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException, IOException {

		File file = new File(FILE_NAME+FILE_EXT);
		file.delete();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Status Report");

		String username = (String) request.getSession().getAttribute("username");
		String password = (String) request.getSession().getAttribute("password");

		try {

			List<JiraDTO> tickets = getJiraTickets(username, password, request);

			System.out.println("Creating excel");

			String[] headers = new String[] {"Ticket", "Summary","Assignee","Reporter","Status","Points to be discussed","Work Logged on Current","Planned Duration","Remaining Estaimation","Estimation Comments","Target Date"};
			int noOfColumns = headers.length-1;
			int rowCount = 0;

			Row rowZero = sheet.createRow(rowCount++);
			CellStyle style = workbook.createCellStyle();
			Font font = workbook.createFont();
			font.setBold(true);
			style.setFont(font);
			for (int col = 0; col <= noOfColumns; col++) {
				Cell cell = rowZero.createCell(col);
				rowZero.setHeightInPoints(50);
				cell.setCellValue(headers[col]);
				style.setWrapText(true); //Set wordwrap
				style.setBorderTop(BorderStyle.THIN );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				cell.setCellStyle(style);
			}

			rowCount = 0;
			for(JiraDTO jira : tickets){

				Row row = sheet.createRow(++rowCount);

				createCell(workbook, jira.getTicketNumber(), row, 0);
				createCell(workbook, jira.getSummary(), row, 1);
				createCell(workbook, jira.getAssignee(), row, 2);
				createCell(workbook, jira.getReporter(), row, 3);
				createCell(workbook, jira.getStatus(), row, 4);
				createCell(workbook, "", row, 5);
				createCell(workbook, "", row, 6);
				createCell(workbook, jira.getEstimated(), row, 7);
				createCell(workbook, jira.getRemaining(), row, 8);
				createCell(workbook, "", row, 9);
				createCell(workbook, "", row, 10);

			}

			alignSheet(sheet);

			try {
				FileOutputStream outputStream = new FileOutputStream(FILE_NAME+FILE_EXT);
				workbook.write(outputStream);
				workbook.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Done");

		} catch (URISyntaxException | InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}

		return response;
	}


	private String validateJiraTimeStr(String test) {

		List<String> charList = new ArrayList<String>();
		charList.add("w");
		charList.add("d");
		charList.add("h");
		charList.add("m");

		String ret = "invalid";
		String[] testArr = test.trim().split(" ");
		for(String t : testArr){
			if(t.length() >=2 && checkNumeric(t.substring(0, t.length()-1)) ){
				if(charList.contains(Character.toString(t.charAt(t.length()-1)).toLowerCase())){
					charList.remove(Character.toString(t.charAt(t.length()-1)));
					ret =  "valid";
				}else
				{
					ret = "invalid";
					break;
				}
			}else{
				ret = "invalid";
				break;
			}
		}

		return ret;
	}

	private static boolean checkNumeric(String substring) {
		boolean isNumeric = substring.chars().allMatch( Character::isDigit );
		return isNumeric;
	}

	private void alignSheet(XSSFSheet sheet) {
		sheet.setColumnWidth(0, 3000);
		sheet.setColumnWidth(1, 12000);
		sheet.setColumnWidth(2, 8000);
		sheet.setColumnWidth(3, 9000);
		sheet.setColumnWidth(4, 3000);
		sheet.setColumnWidth(5, 12000);
		sheet.setColumnWidth(6, 2500);
		sheet.setColumnWidth(7, 2500);
		sheet.setColumnWidth(8, 2900);
		sheet.setColumnWidth(9, 10000);
		sheet.setColumnWidth(10, 2000);
		sheet.setColumnWidth(11, 2000);
		sheet.setZoom(85);
	}

	private void createCell(XSSFWorkbook workbook, String content, Row row, int position) {
		Cell cell = row.createCell(position);
		textWrap(workbook, content, cell);
		cell.setCellValue(content);
	}

	private void textWrap(XSSFWorkbook workbook, String jira, Cell cell) {
		CellStyle styles = workbook.createCellStyle();
		styles.setBorderTop(BorderStyle.THIN );
		styles.setBorderBottom(BorderStyle.THIN);
		styles.setBorderLeft(BorderStyle.THIN);
		styles.setBorderRight(BorderStyle.THIN);
		if(jira.length() > 50){
			styles.setWrapText(true);
		}
		cell.setCellStyle(styles); 
	}

	private JiraRestClient getJiraClient(String username, String password, HttpServletRequest request)
			throws URISyntaxException, InterruptedException, ExecutionException {

		JiraRestClient jiraClient = (JiraRestClient) request.getSession().getAttribute("jiraClient");

		if (jiraClient == null) {
			JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
			final URI jiraServerUri = new URI("http://jira.lexisnexis.fr/");
			jiraClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
			request.setAttribute("jiraClient", jiraClient);
		}

		return jiraClient;
	}

	private String validate(Issue ticket, String tobeChecked) {

		switch (tobeChecked) {
		case "estimated":
			return (nullCheckObject(ticket.getTimeTracking())
					&& ticket.getTimeTracking().getOriginalEstimateMinutes() != null)
					? ticket.getTimeTracking().getOriginalEstimateMinutes().toString() : "";

		case "logged":
			return (nullCheckObject(ticket.getTimeTracking()) && ticket.getTimeTracking().getTimeSpentMinutes() != null)
					? ticket.getTimeTracking().getTimeSpentMinutes().toString() : "";

		case "remaining":
			return (nullCheckObject(ticket.getTimeTracking())
					&& ticket.getTimeTracking().getRemainingEstimateMinutes() != null)
					? ticket.getTimeTracking().getRemainingEstimateMinutes().toString() : "";

		case "assignee":
			return (nullCheckObject(ticket.getAssignee()) && nullCheckObject(ticket.getAssignee().getDisplayName()))
					? ticket.getAssignee().getDisplayName() : "";

		case "reporter":
			return (nullCheckObject(ticket.getReporter()) && ticket.getReporter().getDisplayName() != null)
					? ticket.getReporter().getDisplayName() : "";

		case "resolution":
			return (nullCheckObject(ticket.getResolution()) && ticket.getResolution().getName() != null)
					? ticket.getResolution().getName() : "";

		case "status":
			return (nullCheckObject(ticket.getStatus()) && ticket.getStatus().getName() != null)
					? ticket.getStatus().getName() : "";

		case "summary":
			return (nullCheckObject(ticket.getSummary())) ? ticket.getSummary() : "";

		default:
			return "";
		}
	}

	private boolean nullCheckObject(Object tobeChecked) {
		if (tobeChecked != null) {
			return true;
		} else {
			return false;
		}
	}

	private String nullCheckString(String tobeChecked) {
		if (tobeChecked != null) {
			return tobeChecked;
		} else {
			return "";
		}
	}

	private int convertToMinutes(String test) {

		int total = 0;

		try {
			String[] arr = test.split(" ");
			for (String t : arr) {

				char ch = t.charAt(t.length() - 1);
				int val = Integer.valueOf(t.substring(0, t.length() - 1));

				switch (ch) {
				case 'w':
					total = total + (val * 5 * 8 * 60);
					break;
				case 'd':
					total = total + (val * 8 * 60);
					break;
				case 'h':
					total = total + (val * 60);
					break;
				case 'm':
					total = total + val;
					break;
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return total;
	}

	private String changeDisplayPattern(String num) {

		int weekDiv = 0;
		int weekRem = 0;
		int dayDiv = 0;
		int dayRem = 0;
		int hourDiv = 0;
		int hourRem = 0;
		int min = 0;

		if (num.equals("") || num.equals("0")) {
			return "0m";
		}

		int number = Integer.parseInt(num);

		String finalStr = "";

		try {

			weekDiv = number / (5 * 8 * 60);
			weekRem = number % (5 * 8 * 60);

			if (weekRem > 0) {
				dayDiv = weekRem / (8 * 60);
				dayRem = weekRem % (8 * 60);
			}

			if (dayRem > 0) {
				hourDiv = dayRem / (60);
				hourRem = dayRem % (60);
			}

			if (hourRem > 0) {
				min = hourRem;
			}

			finalStr = weekDiv + "w " + dayDiv + "d " + hourDiv + "h " + min + "m";
			finalStr = finalStr.replace("0w", "").replace("0d", "").replace("0h", "").replace("0m", "").trim();

		} catch (Exception e) {
			return "";
		}

		return finalStr;
	}

	private String removeTimeZone(String time) {
		return time.replace(".000+05:30", "").replace("T", " ");
	}

}
