package sn.msd.security;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import sn.msd.security.jwt.AuthEntryPointJwt;
import sn.msd.security.jwt.AuthTokenFilterJwt;
import sn.msd.security.service.UserDetailServiceImpl;

@SuppressWarnings("deprecation")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private AuthEntryPointJwt unAuthEntryPointJwt;
	@Autowired
	private UserDetailServiceImpl userDetailServiceImpl;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailServiceImpl).passwordEncoder(bCryptPasswordEncoder);
	}

	@Bean
	public AuthTokenFilterJwt authenticationJwtTokenFilter() {
		return new AuthTokenFilterJwt();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unAuthEntryPointJwt).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/auth/**").permitAll()
				.antMatchers("/agent/**", "/compte/**", "/role/**")
				.permitAll()
				.antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security",
						"/swagger-ui.html", "/webjars/**", "/swagger-resources/configuration/ui", "/swagge‌​r-ui.html",
						"/swagger-resources/configuration/security")
				.permitAll().anyRequest().authenticated();
		http.addFilterBefore((Filter) authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
	}

}
