package br.com.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClienteReceiver implements Runnable{
	private Socket socket;

	public ClienteReceiver(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream())
			);
			
			String mensagem;
			
			while((mensagem = in.readLine()) != null) {
				System.out.println(mensagem);
			}
			
		} catch (Exception e) {
			System.out.println("Conexão encerrada.");
			e.printStackTrace();
		}
	}
}
