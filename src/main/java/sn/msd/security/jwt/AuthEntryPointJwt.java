package sn.msd.security.jwt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint{

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthEntryPointJwt.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("Unauthorized errror:{}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error:UnAuthorized");

	}

}
