package com.suf.wsr.daily_report.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.suf.wsr.daily_report.intf.DailyReportException;
import com.suf.wsr.daily_report.intf.DailyReportIntf;


public class DailyReportImpl implements DailyReportIntf {
	
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
			JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
			jiraUserName = restClient.getSessionClient().getCurrentSession().get().getUsername();
		} catch (URISyntaxException e) {
			throw new DailyReportException(e + "Given URL invalid");
		} catch (RestClientException  | InterruptedException | ExecutionException e) {
			LOGGER.error("Error While Accessing Rest Client : "+ e.getMessage());
		}


		return (!jiraUserName.equals(""));
	}


}
