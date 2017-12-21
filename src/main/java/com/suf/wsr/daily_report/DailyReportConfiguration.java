package com.suf.wsr.daily_report;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.suf.wsr.daily_report.controller.DailyReportController;
import com.suf.wsr.daily_report.impl.DailyReportImpl;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

@Configuration
@Import({DailyReportController.class})
@EnableAutoConfiguration
@ComponentScan
public class DailyReportConfiguration extends WebMvcConfigurerAdapter {

	@Bean(name = "dailyReport")
	public DailyReportIntf getDviewerBean() {
		DailyReportImpl dailyReport = new DailyReportImpl();
		return dailyReport;
	}

	@Bean
	public ViewResolver internalResourceViewResolver() {
	    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	    return resolver;
	}


}



