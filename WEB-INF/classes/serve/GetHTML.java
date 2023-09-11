package serve;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import ICD.Jogador;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class GetDiv
 */
@WebServlet("/GetHTML")
public class GetHTML extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GetHTML() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();

		Jogador JW = (Jogador) session.getAttribute("Jogador");

		// Set the response message's MIME type.
		response.setContentType("text/html;charset=ISO-8859-1");
		// Allocate a output writer to write the response message into the network
		// socket.
		PrintWriter out = response.getWriter();
		String query = request.getParameter("query");
		String tag = request.getParameter("tag");
		String nlt = request.getParameter("nlt");
		String nit = request.getParameter("nit");
		if (query == null)
			query = "";
		if (tag == null)
			tag = "option";
		int l = 0;
		if (nlt != null)
			l = Integer.parseInt(nlt);
		int i = 10;
		if (nit != null)
			i = Integer.parseInt(nit);

		/// AQUI E PARA FAZER JW.GETDATA para returnar a lista"
		// List<String> items = db.getData(query,l , i);
		List<String> items = null;
		try {
			items = JW.getQueryForAllPlayers(query);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<String> iterator = items.iterator();
		while (iterator.hasNext())
			if (tag.compareToIgnoreCase("br") == 0)
				out.write((String) iterator.next() + "<br>");
			else
				out.write("<" + tag + ">" + (String) iterator.next() + "</" + tag + ">");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
