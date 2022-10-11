package sn.msd.security.jwt;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import sn.msd.model.Compte;
import sn.msd.repository.CompteRepository;
import sn.msd.security.service.UserDetailImp;

public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	@Value("${sn.app.jwtSecret}")
	private String jwtSecret;
	@Value("${sn.app.jwtExpirationMs}")
	private int jwtExpiration;
	@Autowired
	CompteRepository compteRepository;
	
	public String generateJwtToken(Authentication authentication) {
		UserDetailImp userDetailImp = (UserDetailImp) authentication.getPrincipal();
		
		List<String> roles = userDetailImp.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		String token =  Jwts.builder().setSubject(userDetailImp.getUsername()).setIssuedAt(new Date())
				.claim("email", userDetailImp.getEmail())
				.claim("roles", roles)
				.setExpiration(new Date((new Date()).getTime() + jwtExpiration))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
		token = token.trim();
		
		updateActiveToken(userDetailImp.getUsername(),token);

		return token;
	}

	public String getUserNameFromJwtToken(String authToken) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody().getSubject();
	}
	public List<String> getPrivilegesFromJwtToken(String authToken) {
		return (List<String>) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody().get("roles");
	}

	public boolean validationJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error(" Invalide Jwt signature :{}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error(" Malfomed Jwt :{}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error(" Expiration Jwt :{}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error(" Unsupported Jwt :{}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error(" Invalide argument :{}", e.getMessage());
		}
		return false;
	}
	
	public Compte updateActiveToken(String username, String token) {
		Compte compteOpt = compteRepository.findByLogin(username);

		if (compteOpt != null) {
			compteOpt.setActiveToken(token);
			Compte compte = compteRepository.save(compteOpt);
			return compte;
		}
		return null;
	}

}
