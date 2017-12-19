package com.suf.wsr.daily_report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter  {

	@Autowired
	private JiraAuthentication jiraAuthentication;

	@Autowired
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(this.jiraAuthentication);

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/login").permitAll()    // Permit access for all to login REST service
//		.antMatchers("/").permitAll()         // Necessary to permit access to default document
		.anyRequest().authenticated().and()   // All other requests require authentication
		.httpBasic().and()
		.logout().and()
		.csrf().disable();
	}

}
