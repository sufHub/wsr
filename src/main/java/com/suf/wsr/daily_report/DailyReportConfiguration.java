package com.suf.wsr.daily_report;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.suf.wsr.daily_report.controller.DailyReportController;
import com.suf.wsr.daily_report.impl.DailyReportImpl;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

@Configuration
@Import({DailyReportController.class, SecurityConfig.class})
@EnableAutoConfiguration
@ComponentScan
public class DailyReportConfiguration {
	
	@Bean(name = "dailyReport")
	public DailyReportIntf getDviewerBean() {
		DailyReportImpl dailyReport = new DailyReportImpl();
		return dailyReport;
	}

}
