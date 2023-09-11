package ICD;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import db.DummyDB;

public class Servidor {

	public final static int DEFAULT_PORT = 5025; // porto onde vai ficar a espera

	// lista de jogadores ligados
	private Map<String, Socket> connected = new HashMap<String, Socket>();
	private ArrayList<Socket> sockets = new ArrayList<>();
	private ArrayList<String> nicknames = new ArrayList<>();
	private Map<String, String> dicOfInvites = new HashMap<String, String>();
	private List<String> lista=null;
	
	private static String usersPasswordsXML = "src/main/webapp/xml/usersAndPasswords/userAndPasswords.xml";
	private String leaderboardXML = "src/main/webapp/xml/leaderBoard/leaderBoard.xml";

	private String usersPasswordsXSD = "src/main/webapp/xml/usersAndPasswords/userAndPasswordsValidation.xsd";
	private String leaderboardXSD = "src/main/webapp/xml/leaderBoard/leaderBoardValidation.xsd";
	private String pathJogadores = "src/main/webapp/txt/jogadores.txt";
	public Servidor() {

		Thread checkPlayers = new checkPlayers(this);
		checkPlayers.start();

		ServerSocket serverSocket = null;
		try {
			
			if (validateXMLDocAgainstXSDDoc(usersPasswordsXML, usersPasswordsXSD))
				System.out.println("No erros validating Users and Passwords Database");
			else
				System.out.println("ERROR ON VALIDATING THE USERS AND PASSWORDS DATABASE");
			if (validateXMLDocAgainstXSDDoc(leaderboardXML, leaderboardXSD))
				System.out.println("No erros validating leaderBoard Database");
			else
				System.out.println("ERROR ON VALIDATING THE USERS AND PASSWORDS DATABASE");

			serverSocket = new ServerSocket(DEFAULT_PORT);
			Socket newSock = null;
			for (;;) {
				System.out.println("Servidor TCP concorrente aguarda ligacao no porto " + DEFAULT_PORT + "...");
				newSock = serverSocket.accept();
				System.out.println("Cliente a conectar-se...");
				Thread threadLogIn = new LoginThread(newSock, this);
				threadLogIn.start();
			}

		} catch (IOException e) {
			System.err.println("Excepcao no servidor: " + e);
		}
	}

	public static void main(String[] args) {
		new Servidor();
	}
	
	public Socket getSocketByKey(String key) {
	    return connected.get(key);
	}
	
	
	public ArrayList<String> PlayersNamesDB() {
	    ArrayList<String> players = new ArrayList<String>();

	    try (BufferedReader reader = new BufferedReader(new FileReader(pathJogadores))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            players.add(line);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return players;
	}
	
	public List<String> getDataPlayers(String query, int nLetters, int nItems) {
		List<String> matched = new ArrayList<String>();
		lista = PlayersNamesDB();
		
		
		
		if(query==null || query.length()<nLetters) // numero minimo de letras
			return matched;
		query = query.toLowerCase();
		for(int i=0; i<lista.size(); i++) {
			String item = lista.get(i).toLowerCase();
			if( item.toLowerCase().startsWith(query)) { 
				matched.add(lista.get(i));
			if( matched.size()>nItems)  // limita o numero de respostas
				break;
			}
		}
		System.out.println(matched);
		return matched;
	}
	
	public String extractUserData1(String name) {
        try {
        	
        	
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(usersPasswordsXML));

            NodeList userList = doc.getElementsByTagName("User");
            for (int i = 0; i < userList.getLength(); i++) {
                Element userElement = (Element) userList.item(i);
                String userName = userElement.getAttribute("nickname");
                if (userName.equals(name)) {
                    String color = userElement.getAttribute("Color");
                    String dateOfBirth = userElement.getAttribute("DateOfBirth");
                    String nationality = userElement.getAttribute("nationality");

                    String profilePic = "";
                    NodeList profilePicList = userElement.getElementsByTagName("ProfilePic");
                    if (profilePicList.getLength() > 0) {
                        Element profilePicElement = (Element) profilePicList.item(0);
                        profilePic = profilePicElement.getTextContent();
                    }

                    return color + "!" + dateOfBirth + "!" + nationality + "!" + profilePic;
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return "";
    }
	
	public String extractUserData2(String nickname) {
	    try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new File(leaderboardXML));

	        NodeList userList = doc.getElementsByTagName("User");
	        for (int i = 0; i < userList.getLength(); i++) {
	            Element userElement = (Element) userList.item(i);
	            String userName = userElement.getAttribute("nickname");
	            if (userName.equals(nickname)) {
	                String wins = userElement.getAttribute("wins");
	                String losses = userElement.getAttribute("losses");
	                String draws = userElement.getAttribute("draw");
	                String timeSpent = userElement.getAttribute("time_spent");

	                return wins + "!" + losses + "!" + draws + "!" + timeSpent;
	            }
	        }
	    } catch (ParserConfigurationException | SAXException | IOException e) {
	        e.printStackTrace();
	    }

	    return "";
	}
	
	public String extractUserColor(String nickname) {
	    try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse(new File(usersPasswordsXML));

	        NodeList userList = doc.getElementsByTagName("User");
	        for (int i = 0; i < userList.getLength(); i++) {
	            Element userElement = (Element) userList.item(i);
	            String userName = userElement.getAttribute("nickname");
	            if (userName.equals(nickname)) {
	                String color = userElement.getAttribute("Color");
	                return color;
	            }
	        }
	    } catch (ParserConfigurationException | SAXException | IOException e) {
	        e.printStackTrace();
	    }

	    return "";
	}
	
