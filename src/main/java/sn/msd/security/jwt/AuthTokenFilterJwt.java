package sn.msd.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import sn.msd.security.service.UserDetailServiceImpl;

public class AuthTokenFilterJwt extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserDetailServiceImpl userDetailsService;
	@Value("${sn.client.headerKey}")
	private String headerKey;
	@Value("${sn.client.tokenType}")
	private String tokenType;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validationJwtToken(jwt)) {
				String username = jwtUtils.getUserNameFromJwtToken(jwt);

//				List<String> privileges = jwtUtils.getPrivilegesFromJwtToken(jwt);
//				
//				Collection<GrantedAuthority> authorities = new ArrayList<>();
//				privileges.forEach(rn -> {
//	                authorities.add(new SimpleGrantedAuthority(rn));
//	            });

				UserDetails userDetails = userDetailsService.loadUserByUsernameAndActiveToken(username, jwt);

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}

		} catch (Exception e) {
			logger.error("Can't set user.authentificationToken {}", e);
		}
		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(headerKey);
		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(tokenType)) {
			return headerAuth.trim().substring(6, headerAuth.length());
		}
		return null;

	}

}
