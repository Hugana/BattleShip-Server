package ICD;

public class checkPlayers extends Thread {

	private Servidor server;

	public checkPlayers(Servidor server) {
		this.server = server;
	}

	public void run() {
		for (;;) {
			//System.out.println(server.getsocketslistlen());
			if (server.getSocketsListLen() == 2) {
				System.out.println("yes");
				new HandleConnectionThread(this.server,server.getSocket(0), server.getSocket(1), server.getNickname(0),server.getNickname(1)).start();
				server.startTwoPlayerPlaySession(server.getNickname(0),server.getNickname(1));
			}
		}
	}
	
	
}
