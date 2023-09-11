package ICD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ICD.Servidor.User;


public class checkInvite extends Thread {
	private BufferedReader is;
	private PrintWriter os;
	private Socket socket;

	public checkInvite(Socket socket) {
		this.socket = socket;
		try {
			is = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			os = new PrintWriter(this.socket.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("ESTAS NA ESPERA DO INVITE");
		for (;;) {
			try {

				String pedido = is.readLine();
				System.out.println(pedido);

				
			} catch (IOException e1) {
				System.out.println("Desconectado...");
				return;
			}
			
		}
	}
}
