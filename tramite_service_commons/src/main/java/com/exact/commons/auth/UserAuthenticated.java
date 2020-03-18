package com.exact.commons.auth;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthenticated implements Principal {
	
	private String id;
	private String nombres;
	private String correo;
	
	
	
	@Override
	public String getName() {
		return id;
	}
	
	

}
