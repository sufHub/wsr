package com.suf.wsr.daily_report.intf;

import java.util.List;

public interface DailyReportIntf {
	
	List<String> getTickets(String username, String password);
	
	boolean autentication(String username, String password) throws DailyReportException;
	

}
