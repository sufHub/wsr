package com.suf.wsr.daily_report;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.suf.wsr.daily_report.controller.DailyReportController;
import com.suf.wsr.daily_report.dao.DailyReportDao;
import com.suf.wsr.daily_report.dao.DailyReportDaoImpl;
import com.suf.wsr.daily_report.impl.DailyReportImpl;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

/**
 * Configuration class for the Application
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

@Configuration
@Import({DailyReportController.class})
@EnableAutoConfiguration
@ComponentScan
public class DailyReportConfiguration extends WebMvcConfigurerAdapter {
	
	@Value("${application.jira.url}")
	private String jiraUrl;
	
	@Value("${application.jira.query}")
	private String query;
	
	@Value("${application.jira.workLogQuery}")
	private String workLogQuery;
	
	@Value("${application.DB.connection}")
	private String dbConnection;
	
	/**
	 * 
	 * @return DailyReportIntf
	 */
	@Bean(name = "dailyReport")
	public DailyReportIntf getDviewerBean() {
		DailyReportImpl dailyReport = new DailyReportImpl(jiraUrl, query, workLogQuery);
		return dailyReport;
	}
	
	@Bean(name = "dailyDaoBean")
	public DailyReportDao dailyDaoBean() {
		DailyReportDao dao = new DailyReportDaoImpl(dbConnection);
		return dao;
	}
	
	/**
	 * 
	 * @return ViewResolver
	 */
	@Bean
	public ViewResolver internalResourceViewResolver() {
	    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	    return resolver;
	}

}



