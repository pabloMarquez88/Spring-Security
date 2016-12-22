package ar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GestorPermisosDistribuido {

	private final static ConcurrentMap<Object, Set<String>> principals = new ConcurrentHashMap<Object, Set<String>>();

	public GestorPermisosDistribuido() {
	}

	public static ConcurrentMap<Object, Set<String>> getPrincipals() {
		System.out.println("GET PRINCIPALS");
//		return principals;
		return SqlHelper.recuperarPrincipal();
	}

	public static List<Object> getPrincipalsList() {
		System.out.println("GET PRINCIPALS LIST");
		return  new ArrayList<Object>(SqlHelper.recuperarPrincipal().keySet());
//		return new ArrayList<Object>(principals.keySet());
	}

	public static Set<String> getPrincipal(Object principal) {
		System.out.println("GET PRINCIPAL");
//		return principals.get(principal);
		return SqlHelper.getPrincipal(principal.toString()).getSessions();
	}

	/**
	 * DEBERIA SER SINCRONIZADO POR QUE NO TENGO LA ATOMICIDAD DEL HASHMAP
	 * 
	 * @param principal
	 * @param sessionsUsedByPrincipal
	 * @return
	 */
	public static synchronized Set<String> storePrincipalIfAbsent(Object principal, Set<String> sessionsUsedByPrincipal) {
		System.out.println("STORE PRINCIPAL IF ABSENT");
		// return principals.putIfAbsent(principal,sessionsUsedByPrincipal);
		UsuarioP princi = SqlHelper.getPrincipal(principal.toString());
		if (princi.getPrincipal() == null) {
			return SqlHelper.guardarPrincipalSession(principal.toString(), sessionsUsedByPrincipal);
//			return principals.put(principal, sessionsUsedByPrincipal);
		} else {
			return princi.getSessions();
		}
	}

	public static void removePrincipal(Object principal) {
		System.out.println("REMOVE PRINCIPAL");
//		principals.remove(principal);
		SqlHelper.borrarPrincipal(principal.toString());

	}

}
