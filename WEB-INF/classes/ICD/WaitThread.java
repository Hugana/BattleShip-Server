package ICD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ICD.Servidor.User;

public class WaitThread extends Thread {

	private Socket playerConnection;
	private BufferedReader is;
	private PrintWriter os;

	private boolean conectou;
	private Servidor server;
	private String nickname;
	private String usersPasswordsXML = "src/main/webapp/xml/usersAndPasswords/userAndPasswords.xml";

	public WaitThread(Socket playerConnection, Servidor server, String nickname) {
		this.playerConnection = playerConnection;
		this.nickname = nickname;

		this.server = server;
		try {
			is = new BufferedReader(new InputStreamReader(this.playerConnection.getInputStream()));
			os = new PrintWriter(this.playerConnection.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("ESTE E O NICK NA WAIT_" + nickname);
		// System.out.println("ESTE E O server NA WAIT_" + server);
		while (!conectou) {
			try {

				String pedido = is.readLine();
				System.out.println(pedido);

				requestAnswer(pedido);

				

			} catch (IOException e1) {
				System.out.println("Desconectado...");
				return;
			}
		}

	}

	public void requestAnswer(String request) throws IOException {

		String protocolType = server.XPathGetReturn(request, "name(/protocol/*[1])");
		System.out.println("ISTO E O PROTOCOLO NO MENU " + protocolType);
		switch (protocolType) {

		case "getPlayers":

			System.out.println("ESTRASTE AQUI SEU RABO");

			String query = server.XPathGetReturn(request, "//getPlayers/@query");
			System.out.println("ISTO E A QUERY " + query);
			List<String> jogadores = server.getDataPlayers(query, 1, 10);

			String[] jogadoresArray = jogadores.toArray(new String[0]);

			ObjectOutputStream outputStream = new ObjectOutputStream(playerConnection.getOutputStream());
			outputStream.writeObject(jogadoresArray);

			break;

		case "changePicture":

			System.out.println("ChangePicture");

			os.println("pedido concedido");

			String image = is.readLine();
			// System.out.println("ISTO E O TAMANHO: " +
			// image.substring(image.indexOf(',')).length());
			server.updateUserImage(nickname, image);

			break;

		case "requestToPlay":

			server.addPlayerToWaitingList(nickname);
			System.out.println("numero de sockers para jogar = " + server.getSocketsListLen());
			System.out.println("numero de nicknames para jogar = " + server.getNicknamesListLen());
			if (server.getSocketsListLen() >= 2) {
				os.println("You are going to play");
				conectou = true;

			} else if (server.getNicknamesListLen() < 2) {
				os.println("There inst to Players yet to play");
				conectou = true;
			}

			break;

		case "listOnlinePlayers":
			os.println("pedido concedido");
			os.println(server.getConnectedNicknames());
			break;

		case "sendInvite":
			String nicknameForInvite = server.XPathGetReturn(request, "//sendInvite/@nickname");
			server.addToDictionary(nickname, nicknameForInvite);
			server.printInvites();
			break;

		case "checkInvite":
			if (server.checkInvite(nickname)) {
				os.println("Received an Invite");
				String resposta = is.readLine();
				if (resposta.equals("y")) {
					conectou = true;
					String inviter = server.getInviteeNickname(nickname);
					System.out.println("ISTO E O INVITER" + playerConnection + " e isto � o nick" + nickname);
					Socket inviterSocket = server.getSocketByKey(inviter);
					PrintWriter osInviter = new PrintWriter(inviterSocket.getOutputStream(), true);
					osInviter.println("y");
					os.println("y");
					new HandleConnectionThread(this.server,playerConnection, inviterSocket, nickname, inviter).start();
					server.startTwoPlayerPlaySession(nickname, inviter);
				} else if (resposta.equals("n")) {
					String inviter = server.getInviteeNickname(nickname);
					Socket inviterSocket = server.getSocketByKey(inviter);
					PrintWriter osInviter = new PrintWriter(inviterSocket.getOutputStream(), true);
					osInviter.println("n");
					os.println("n");
				}

			}
			else {
				os.println("No invite received");
			}
			break;

		case "toggleConnect":
			conectou = true;
			break;

		case "getPlayerInfo":

			String nickForInfo = server.XPathGetReturn(request, "//getPlayerInfo/@nickname");

			String data1 = server.extractUserData1(nickForInfo);

			String data2 = server.extractUserData2(nickForInfo);

			String[] userDataArray1 = data1.split("!");
			String[] userDataArray2 = data2.split("!");

			String color = userDataArray1[0];
			String dateOfBirth = userDataArray1[1];
			String nationality = userDataArray1[2];
			String imageBase64 = userDataArray1[3];
			String wins = userDataArray2[0];
			String losses = userDataArray2[1];
			String draws = userDataArray2[2];
			String timeSpent = userDataArray2[3];

			os.println(color);
			os.println(dateOfBirth);
			os.println(nationality);
			os.println(wins);
			os.println(losses);
			os.println(draws);
			os.println(timeSpent);
			os.println(imageBase64);

			/*
			 * String xmlString = "<protocol>" + "<success type=\"getPlayerInfo\">" +
			 * "<data color=\"" + color + "\" dateOfBirth=\"" + dateOfBirth +
			 * "\" nationality=\"" + nationality + "\" wins=\"" + wins + "\" losses=\"" +
			 * losses + "\" draws=\"" + draws + "\" timeSpent=\"" + timeSpent + "\">" +
			 * "<ProfilePicture>" + imageBase64 + "</ProfilePicture>" + "</data>" +
			 * "</success>" + "</protocol>";
			 * 
			 * os.println(xmlString);
			 */

			break;

		case "getPlayerInfoJSP":
			String nickForInfo1 = server.XPathGetReturn(request, "//getPlayerInfoJSP/@nickname");
			System.out.println(nickForInfo1);
			String data11 = server.extractUserData1(nickForInfo1);

			String data21 = server.extractUserData2(nickForInfo1);

			String[] userDataArray11 = data11.split("!");
			String[] userDataArray21 = data21.split("!");

			String color1 = userDataArray11[0];
			String dateOfBirth1 = userDataArray11[1];
			String nationality1 = userDataArray11[2];
			String imageBase641 = userDataArray11[3];
			String wins1 = userDataArray21[0];
			String losses1 = userDataArray21[1];
			String draws1 = userDataArray21[2];
			String timeSpent1 = userDataArray21[3];

			os.println(color1);
			os.println(dateOfBirth1);
			os.println(nationality1);
			os.println(wins1);
			os.println(losses1);
			os.println(draws1);
			os.println(timeSpent1);
			os.println(imageBase641);

			break;

		case "seeRankings":

			os.println("pedido concedido");

			String showing = server.XPathGetReturn(is.readLine(), "//leaderBoard/@top");
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
			
		case "getColor":
			String nickForColor = server.XPathGetReturn(request, "//getColor/@nickname");
			System.out.println("ISTO E O NICK PARA A COR " + nickForColor);
			String colorToSet = server.extractUserColor(nickForColor);
			os.println(colorToSet);
			System.out.println("ISTO E A COR " + colorToSet);
			break;
			

		}

	}

	public void setConectou(boolean value) {
		this.conectou = value;
	}

}
