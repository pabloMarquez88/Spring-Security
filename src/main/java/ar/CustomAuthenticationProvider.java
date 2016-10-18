package ar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		System.out.println("CustomAuthenticationProvider.authenticate()");
		String name = authentication.getName();
		String password = authentication.getCredentials().toString();

		List<GrantedAuthority> grantedAuths = new ArrayList<>();
		Autority as = new Autority();
		as.setRol("ROLE_SPECIAL_USER");
		grantedAuths.add(as);
		Authentication auth = new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
		return auth;

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}