package com.exact.commons.auth;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthenticated implements Principal {
	
	private String id;
	private String username;
	private String nombre;
	private String correo;
	private String perfilId;
	
	
	
	@Override
	public String getName() {
		return id;
	}
	
	

}
