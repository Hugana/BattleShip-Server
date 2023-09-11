package ICD;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jakarta.servlet.http.Part;

public class Jogador {

	private final static String DEFAULT_HOST = "localhost"; // "pfenvy"; //"194.210.190.133"; // Maquina onde reside o
															// servidor
	private final static int DEFAULT_PORT = 5025; // porto onde o servidor esta a espera

	private static String nickname;
	private static String password;
	private static boolean isInLogin;
	private static boolean isInWait;
	private static String XMLreply;
	private static String xsdPath = "src\\main\\webapp\\xml\\protocol\\ProtocolFromServerToClient.xsd";

	Socket socket = null;
	BufferedReader is = null;
	PrintWriter os = null;

	public Jogador() throws UnknownHostException, IOException {
		socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);

		// Mostrar os parametros da liga��o
		System.out.println("Liga��o: " + socket);

		// Stream para escrita no socket
		os = new PrintWriter(socket.getOutputStream(), true);

		// Stream para leitura do socket
		is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	public void Write(String Mensagem) {
		os.println(Mensagem);
	}

	public String ReedMessage() throws IOException {
		return is.readLine();
	}

	public String tabuleiro(String newline) throws IOException {

		return is.readLine().replaceAll("\7", newline);
	}

	public void jogar(short jogada) {

		os.println(jogada);
	}

	public void turnoff() {
		isInLogin = true;
	}

	public List<String> getQueryForAllPlayers(String query) throws IOException, ClassNotFoundException {

		os.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<getPlayers query=\"" + query
				+ "\"/></protocol>");

		ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
		String[] jogadoresArray = (String[]) inputStream.readObject();

		List<String> jogadores = new ArrayList<>(Arrays.asList(jogadoresArray));

		List<String> lista = jogadores;

