package ICD;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import java.time.*;

class HandleConnectionThread extends Thread {

	private Socket connection1;
	private Socket connection2;
	private String nickname1;
	private String nickname2;

	// tempo individual
	private Duration timeJogador1, timeJogador2;
	private Servidor server;

	public HandleConnectionThread(Servidor server,Socket connection1, Socket connection2, String nickname1, String nickname2) {
		this.connection1 = connection1;
		this.connection2 = connection2;
		this.nickname1 = nickname1;
		this.nickname2 = nickname2;
		this.timeJogador1 = Duration.ZERO;
		this.timeJogador2 = Duration.ZERO;
		this.server = server;
	}

	public void run() {

		BufferedReader is1 = null;
		PrintWriter os1 = null;

		BufferedReader is2 = null;
		PrintWriter os2 = null;

		try {
			System.out.println("Game Thread: " + this.getId() + ", " + connection1.getRemoteSocketAddress() + ", "
					+ connection2.getRemoteSocketAddress());

			is1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));

			os1 = new PrintWriter(connection1.getOutputStream(), true);

			is2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));

			os2 = new PrintWriter(connection2.getOutputStream(), true);

			int NCOLS = 8;
			int NROWS = 8;

			char[][] board = new char[NROWS][NCOLS];

			Duration tempoJogadaJogador = Duration.ZERO;
			Instant jogada1 = null;
			Instant jogada2 = Instant.now();

			connection1.setSoTimeout(30000);
			connection2.setSoTimeout(30000);

			for (;;) {

				// inicio contagem

				os1.println(createBoardProtocol(board));

				try {
					String inputLine1 = is1.readLine();

					int intValue1 = Integer.parseInt(inputLine1);

					if (inputLine1 == null) {
						System.out.println("Socket timeout occurred for " + nickname1);
						break;
					}

					System.out.println("Recebi do " + nickname1 + " a coluna: " + inputLine1);

					QuatroEmLinha.play('A', board, intValue1);

					// atualizar tempo de jogada
					jogada1 = Instant.now();
					tempoJogadaJogador = Duration.between(jogada2, jogada1);
					timeJogador1 = timeJogador1.plus(tempoJogadaJogador);
					// os1.println("Tempo de jogada A: " + milliesFormat(tempoJogadaJogador));
					System.out.println("Tempo de jogada A: " + milliesFormat(tempoJogadaJogador));
					System.out.println("Tempo jogador A: " + milliesFormat(timeJogador1));

					QuatroEmLinha.showboard(board);

					if (QuatroEmLinha.lastPlayerWon(board, intValue1)) {
						os1.println("Victory");
						os2.println("Defeat");
						os1.println("Tempo de jogo " + timeJogador1);
						os2.println("Tempo de jogo " + timeJogador2);
						// System.out.println("Time Played of the game - " + this.getId() + "->" +
						// timeJogador1.toMillis());
						upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname1);
						break;

					}
					if (!QuatroEmLinha.existsFreePlaces(board)) {
						os1.println("Empate");
						os2.println("Empate");
						os1.println("Tempo de jogo " + timeJogador1);
						os2.println("Tempo de jogo " + timeJogador2);
						// System.out.println("Time Played of the game - " + this.getId() + "->" +
						// timeElapsed.toMillis());
						upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname1);
						break;
					}

				} catch (SocketTimeoutException e) {
					System.out.println("Socket timeout occurred for " + nickname1);
					upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname2);
					break;
				}

				os2.println(createBoardProtocol(board));

				try {
					String inputLine2 = is2.readLine();

					int intValue2 = Integer.parseInt(inputLine2);

					if (inputLine2 == null) {
						System.out.println("Socket timeout occurred for " + nickname2);
						break;
					}

					System.out.println("Recebi do " + nickname2 + " a coluna: " + inputLine2);

					QuatroEmLinha.play('B', board, intValue2);

					// atualizar tempo de jogada
					jogada2 = Instant.now();
					tempoJogadaJogador = Duration.between(jogada1, jogada2);
					timeJogador2 = timeJogador2.plus(tempoJogadaJogador);
					// os2.println("Tempo de jogada B: " + milliesFormat(tempoJogadaJogador));
					System.out.println("Tempo de jogada B: " + milliesFormat(tempoJogadaJogador));
					System.out.println("Tempo jogador B: " + milliesFormat(timeJogador2));

					QuatroEmLinha.showboard(board);

					if (QuatroEmLinha.lastPlayerWon(board, intValue2)) {
						os1.println("Defeat");
						os2.println("Victory");
						os1.println("Tempo de jogo " + timeJogador1);
						os2.println("Tempo de jogo " + timeJogador2);
						// System.out.println("Time Played of the game - " + this.getId() + "->" +
						// timeElapsed.toMillis());
						upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname2);
						break;

					}
					if (!QuatroEmLinha.existsFreePlaces(board)) {
						os1.println("Empate");
						os2.println("Empate");
						os1.println("Tempo de jogo " + timeJogador1);
						os2.println("Tempo de jogo " + timeJogador2);
						// System.out.println("Time Played of the game - " + this.getId() + "->" +
						// timeElapsed.toMillis());
						upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname1);
						break;
					}

				} catch (SocketTimeoutException e) {
					System.out.println("Socket timeout occurred for " + nickname2);
					upDateLeaderBoard(false, timeJogador1, timeJogador2, nickname1, nickname2, nickname1);
					break;
				}

			}

		} catch (IOException e) {
			System.err.println("Erro na ligacao : " + e.getMessage());
		} finally {
			// garantir que o socket � fechado
			try {
				if (is1 != null)
					is1.close();
				if (os1 != null)
					os1.close();
				if (connection1 != null)
					connection1.close();
				if (is2 != null)
					is2.close();
				if (os2 != null)
					os2.close();
				if (connection2 != null)
					connection2.close();
			} catch (IOException e) {
			}
		}

	}

	public static String createBoardProtocol(char[][] board) {
		StringBuilder protocol = new StringBuilder();
		protocol.append("<protocol>");
		protocol.append("<sendBoard>");

		int rows = board.length;
		int cols = board[0].length;

		for (int row = 0; row < rows; row++) {
			protocol.append("<row").append(row + 1).append(">");
			for (int col = 0; col < cols; col++) {
				protocol.append("<col").append(col + 1).append(">");
				protocol.append(board[row][col]);
				protocol.append("</col").append(col + 1).append(">");
			}
			protocol.append("</row").append(row + 1).append(">");
		}

		protocol.append("</sendBoard>");
		protocol.append("</protocol>");

		return protocol.toString();
	}

	private static void upDateLeaderBoard(boolean isDraw, Duration timeJogador1, Duration timeJogador2,
			String nickname1, String nickname2, String winner) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			File xmlFile = new File("src/main/webapp/xml/leaderBoard/leaderBoard.xml");
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			XPath xpath = XPathFactory.newInstance().newXPath();

			Element user1 = (Element) xpath.evaluate("/leaderboard/User[@nickname='" + nickname1 + "']", doc,
					XPathConstants.NODE);
			Element user2 = (Element) xpath.evaluate("/leaderboard/User[@nickname='" + nickname2 + "']", doc,
					XPathConstants.NODE);

			// DRAWS, LOSSES, WINS
			if (isDraw) {
				int draws1 = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname1 + "']/@draws", doc));
				int draws2 = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname2 + "']/@draws", doc));
				user1.setAttribute("draws", String.valueOf(draws1 + 1));
				user2.setAttribute("draws", String.valueOf(draws2 + 1));

			} else if (nickname1.equals(winner)) {
				int losses = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname2 + "']/@losses", doc));
				int wins = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname1 + "']/@wins", doc));
				user1.setAttribute("wins", String.valueOf(wins + 1));
				user2.setAttribute("losses", String.valueOf(losses + 1));

			} else if (nickname2.equals(winner)) {
				int losses = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname1 + "']/@losses", doc));
				int wins = Integer
						.parseInt(xpath.evaluate("/leaderboard/User[@nickname='" + nickname2 + "']/@wins", doc));
				user2.setAttribute("wins", String.valueOf(wins + 1));
				user1.setAttribute("losses", String.valueOf(losses + 1));
			}

			// TEMPO
			String time1 = (xpath.evaluate("/leaderboard/User[@nickname='" + nickname1 + "']/@time_spent", doc));
			String time2 = (xpath.evaluate("/leaderboard/User[@nickname='" + nickname2 + "']/@time_spent", doc));

			Duration duration1 = Duration.ofMillis(timeToMillis(time1) + timeJogador1.toMillis());
			Duration duration2 = Duration.ofMillis(timeToMillis(time2) + timeJogador2.toMillis());

			user1.setAttribute("time_spent", milliesFormat(duration1));
			user2.setAttribute("time_spent", milliesFormat(duration2));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);

			// Create a copy of the leaderboard XML file
			File copyFile = new File("src/main/webapp/xml/leaderBoard/leaderboardcopy.xml");
			Files.copy(xmlFile.toPath(), copyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException
				| XPathExpressionException e) {
			System.err.println("Error adding user to XML: " + e.getMessage());
		}
	}

	private static String milliesFormat(Duration duration) {
		return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
	}

	private static long timeToMillis(String timeString) {

		String[] parts = timeString.split(":");

		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);

		long millis = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);

		return millis;
	}

}