package ar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SqlHelper {

	private static final String CREATE_PRINCIPALS_TABLE = "CREATE TABLE usuarios (id INTEGER IDENTITY PRIMARY KEY,name VARCHAR(300))";

	private static final String CREATE_SESSION_TABLE = "CREATE TABLE sesiones (id INTEGER IDENTITY PRIMARY KEY,value VARCHAR(300), id_principal varchar(300))";
	private static final String DELETE_PRINCIPAL_TABLE = "DROP TABLE usuarios";
	private static final String DELETE_SESSION_TABLE = "DROP TABLE sesiones";

	private static final String INSERTAR_SESSION = "INSERT INTO sesiones (value,id_principal) values (?,?)";
	private static final String INSERTAR_PRINCIPAL = "INSERT INTO usuarios (name) values (?)";
	private static final String DELETE_PRINCIPAL = "DELETE FROM usuarios WHERE name = ?";
	private static final String DELETE_SESSION = "DELETE FROM sesiones where id_principal = ?";
	private static final String DELETE_SESSION_BY_SESSION = "DELETE FROM sesiones where value = ?";
	private static final String BUSCAR_PRINCIPAL = "SELECT name FROM usuarios WHERE name = ?";
	private static final String BUSCAR_SESSION = "SELECT value FROM sesiones WHERE id_principal = ?";
	private static final String TRAER_PRINCIPALS = "SELECT name from usuarios";

	static {
		try {
//			System.out.println("CREANDO TABLAS INICIO");
//			Connection conexion = getConnection();
//			conexion.createStatement().execute(DELETE_PRINCIPAL_TABLE);
//			conexion.createStatement().execute(DELETE_SESSION_TABLE);
//			conexion.createStatement().execute(CREATE_PRINCIPALS_TABLE);
//			conexion.createStatement().execute(CREATE_SESSION_TABLE);
//			
//			conexion.close();
//			System.out.println("CREANDO TABLAS FIN");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  Set<String> guardarPrincipalSession(String principal, Set<String> sessions){
		guardarPrincipal(principal);
		Iterator<String> iter = sessions.iterator();
		while (iter.hasNext()){
			String s = iter.next();
			guardarSession(principal, s);
		}
		return sessions;
	}
	
	public static void guardarSession(String principal, String session) {
		System.out.println("GUARDAR SESSION INICIO");
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(INSERTAR_SESSION);
			comando.setString(1, session);
			comando.setString(2, principal);
			comando.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("GUARDAR SESSION FIN");
	}

	public static void guardarPrincipal(String principal) {
		System.out.println("GUARDAR PRINCIPAL INICIO");
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(INSERTAR_PRINCIPAL);
			comando.setString(1, principal);
			comando.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("GUARDAR PRINCIPAL FIN");
	}

	public static void borrarPrincipal(String principal) {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(DELETE_PRINCIPAL);
			comando.setString(1, principal);
			comando.execute();
			borrarSession(principal);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void borrarSession(String principal) {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(DELETE_SESSION);
			comando.setString(1, principal);
			comando.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void borrarSessionBySession(String session) {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(DELETE_SESSION_BY_SESSION);
			comando.setString(1, session);
			comando.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Set<String> recuperarSessiones(String principal){
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(BUSCAR_SESSION);
			comando.setString(1, principal);
			ResultSet rs = comando.executeQuery();
			Set<String> sal = new HashSet<String>();
			while (rs.next()) {
				sal.add(rs.getString(1));
			}
			if (sal.size()>0){
				return sal;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static UsuarioP getPrincipal(String principal) {
		System.out.println("GET PRINCIPAL BY PRINCIPAL INICIO");
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(BUSCAR_PRINCIPAL);
			comando.setString(1, principal);
			ResultSet rs = comando.executeQuery();
			String salida = "";
			while (rs.next()) {
				salida = rs.getString(1);
			}
			UsuarioP usuario = new UsuarioP();
			Set<String> sal = recuperarSessiones(principal);
			usuario.setSessions(sal);
			System.out.println("GET PRINCIPAL BY PRINCIPAL FIN");
			return usuario;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static ConcurrentMap<Object, Set<String>> recuperarPrincipal() {
		Connection con = null;
		try {
			con = getConnection();
			PreparedStatement comando = con.prepareStatement(TRAER_PRINCIPALS);
			ResultSet rs = comando.executeQuery();
			List<Object> salida = new ArrayList<Object>();
			while (rs.next()) {
				salida.add(rs.getString(1));
			}
			ConcurrentMap<Object, Set<String>> sal = new ConcurrentHashMap<Object, Set<String>>();
			for (Object s : salida){
				Set<String> sesiones = recuperarSessiones(s.toString());
				sal.put(s, sesiones);
			}
			return sal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static Connection getConnection() {
		try {
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		Connection connection = null;
		try {

			connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/");
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]) {
		System.out.println(getConnection());
		guardarPrincipal("PABLO");
	}

	public SqlHelper() {
		// TODO Auto-generated constructor stub
	}

}
