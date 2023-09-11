package ICD;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.io.IOException;

@WebServlet("/handle-invite-response")
public class HandleInviteResponseServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inviteResponse = request.getParameter("inviteResponse");
        Jogador JW = (Jogador) request.getSession().getAttribute("Jogador");

        if (inviteResponse.equals("yes")) {
            JW.Write("y");
            JW.ReedMessage();
            response.sendRedirect("play.jsp");
        } else if (inviteResponse.equals("no")) {
            JW.Write("n");
            JW.ReedMessage();
            response.sendRedirect("menu.jsp");
            
        }
    }
}
