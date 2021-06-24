package com.exact.commons.auth;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthenticated implements Principal {
	
	public UserAuthenticated(String id, String nombre) {
		this.id = id;
		this.nombre = nombre;
	}
	
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
