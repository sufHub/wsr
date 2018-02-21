package com.suf.wsr.daily_report;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.suf.wsr.daily_report.controller.JiraDTO;
import com.suf.wsr.daily_report.dao.DailyReportDaoImpl;


/**
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

@Test
public class DailyReportApplicationTest {
	
	DailyReportDaoImpl dao = new DailyReportDaoImpl("jdbc:sqlite:DailyReport");  

	public void insertTest(){
		
		List<JiraDTO> list = new ArrayList<JiraDTO>();
		
		JiraDTO ticket = new JiraDTO();
		ticket.setTicketNumber("TEST-1000");
		ticket.setExcelComments("Test");
		ticket.setExcelEstComments("TEST");
		ticket.setAssignee("ummerfas");
		ticket.setReporter("ummerfas");
		
		list.add(ticket);
		
		dao.addTicket(list);
		
	}
	
	public void getTicketsTest(){
		
		List<JiraDTO> list = dao.getAllTicket();
		for(JiraDTO ticket : list){
			System.out.println(ticket.getTicketNumber());
		}
		
	}


}
