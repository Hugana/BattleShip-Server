package ICD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.xml.xpath.XPathExpressionException;

import ICD.Servidor.User;

public class LoginThread extends Thread {

	private Servidor server;
	private boolean isConnected;

	private Socket playerConnection;
	private BufferedReader is;
	private PrintWriter os;
	private String nickname;
	private String password;
	private String nationality;
	private String DateOfBirth;
	private String Color;
	private String xsdPath = "src\\main\\webapp\\xml\\protocol\\protocolValidation.xsd";

	public LoginThread(Socket playerConnection, Servidor server) {
		this.playerConnection = playerConnection;

		this.server = server;
		try {
			is = new BufferedReader(new InputStreamReader(playerConnection.getInputStream()));
			os = new PrintWriter(playerConnection.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		while (!isConnected) {
			try {
				String request = is.readLine();

				if (server.validateProtocol(request, xsdPath)) {
					requestAnswer(request);
				} else {
					os.println("Invalid xml format!");
				}
			} catch (XPathExpressionException e) {
				os.println("Error getting protocol type");
				e.printStackTrace();
			} catch (IOException e1) {
				System.out.println("Desconectado...");
				return;
			}
		}
		server.adicionarLigacao(nickname, playerConnection);
		new WaitThread(playerConnection, server, nickname).start();
	}

	private void requestAnswer(String request) throws XPathExpressionException {

		String protocolType = server.XPathGetReturn(request, "name(/protocol/*[1])");
		System.out.println("ISTO E O PROTOCOLO NO LOGIN " + protocolType);
		switch (protocolType) {

		// --------------- LOGIN ---------------
		case "login":
			

			nickname = server.XPathGetReturn(request, "//login/@nickname");
			password = server.XPathGetReturn(request, "//login/@password");
			if (server.checkUserInXML(nickname, password, 'L')) {
				isConnected = true;
				os.println("<protocol>" + "<success type=\"login\">" + "<data nickname=\"" + nickname + "\" password=\""
						+ password + "\"/>" + "<message>Login Valid</message>" + "</success>" + "</protocol>");

			} else {
				System.out.println("NO LOGIN");
				os.println("<protocol>" + "<error type=\"login\">" + "<data nickname=\"" + nickname + "\" password=\""
						+ password + "\"/>" + "<message>Login Invalid</message>" + "</error>" + "</protocol>");
			}
			break;

		// --------------- REGISTER ---------------
		case "register":
			nickname = server.XPathGetReturn(request, "//register/@nickname");
			password = server.XPathGetReturn(request, "//register/@password");
			nationality = server.XPathGetReturn(request, "//register/@nationality");
			DateOfBirth = server.XPathGetReturn(request, "//register/@dateOfBirth");
			Color = server.XPathGetReturn(request, "//register/@color");

			if (server.checkUserInXML(nickname, password, 'R')) {
				System.out.println("NO REGISTER");
				os.println("<protocol>" + "<success type=\"register\">" + "<data nickname=\"" + nickname
						+ "\" password=\"" + password + "\" nationality=\"" + nationality + "\" DateOfBirth=\""
						+ DateOfBirth + "\" Color=\"" + Color + "\"/>" + "<message>Register Invalid</message>"
						+ "</success>" + "</protocol>");

			} else {
				server.addUserToXML(nickname, password, nationality, DateOfBirth, Color);
				server.addUserToLeaderBoard(nickname);

				System.out.println("REGISTER");
				os.println("<protocol>" + "<error type=\"register\">" + "<data nickname=\"" + nickname
						+ "\" password=\"" + password + "\" nationality=\"" + nationality + "\" DateOfBirth=\""
						+ DateOfBirth + "\"/>" + "<message>Register Valid</message>" + "</error>" + "</protocol>");
			}
			break;

		// --------------- LEADERBOARD ---------------
		case "leaderBoard":

			System.out.println(request);

			String showing = server.XPathGetReturn(request, "//leaderBoard/@top");
			String topUsers = server.orderLeaderboard(Integer.parseInt(showing));
			String userList = "";
			for (User user : server.orderLeaderboardElements(Integer.parseInt(showing))) {
				userList += "<user @nickname=\"" + user.nickname + "\"/>";
			}

			if (topUsers == null) {

				os.println("<protocol>" + "<error type=\"leaderboard\">" + "<message>Leaderboard Invalid</message>"
						+ "</error>" + "</protocol>");

			} else {

				os.println("<protocol>" + "<success type=\"leaderboard\"><data>" + userList + "</data></success>"
						+ "</protocol>");

				os.println(topUsers.replaceAll("\n", "\7"));
			}

			break;
		}
	}

}