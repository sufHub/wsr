package com.suf.wsr.daily_report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.suf.wsr.daily_report.intf.DailyReportException;
import com.suf.wsr.daily_report.intf.DailyReportIntf;

@Component 
public class JiraAuthentication implements AuthenticationProvider {

	@Autowired
	private @Qualifier(value = "dailyReport") DailyReportIntf dailyReport;

	@Override
	public Authentication authenticate(Authentication authenticate) throws AuthenticationException {

		try {
			boolean status = dailyReport.autentication(authenticate.getName(), authenticate.getCredentials().toString());
			if(status){
				List<GrantedAuthority> grantedAuths = new ArrayList<>();
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
				grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
				return new UsernamePasswordAuthenticationToken(authenticate.getName(), authenticate.getCredentials().toString());
			}
		} catch (DailyReportException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		 return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}
