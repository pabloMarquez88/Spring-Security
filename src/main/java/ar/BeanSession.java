package ar;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component("beanSession")
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class BeanSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2141179190748554370L;

	private String usuario;
	private String password;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}