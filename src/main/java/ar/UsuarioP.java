package ar;

import java.util.Set;

public class UsuarioP {
	public String principal;
	private Set<String> sessions;

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Set<String> getSessions() {
		return sessions;
	}

	public void setSessions(Set<String> sessions) {
		this.sessions = sessions;
	}

}
