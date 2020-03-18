package com.exact.commons.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

import com.exact.commons.auth.UserAuthenticated;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
	
	private String key;
	Properties properties;
	
	private AntPathMatcher pathMatcher = new AntPathMatcher();

	
	public JWTAuthorizationFilter(AuthenticationManager authenticationManager) throws IOException {
		super(authenticationManager);
		properties = new Properties();
		InputStream input = new FileInputStream("./secret.properties");
		properties.load(input);
		key = properties.getProperty("jwt.key");
	}
	
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return pathMatcher.match("/h2-console*", request.getServletPath()); 
	}


	@SuppressWarnings("unchecked")
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String header = request.getHeader("Authorization");

		if (header == null) {
			return;
		}

		String token = header.replace("Bearer ", "");

		if (token == null) {
			return;
		}

		Claims claims = null;

		try {
			claims = Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException eje) {
			response.setStatus(894);
			response.sendError(894, "EL TOKEN ENVIADO HA EXPIRADO");
			return;
		} catch (MalformedJwtException mje) {
			response.setStatus(498);
			response.sendError(498, "EL TOKEN ENVIADO ES INVÁLIDO");
			return;
		} catch (SignatureException se) {
			response.setStatus(498);
			response.sendError(498, "EL TOKEN ENVIADO ES INVÁLIDO");
			return;
		}

		UsernamePasswordAuthenticationToken authentication = null;
		String usuarioId = claims.get("id").toString();
		String nombres = claims.get("nombre").toString();
		String correo = claims.get("correo").toString();
		
		UserAuthenticated userAuthenticated = new UserAuthenticated(usuarioId, nombres, correo);
		
		Object authoritiesJson = claims.get("authorities");
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		
		((ArrayList<String>) authoritiesJson).forEach(authority -> {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
			authorities.add(grantedAuthority);
		});
		
		authentication = new UsernamePasswordAuthenticationToken(userAuthenticated, null, authorities);		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		super.doFilterInternal(request, response, chain);
	}

}