		return lista;

	}

	public boolean changePictureServlet() throws IOException {
		os.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<changePicture/></protocol>");

		String XMLreply = is.readLine();

		if (XMLreply.equals("pedido concedido")) {
			return true;
		}
		return false;

	}

	public static void main(String[] args) {

		Socket socket = null;
		BufferedReader is = null;
		PrintWriter os = null;

		try {
			Jogador JW = new Jogador();
			try (Scanner in = new Scanner(System.in)) {

				// ============================== FOR: LOGIN ==============================

				while (!isInLogin) {

					System.out.println("Welcome!");
					System.out.println("1 - Login");
					System.out.println("2 - Resgister");
					System.out.println("3 - Leave");
					System.out.println("4 - See Rankings");
					System.out.println("Select your option: ");

					String decision = in.next();

					switch (decision) {
					// --------------- LOGIN ---------------
					case "1":
						System.out.println("To register,enter your nickname and password:");
						System.out.print("Nickname: ");
						System.out.print("");
						nickname = in.next();
						System.out.print("Password:");
						password = in.next();

						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><login nickname=\"" + nickname
								+ "\" password=\"" + password + "\"/></protocol>");

						XMLreply = JW.ReedMessage();

						if (JW.validateProtocol(XMLreply, xsdPath)) {
							String reply = JW.XPathGetReturn(XMLreply, "//protocol/*/message/text()");
							System.out.println(reply);
							AnswerReply(reply);
						} else {
							System.out.println("Error Validating the XML");
						}
						break;

					// --------------- REGISTER ---------------
					case "2":
						System.out.println("To register,enter your nickname and password:");
						System.out.println("Nickname: ");
						String nickname = in.next();
						System.out.println("Password:");
						String password = in.next();
						System.out.println("Nationality: ");
						String nationality = in.next();
						System.out.println("Date of Birth (DD/MM/YY):");
						String dateOfBirth = in.next();
						System.out.println("Color (Hexadicimal):");
						String color = in.next();

						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><protocol><register nickname=\"" + nickname
								+ "\" password=\"" + password + "\" nationality=\"" + nationality + "\" dateOfBirth=\""
								+ dateOfBirth + "\" color=\"" + color + "\"/></protocol>");

						XMLreply = JW.ReedMessage();
						System.out.println(XMLreply);

						if (JW.validateProtocol(XMLreply, xsdPath)) {
							String reply = JW.XPathGetReturn(XMLreply, "//protocol/*/message/text()");
							System.out.println(reply);
							AnswerReply(reply);
						} else {
							System.out.println("Error Validating the XML");
						}

						break;

					// --------------- LEAVE ---------------
					case "3":
						break;

					// --------------- RANKINGS ---------------
					case "4":
						System.out.println("How many users do you want to see? (0 to show all)");
						System.out.println("Number of users to show: ");
						String top = in.next();

						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<leaderBoard top=\""
								+ top + "\"/></protocol>");

						XMLreply = JW.ReedMessage();
						System.out.println(XMLreply);

						String rank = JW.ReedMessage();
						System.out.println(rank.replaceAll("\7", "\n"));
						break;
					}
				}

				// ============================== FOR: ESPERA ==============================
				// Thread checkInvite = new checkInvite(socket);
				// checkInvite.start();

				while (!isInWait) {

					System.out.println("Your in the Menu!!!");
					System.out.println("Choose 1 to play");
					System.out.println("Choose 2 to see someone's Info");
					System.out.println("Choose 3 to change your profile picture");
					System.out.println("Choose 4 to see the Rankings");
					System.out.println("Choose 5 to check for invites");
					System.out.println("Choose 6 to see all the users connected");
					System.out.println("Choose 7 to send and invite to play");

					String decision = in.next();

					switch (decision) {

					// --------------- JOGAR ---------------
					case "1":
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<requestToPlay/></protocol>");
						String XMLreply = JW.ReedMessage();
						System.out.println(XMLreply);
						isInWait = true;
						break;

					// --------------- VER PERIL ---------------
					// TODO
					case "2":
						System.out.println("Type the profile you want to see:");
						String nick = in.next();
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<getPlayerInfo nickname=\"" + nick + "\"/></protocol>");

						// String XMLreply1 = JW.ReedMessage();

						// System.out.println(XMLreply);

						String color = JW.ReedMessage();
						String dateOfBirth = JW.ReedMessage();
						String nationality = JW.ReedMessage();
						String wins = JW.ReedMessage();
						String losses = JW.ReedMessage();
						String draws = JW.ReedMessage();
						String timeSpent = JW.ReedMessage();
						String imageBase64 = JW.ReedMessage();

						System.out.println("this is the color: " + color);
						System.out.println("this is the Date of Birth: " + dateOfBirth);
						System.out.println("this is the nationality " + nationality);
						System.out.println("this is the wins: " + wins);
						System.out.println("this is the losses: " + losses);
						System.out.println("this is the draws: " + draws);
						System.out.println("this is the timeSpent: " + timeSpent);
						break;

					// --------------- CHANGE PF PIC ---------------
					case "3":
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<changePicture/></protocol>");

						String XMLreply11 = JW.ReedMessage();

						if (XMLreply11.equals("pedido concedido")) {

							System.out.println("Choose the picture with the following path");
							System.out.println("src\\main\\webapp\\ProfilePhotos\\");
							String pathToFoto = "src\\main\\webapp\\ProfilePhotos\\" + in.next();

							byte[] imageData = Files.readAllBytes(Paths.get(pathToFoto));
							String base64Image = Base64.getEncoder().encodeToString(imageData);
							// os.println(base64Image);
							JW.Write("data:" + Servidor.getMimeType(pathToFoto) + ";base64," + base64Image);
						}

						break;

					// --------------- SEE RANKINGS ---------------
					// TODO
					case "4":
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<seeRankings/></protocol>");

						XMLreply11 = JW.ReedMessage();

						if (XMLreply11.equals("pedido concedido")) {
							System.out.println("How many users do you want to see? (0 to show all)");
							System.out.println("Number of users to show: ");
							String top = in.next();

							JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<leaderBoard top=\""
									+ top + "\"/></protocol>");

							XMLreply11 = JW.ReedMessage();
							// System.out.println(XMLreply11);

							String rank = JW.ReedMessage();

							// System.out.println(rank);
							System.out.println(rank.replaceAll("\7", "\n"));
						}
						break;

					// --------------- VER PERFIL DE OUTRO ---------------
					// TODO
					case "5":
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<checkInvite/></protocol>");

						String XMLreply111 = JW.ReedMessage();

						if (XMLreply111.equals("Received an Invite")) {
							System.out.println("You received an invite");
							System.out.println("Choose y or n to accept or decline");
							String resposta = in.next();
							JW.Write(resposta);

							String respostayn = JW.ReedMessage();
							if (respostayn.equals("y")) {
								isInWait = true;
								System.out.println("Invite accepted");
							} else if (respostayn.equals("n")) {
								System.out.println("Invite declined");
							}
						} else {
							System.out.println(XMLreply111);
						}

						break;

					case "6":
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
								+ "<listOnlinePlayers/></protocol>");
						String XMLreply1111 = JW.ReedMessage();
						if (XMLreply1111.equals("pedido concedido")) {

							String onlinePlayers = JW.ReedMessage();

							System.out.println(onlinePlayers);
						}
						break;

					case "7":
						System.out.println("Chosse who you want to play with:");
						String nickForInvite = in.next();
						JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>" + "<sendInvite nickname=\""
								+ nickForInvite + "\"/></protocol>");
						System.out.println("Waiting for a response");

						String yn = JW.ReedMessage();
						if (yn != null) {
							if (yn.equals("y")) {
								isInWait = true;
								System.out.println("Received answer: " + yn);
								JW.Write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<protocol>"
										+ "<toggleConnect/></protocol>");
							} else if (yn.equals("n")) {

								System.out.println("Received answer: " + yn);
							}
						}

						break;

					}
				}

				// ============================== FOR: JOGO ==============================
				for (;;) {
					String boardString = JW.ReedMessage();

					if (boardString == null) {
						System.out.println("The other player left you are the winner!!");
						isInWait = false;
						break;
					}

					else if (boardString != null && boardString.equals("Defeat") || boardString.equals("Victory")) {
						System.out.println(boardString);
						isInWait = false;
						break;
					} else {
						char[][] b = parseBoardProtocol(boardString);

						QuatroEmLinha.showboard(b);

						System.out.println("Coloque a sua coluna");
						JW.jogar(in.nextShort());
					}

					// System.out.println(JW.tabuleiro("\n"));

				}
				isInWait = false;
			}

		} catch (

		IOException e) {
			System.err.println("Erro na ligacao " + e.getMessage());
		} finally {
			// No fim de tudo, fechar os streams e o socket
			try {
				if (os != null)
					os.close();
				if (is != null)
					is.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				// if an I/O error occurs when closing this socket
			}
		} // end finally
	} // end main

	private static void AnswerReply(String reply) {

		switch (reply) {

		// --------------- login ---------------
		case "Login Valid":
			isInLogin = true;
			break;

		case "Login Invalid":
			System.out.println("Login invalid.");
			System.out.println("Password or Nickname is wrong");
			break;

		// --------------- registo ---------------
		case "Register Valid":
			System.out.println("Registration complete.");
			System.out.println("You can now login!");
			break;

		case "Register Invalid":
			System.out.println("Registration imcomplete.");
			System.out.println("That nickname has already been taken");
			break;

		}
	}

	public static char[][] parseBoardProtocol(String protocol) {
		List<char[]> rows = new ArrayList<>();

		int startIndex = protocol.indexOf("<sendBoard>") + "<sendBoard>".length();
		int endIndex = protocol.indexOf("</sendBoard>");

		if (startIndex != -1 && endIndex != -1) {
			String boardContent = protocol.substring(startIndex, endIndex);

			int rowStartIndex = 0;
			while (true) {
				int rowEndIndex = boardContent.indexOf("</row", rowStartIndex);
				if (rowEndIndex == -1) {
					break;
				}

				String rowContent = boardContent.substring(rowStartIndex, rowEndIndex);
				List<Character> row = new ArrayList<>();

				int colStartIndex = 0;
				while (true) {
					int colEndIndex = rowContent.indexOf("</col", colStartIndex);
					if (colEndIndex == -1) {
						break;
					}

					String colContent = rowContent.substring(colStartIndex, colEndIndex);
					int valueStartIndex = colContent.lastIndexOf('>') + 1;
					char value = colContent.charAt(valueStartIndex);
					row.add(value);

					colStartIndex = colEndIndex + "</col".length();
				}

				char[] rowArray = new char[row.size()];
				for (int i = 0; i < row.size(); i++) {
					rowArray[i] = row.get(i);
				}
				rows.add(rowArray);

				rowStartIndex = rowEndIndex + "</row".length();
			}
		}

		char[][] board = new char[rows.size()][];
		for (int i = 0; i < rows.size(); i++) {
			board[i] = rows.get(i);
		}

		return board;
	}

	public char[][] parseBoardProtocolJSP(String protocol) {
		List<char[]> rows = new ArrayList<>();
		if (protocol == null) {
			return new char[8][8];
		}
		int startIndex = protocol.indexOf("<sendBoard>") + "<sendBoard>".length();
		int endIndex = protocol.indexOf("</sendBoard>");

		if (startIndex != -1 && endIndex != -1) {
			String boardContent = protocol.substring(startIndex, endIndex);

			int rowStartIndex = 0;
			while (true) {
				int rowEndIndex = boardContent.indexOf("</row", rowStartIndex);
				if (rowEndIndex == -1) {
					break;
				}

				String rowContent = boardContent.substring(rowStartIndex, rowEndIndex);
				List<Character> row = new ArrayList<>();

				int colStartIndex = 0;
				while (true) {
					int colEndIndex = rowContent.indexOf("</col", colStartIndex);
					if (colEndIndex == -1) {
						break;
					}

					String colContent = rowContent.substring(colStartIndex, colEndIndex);
					int valueStartIndex = colContent.lastIndexOf('>') + 1;
					char value = colContent.charAt(valueStartIndex);
					row.add(value);

					colStartIndex = colEndIndex + "</col".length();
				}

				char[] rowArray = new char[row.size()];
				for (int i = 0; i < row.size(); i++) {
					rowArray[i] = row.get(i);
				}
				rows.add(rowArray);

				rowStartIndex = rowEndIndex + "</row".length();
			}
		}

		char[][] board = new char[rows.size()][];
		for (int i = 0; i < rows.size(); i++) {
			board[i] = rows.get(i);
		}

		return board;
	}

	public String XPathGetReturn(String XMLlogin, String XpathExpression) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(XMLlogin)));

			XPath xpath = XPathFactory.newInstance().newXPath();
			return xpath.evaluate(XpathExpression, doc);

		} catch (Exception e) {
			throw new RuntimeException("Error extracting element form XPath Expression", e);
		}
	}

	public String XPathGetReturn2(String XMLlogin, String XpathExpression) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(XMLlogin)));

			XPath xpath = XPathFactory.newInstance().newXPath();
			return xpath.evaluate(XpathExpression, doc);

		} catch (Exception e) {
			throw new RuntimeException("Error extracting element form XPath Expression", e);
		}
	}

	public boolean validateProtocol(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory
					.newSchema(new StreamSource("src\\main\\webapp\\xml\\protocol\\ProtocolFromServerToClient.xsd"));
			Validator validator = schema.newValidator();
			Source source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			validator.validate(source);
			return true;
		} catch (IOException | SAXException e) {
			e.printStackTrace();
			return false;
		}
	}
}

// end ClienteTCP
