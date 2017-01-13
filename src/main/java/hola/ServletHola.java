package hola;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletHola
 */
public class ServletHola extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletHola() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final Enumeration<String> nomes = request.getParameterNames();
		String salida = "\n";
		while (nomes.hasMoreElements()) {
			final String nomeDoParametro = nomes.nextElement();
			System.out.println(nomeDoParametro + " " + request.getParameter(nomeDoParametro));
			salida = salida + nomeDoParametro + " " + request.getParameter(nomeDoParametro) + "\n";
		}
		response.getWriter().append("GET Served at: ").append(request.getContextPath()).append(salida);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final Enumeration<String> nomes = request.getParameterNames();
		String salida = "\n";
		while (nomes.hasMoreElements()) {
			final String nomeDoParametro = nomes.nextElement();
			System.out.println(nomeDoParametro + " " + request.getParameter(nomeDoParametro));
			salida = salida + nomeDoParametro + " " + request.getParameter(nomeDoParametro) + "\n";
		}
		
		response.getWriter().append("POST Served at: ").append(request.getContextPath()).append(salida);
	}

}
