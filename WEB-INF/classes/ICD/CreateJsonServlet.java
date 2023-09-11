package ICD;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


import java.io.IOException;

import java.io.PrintWriter;


import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;

@WebServlet(urlPatterns = "/CreateJsonServlet")
public class CreateJsonServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String XMLreply;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession();

        if (request.getMethod().equalsIgnoreCase("post")) {
            Jogador JW = (Jogador) session.getAttribute("Jogador");

            JW.Write("Get all players");

            try {
                XMLreply = JW.ReedMessage();

                if (XMLreply.equals("pedido concedido")) {

                    String allNames = JW.ReedMessage();
                    String[] names = allNames.split(",");

                    JSONArray jsonArray = new JSONArray();
                    for (String name : names) {
                        jsonArray.put(name);
                    }

                    String namesArray = jsonArray.toString();  // Convert the JSONArray to a string

                    PrintWriter out = response.getWriter();
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Search with Autocomplete</title>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<h1>Search with Autocomplete</h1>");
                    out.println("<input type=\"text\" id=\"searchInput\" placeholder=\"Search...\" autocomplete=\"off\">");
                    out.println("<ul id=\"autocompleteList\"></ul>");
                    out.println("<button onclick=\"goToJsp()\">Go to JSP</button>");
                    out.println("<script>");
                    out.println("const namesArray = " + namesArray + ";");
                    out.println("const searchInput = document.getElementById('searchInput');");
                    out.println("const autocompleteList = document.getElementById('autocompleteList');");
                    out.println("function filterData(searchTerm) {");
                    out.println("    return namesArray.filter(item => item.toLowerCase().includes(searchTerm.toLowerCase()));");
                    out.println("}");
                    out.println("function showSuggestions(event) {");
                    out.println("    const searchTerm = event.target.value;");
                    out.println("    const filteredData = filterData(searchTerm);");
                    out.println("    autocompleteList.innerHTML = '';");
                    out.println("    filteredData.forEach(item => {");
                    out.println("        const li = document.createElement('li');");
                    out.println("        li.textContent = item;");
                    out.println("        autocompleteList.appendChild(li);");
                    out.println("    });");
                    out.println("}");
                    out.println("function goToJsp() {");
                    out.println("    const searchTerm = searchInput.value;");
                    out.println("    window.location.href = \"otherPlayerProfile.jsp?name=\" + encodeURIComponent(searchTerm);");
                    out.println("}");
                    out.println("searchInput.addEventListener('input', showSuggestions);");
                    out.println("</script>");
                    out.println("</body>");
                    out.println("</html>");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