	public void addToDictionary(String nicknameThatInvites, String nicknameThatReceivesInvite) {
	    dicOfInvites.put(nicknameThatInvites,nicknameThatReceivesInvite);
	}
	public String getInviterNickname(String inviteeNickname) {
	    for (Map.Entry<String, String> entry : dicOfInvites.entrySet()) {
	        if (entry.getValue().equals(inviteeNickname)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}

	
	public String getInviteeNickname(String inviteeNickname) {
	    for (Map.Entry<String, String> entry : dicOfInvites.entrySet()) {
	        if (entry.getValue().equals(inviteeNickname)) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	public boolean checkInvite(String username) {
	    for (Map.Entry<String, String> entry : dicOfInvites.entrySet()) {
	        String inviter = entry.getKey();
	        String invitee = entry.getValue();
	        
	        if (invitee.equals(username)) {
	            System.out.println("You have been invited by: " + inviter);
	            return true;
	        }
	    }
	    
	    System.out.println("No invitations found for: " + username);
	    return false;
	}
	
	public void printInvites() {
	    for (Map.Entry<String, String> entry : dicOfInvites.entrySet()) {
	        String key = entry.getKey();
	        String value = entry.getValue();
	        System.out.println(key + " -> " + value);
	    }
	}
	
	public String getAllNamesPlayers() {
	    try {
	        // Load the XML file
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document doc = builder.parse("src\\main\\webapp\\xml\\leaderBoard\\leaderBoard.xml");

	        // Create XPath object
	        XPath xpath = XPathFactory.newInstance().newXPath();

	        // Query for nickname attributes
	        String expression = "//User/@nickname";
	        NodeList nicknameNodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

	        // Extract the nicknames
	        StringBuilder namesBuilder = new StringBuilder();
	        for (int i = 0; i < nicknameNodes.getLength(); i++) {
	            Node nicknameNode = nicknameNodes.item(i);
	            String nickname = nicknameNode.getNodeValue();
	            namesBuilder.append(nickname);
	            if (i < nicknameNodes.getLength() - 1) {
	                namesBuilder.append(",");
	            }
	        }

	        return namesBuilder.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}



	public synchronized void adicionarLigacao(String nickname, Socket socket) {
		connected.put(nickname, socket);
		System.out.println("Cliente colocado no connected: " + nickname);
		System.out.println(socket);
	}

	// TODO: nao usada em lado nenhum
	public synchronized boolean removerLigacao(String nickname, Socket socket) {
		return connected.remove(nickname, socket); // True se existe e remove, False caso contrario
	}

	public synchronized boolean addPlayerToWaitingList(String nickname) {
		if (connected.containsKey(nickname)) {
			nicknames.add(nickname);
			sockets.add(connected.get(nickname));
			return true;
		}
		return false;
	}

	public synchronized boolean startPlayerPlaySession(String nickname) {
		if (connected.containsKey(nickname)) { // se jogador está ligado
			nicknames.remove(nickname); // remover o nick dos disponiveis
			sockets.remove(connected.get(nickname)); // remover o socket dos disponiveis
			return true;
		}
		return false;
	}

	public synchronized boolean startTwoPlayerPlaySession(String nick1, String nick2) {
		return startPlayerPlaySession(nick1) && startPlayerPlaySession(nick2);
	}

	public synchronized int getSocketsListLen() {
		return sockets.size();
	}

	public synchronized int getNicknamesListLen() {
		return nicknames.size();
	}

	public synchronized Socket getSocket(int index) {
		if (index > getSocketsListLen())
			return null;
		return sockets.get(index);
	}

	public synchronized String getNickname(int index) {
		if (index > getNicknamesListLen())
			return null;
		return nicknames.get(index);
	}
	
	
	public String getConnectedNicknames() {
	    StringBuilder sb = new StringBuilder();
	    for (String nickname : connected.keySet()) {
	        sb.append(nickname).append(",");
	    }
	    // Remove the trailing comma (",") from the string
	    if (sb.length() > 0) {
	        sb.setLength(sb.length() - 1);
	    }
	    return sb.toString();
	}
	public Socket getSocketFromNickname(String nickname) {
	    return connected.get(nickname);
	}

	public boolean checkUserInXML(String nickname, String password, char type) {
		
	    int count = 0;
		try {
			Document doc = getFileXML(usersPasswordsXML);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("count(//User[@nickname=\""+nickname+"\" and @password=\"" + password + "\"])");
			count = ((Double) expr.evaluate(doc, XPathConstants.NUMBER)).intValue();
			//System.out.println(" ----> " + count + " " + nickname + " encontrado");
		} catch (XPathExpressionException e) { e.printStackTrace(); }
		
		if ( (type == 'R' && count == 1) || (type == 'L' && count == 1) ) return true;
		return false;
	}

	public void addUserToXML(String nickname, String password, String nationality, String dateOfBirth,String color) {
		try {
			Document doc = getFileXML(usersPasswordsXML);
			Element newUser = doc.createElement("User");
			newUser.setAttribute("nickname", nickname);
			newUser.setAttribute("password", password);
			newUser.setAttribute("nationality", nationality);
			newUser.setAttribute("DateOfBirth", dateOfBirth);
			newUser.setAttribute("Color", color);
			Element profilePic = doc.createElement("ProfilePic");
			String pathToFoto = "src\\main\\webapp\\ProfilePhotos\\DefaultPic.jpg";
			byte[] imageData = Files.readAllBytes(Paths.get(pathToFoto));
			String base64Image = Base64.getEncoder().encodeToString(imageData);
			profilePic.setTextContent("data:"+ getMimeType(pathToFoto) +";base64," + base64Image); // <---- mime da foto
			newUser.appendChild(profilePic);
			profilePic.appendChild(doc.createTextNode("\n\t\t"));
			Element root = doc.getDocumentElement();
			root.appendChild(newUser);
			root.appendChild(doc.createTextNode("\n\t"));
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(usersPasswordsXML));
		} catch (IOException | TransformerException e) {
			System.err.println("Error adding user to XML: " + e.getMessage());
		}
	}

	public void updateUserImage(String nickname, String Image) {
		try {
			Document doc = getFileXML(usersPasswordsXML);
			
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//User[@nickname=\""+nickname+"\"]/ProfilePic");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			nodes.item(0).setTextContent(Image);

			/*NodeList userList = doc.getElementsByTagName("User");
			for (int i = 0; i < userList.getLength(); i++) {
				Element userElement = (Element) userList.item(i);
				if (userElement.getAttribute("nickname").equals(nickname)) {
					Element profilePicElement = (Element) userElement.getElementsByTagName("ProfilePic").item(0);
					profilePicElement.setTextContent(Image);
					break;
				}
			}*/

			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(usersPasswordsXML));

		} catch (TransformerException | XPathExpressionException e) {
			System.err.println("Error updating Image to:" + nickname + " " + e.getMessage());
		}
	}

	public static String XPathGetReturnFromDoc(String XpathExpression, String XMLPath) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(XMLPath));

			XPath xpath = XPathFactory.newInstance().newXPath();
			return xpath.evaluate(XpathExpression, doc);

		} catch (Exception e) {
			throw new RuntimeException("Error extracting element from XPath Expression", e);
		}
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

	public void addUserToLeaderBoard(String nickname) {
		try {
			Document doc = getFileXML(leaderboardXML);
			Element newUser = doc.createElement("User");
			newUser.setAttribute("nickname", nickname);
			newUser.setAttribute("wins", "0");
			newUser.setAttribute("losses", "0");
			newUser.setAttribute("draw", "0");
			newUser.setAttribute("time_spent", "00:00:00");
			Element root = doc.getDocumentElement();
			root.appendChild(newUser);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(doc), new StreamResult(leaderboardXML));
		} catch (TransformerException e) {
			System.err.println("Error adding user to XML: " + e.getMessage());
		}
	}

