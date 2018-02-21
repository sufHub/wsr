package com.suf.wsr.daily_report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

@SpringBootApplication
public class DailyReportApplication {

	/**
	 * Application execution starts here
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DailyReportConfiguration.class, args);
	}
}
