package com.suf.wsr.daily_report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
			@RequestParam(value = "password", required = true) String password) {
		System.out.println("Authentication.............");
		boolean status = false;
		
		try {
			status = dailyReport.autentication(username, password);
		} catch (DailyReportException e) {
			e.printStackTrace();
		}
		
		return status;
	}
	
	@RequestMapping("/")
	String home(Model model) {
		return "index";
	}

}