	public boolean validateProtocol(String xml, String xsdPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(xsdPath));
			Validator validator = schema.newValidator();
			Source source = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
			validator.validate(source);
			return true;
		} catch (IOException | SAXException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean validateXMLDocAgainstXSDDoc(String xmlFilePath, String xsdFilePath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(xsdFilePath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlFilePath)));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Document getFileXML(String filePath) {
		File xmlFile = new File(filePath);
		if (!xmlFile.exists()) {
			System.err.println("XML file not found!");
		}
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder;
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Error checking XML: " + filePath.substring(filePath.lastIndexOf("/")) + e.getMessage());
		}
		return null;
	}
	
	
	
	public static final String getMimeType(String path) {
		switch (getExtension(path)) {
		case "txt":
			return "text/plain";
		case "gif":
			return "image/gif";
		case "jpg":
		case "jpeg":
			return "image/jpeg";
		case "bmp":
			return "image/bmp";
		case "png":
			return "image/png";
		default:
			return "application/octet-stream";
		}
	}
	
	public static String getExtension(String path) {
		int dot = path.lastIndexOf(".");
		if(dot==-1)
			return "";
		return path.substring(dot + 1);
	}
	
	
	public String orderLeaderboard(int size) {
		try {

			Document doc = getFileXML("src\\main\\webapp\\xml\\leaderBoard\\leaderBoard.xml");
			NodeList nodeList = doc.getElementsByTagName("User");

			List<User> users = new ArrayList<User>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					User user = new User();
					user.nickname = element.getAttribute("nickname");
					user.wins = Integer.parseInt(element.getAttribute("wins"));
					user.losses = Integer.parseInt(element.getAttribute("losses"));
					user.draw = Integer.parseInt(element.getAttribute("draw"));
					user.time_spent = element.getAttribute("time_spent");
					users.add(user);
				}
				if (size != 0 && users.size() == size) break;
			}
			if (size == 0 || size > users.size()) size = users.size();

			// organizar segundo criterios: +wins, -time, -loss
			Collections.sort(users, new Comparator<User>() {
				public int compare(User u1, User u2) {
					
					int time1 = convertTimeToSeconds(u1.time_spent);
					int time2 = convertTimeToSeconds(u2.time_spent);
					
					if (u1.wins != u2.wins) return u2.wins - u1.wins; // (+wins)
					if (time1 != time2) return time1 - time2;         // (-time_spent)
					return u1.losses - u2.losses; 					  //  (-losses)
				}
			});
			
			String topUsers = " -- LEADERBOARD (Wins/Losses/Draws) [time] --\n";
			
					
			for (int i = 0; i < size; i++) {
				topUsers += (i + 1) + ". " + users.get(i).nickname
						 + "\n   (" + users.get(i).wins + "/" 
						 + users.get(i).losses + "/" 
						 + users.get(i).draw + ") ["
						 + users.get(i).time_spent + "]\n";
			}
			
			return topUsers;
		} catch (Exception e) {}
		return null;
	}
	
	public List<User> orderLeaderboardElements(int size) {
		try {
			
			Document doc = getFileXML("src\\main\\webapp\\xml\\leaderBoard\\leaderBoard.xml");
			NodeList nodeList = doc.getElementsByTagName("User");
			
			List<User> users = new ArrayList<User>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					User user = new User();
					user.nickname = element.getAttribute("nickname");
					user.wins = Integer.parseInt(element.getAttribute("wins"));
					user.losses = Integer.parseInt(element.getAttribute("losses"));
					user.draw = Integer.parseInt(element.getAttribute("draw"));
					user.time_spent = element.getAttribute("time_spent");
					users.add(user);
				}
				if (size != 0 && users.size() == size) break;
			}
			if (size == 0 || size > users.size()) size = users.size();
			
			// organizar segundo criterios: +wins, -time, -loss
			Collections.sort(users, new Comparator<User>() {
				public int compare(User u1, User u2) {
					
					int time1 = convertTimeToSeconds(u1.time_spent);
					int time2 = convertTimeToSeconds(u2.time_spent);
					
					if (u1.wins != u2.wins) return u2.wins - u1.wins; // (+wins)
					if (time1 != time2) return time1 - time2;         // (-time_spent)
					return u1.losses - u2.losses; 					  //  (-losses)
				}
			});
			
			return users;
		} catch (Exception e) {}
		return null;
	}

	private static int convertTimeToSeconds(String time) {
		String[] parts = time.split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		int seconds = Integer.parseInt(parts[2]);
		return hours * 3600 + minutes * 60 + seconds;
	}

	public static class User {
		public String nickname;
		public int wins;
		public int losses;
		public int draw;
		public String time_spent;
	}
	

}
