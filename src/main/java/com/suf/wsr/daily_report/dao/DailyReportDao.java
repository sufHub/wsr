package com.suf.wsr.daily_report.dao;

import java.sql.Connection;
import java.util.List;

import com.suf.wsr.daily_report.controller.JiraDTO;

/**
 * DAO interface
 * 
 * @author ShaikUmmerFaruk_D
 *
 */

public interface DailyReportDao {

	/**
	 * Retuns connection object
	 * @return SQL Connection Object
	 */
	public Connection connect();

	/**
	 * Adds given JIRA details into the Database
	 * @param dto JiraDTO to be updated
	 */
	public void addTicket(List<JiraDTO> dto);

	/**
	 * Checks if the given JIRA details exist in the Database.
	 * @param dto JiraDTO to be checked in DB
	 */
	public void checkTicket(JiraDTO dto);

	/**
	 * Returns all the JIRA entries stored in DB.
	 * @return List<JiraDTO>
	 */
	public List<JiraDTO> getAllTicket();

	/**
	 * Returns JIRA ticket Numbers in DB.
	 * @return List<String>
	 */
	public List<String> getAllTicketKeys();

	/**
	 * Updates JIRA entries in DB.
	 * 
	 * @param ticket
	 * @param date
	 * @param excelDP
	 * @param excelEstComm
	 * @param timeSpent
	 */
	public void updateWorkLog(String ticket, String date, String excelDP, String excelEstComm, String timeSpent);

	/**
	 * Fetch work logged details from DB.
	 * @param ticket
	 * @return JiraDTO
	 */
	public JiraDTO getWorkLogDetails(String ticket);

	/**
	 * Fetch work logged details for today from DB.
	 * @param ticket
	 * @return List<String>
	 */
	public List<String> getWorkLogForToday(String ticket);

}
