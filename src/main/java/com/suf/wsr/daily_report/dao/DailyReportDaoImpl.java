package com.suf.wsr.daily_report.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.suf.wsr.daily_report.controller.JiraDTO;

@Component
public class DailyReportDaoImpl implements DailyReportDao {

	@Override
	public Connection connect() {
		// SQLite connection string
		String dbFile = "D:\\SQLiteStudio\\DailyReport";
		String url = "jdbc:sqlite:"+dbFile;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	@Override
	public void addTicket(List<JiraDTO> dto) {

		String sql = "INSERT INTO TicketDetails (Key, Assignee, Reporter) "
				+ "values (?,?,?)";

		for(JiraDTO ticket : dto){

			try (Connection conn = this.connect();
					PreparedStatement pstmt = conn.prepareStatement(sql)) {

				pstmt.setString(1, ticket.getTicketNumber());
				pstmt.setString(2, ticket.getAssignee());
				pstmt.setString(3, ticket.getReporter());
				pstmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

		}

	}

	@Override
	public void checkTicket(JiraDTO dto) {

	}

	@Override
	public List<JiraDTO> getAllTicket() {

		String selectSQL = "SELECT * FROM TicketDetails";
		List<JiraDTO> list = new ArrayList<JiraDTO>();

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				JiraDTO ticket = new JiraDTO();
				ticket.setTicketNumber(rs.getString("Key"));
				ticket.setAssignee(rs.getString("Assignee"));
				ticket.setReporter(rs.getString("Reporter"));
				list.add(ticket);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return list;

	}


	@Override
	public List<String> getAllTicketKeys() {

		String selectSQL = "SELECT Key FROM TicketDetails";
		List<String> list = new ArrayList<String>();

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getString("Key"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return list;

	}

	@Override
	public void updateWorkLog(String ticket, String date, String excelDP, String excelEstComm, String timeSpent) {

		String sql = "INSERT INTO WorkLogDetails (Key, Date, Estimation, Comments, EstComments) "
				+ "values (?,?,?,?,?)";

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, ticket);
			pstmt.setString(2, date);
			pstmt.setString(3, timeSpent);
			pstmt.setString(4, excelDP);
			pstmt.setString(5, excelEstComm);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}


	}

	@Override
	public JiraDTO getWorkLogDetails(String ticket) {
		
		JiraDTO jira = new JiraDTO();
		
		// order by Date desc limit 1 : to fetch the latest comments
		
		String sql = "select * from WorkLogDetails where Key = ? order by Date desc limit 1";

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, ticket);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				jira.setTicketNumber(rs.getString("Key"));
				jira.setWorkLogDate(rs.getString("Date"));
				jira.setExcelComments(rs.getString("Comments"));
				jira.setExcelEstComments(rs.getString("EstComments"));
				jira.setEstimated(rs.getString("Estimation"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return jira;
	}
	
	@Override
	public List<String> getWorkLogForToday(String ticket) {
		
		List<String> workLogged = new ArrayList<String>();
		
		String sql = "select * from WorkLogDetails where Key = ? and date(Date) = date('now')";

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, ticket);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				workLogged.add(rs.getString("Estimation"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		return workLogged;
	}

	public Map<String, List<JiraDTO>> getWorkLogToday() {
		
		Map<String, List<JiraDTO>> summary = new HashMap<>();
		
		String sql = "select work.Key, work.Estimation, ticket.Assignee, ticket.Reporter, work.Date from WorkLogDetails work, "
				+ "TicketDetails ticket where date(Date) = date('now') and ticket.Key = work.Key";

		try (Connection conn = this.connect();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				if(summary.containsKey(rs.getString("Assignee"))){
					List<JiraDTO> assigneeList = summary.get(rs.getString("Assignee"));
					populateMap(rs, assigneeList, summary);
				}else{
					populateMap(rs, new ArrayList<JiraDTO>(), summary);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return summary;
	}

	private void populateMap(ResultSet rs, List<JiraDTO> assigneeList, Map<String, List<JiraDTO>> summary) throws SQLException {
		JiraDTO jira = new JiraDTO();
		jira.setTicketNumber(rs.getString("Key"));
		jira.setEstimated(rs.getString("Estimation"));
		jira.setWorkLogDate(rs.getString("Date"));
		jira.setReporter(rs.getString("Reporter"));
		assigneeList.add(jira);
		summary.put(rs.getString("Assignee"), assigneeList);
	}

}
