package com.suf.wsr.daily_report.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;
import com.suf.wsr.daily_report.intf.DailyReportException;
import com.suf.wsr.daily_report.intf.DailyReportIntf;


@Controller
public class DailyReportController {

	@Autowired
	private @Qualifier(value = "dailyReport") DailyReportIntf dailyReport;


	@RequestMapping(value="/login")
	@ResponseBody
	boolean displayDocs1(
			@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "password", required = true) String password,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("Authentication.............");
		boolean status = false;

		try {
			status = dailyReport.autentication(username, password);
		} catch (DailyReportException e) {
			e.printStackTrace();
		}

		if(status){
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

	@RequestMapping("/view")
	ModelAndView view(Model model,HttpServletRequest request, HttpServletResponse response) throws RestClientException, 
	InterruptedException, ExecutionException, URISyntaxException {
		System.out.println("VIEWING....................");
		System.out.println();

		String username = (String)request.getSession().getAttribute("username");

		if(username == null){
			return new ModelAndView("index");
		}


		ModelAndView view = new ModelAndView("view");
		view.addObject("username", request.getSession().getAttribute("username"));
		view.addObject("table", getJiraTickets());
		return view;

	}

	private Iterable<Issue> getJiraTickets() throws URISyntaxException, InterruptedException, ExecutionException {
		JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
		final URI jiraServerUri = new URI("http://jira.lexisnexis.fr/");
		JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, "ummerfas", "Paris@222");
		System.out.println(restClient.getSessionClient().getCurrentSession().get().getUsername());

		Promise<SearchResult> results = restClient.getSearchClient().searchJql("assignee in (UMMERFAS, RIZWANS, BASKARS2) AND Sprint in openSprints() ORDER BY assignee ASC, status ASC");

		for(BasicIssue issue : results.get().getIssues()){
			String key = issue.getKey();
			Issue ticket = restClient.getIssueClient().getIssue(key).get();

			if(ticket.getKey().equalsIgnoreCase("GC-1099"))
				System.out.println(ticket);

		}
		
		return results.get().getIssues();
	}
}
