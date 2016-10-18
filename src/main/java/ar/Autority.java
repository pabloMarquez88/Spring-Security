package ar;

import org.springframework.security.core.GrantedAuthority;

public class Autority implements GrantedAuthority{

	private String rol;
	
	
	
	public String getRol() {
		return rol;
	}



	public void setRol(String rol) {
		this.rol = rol;
	}



	@Override
	public String getAuthority() {
		return rol;
	}

}
