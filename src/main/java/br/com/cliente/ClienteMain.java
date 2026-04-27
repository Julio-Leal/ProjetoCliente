package br.com.cliente;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import br.com.model.Mensagem;

public class ClienteMain {
	private static Scanner input = new Scanner(System.in);
	private static String ip;
	private static int porta;
	
	public static void main(String[] args) {		
		try {
			informarIPEPorta();
			
			Socket socket = new Socket(ip, porta);
			
			System.out.println("[servidor]: Conectado!");
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			Scanner input = new Scanner(System.in);
			Gson gson = new Gson();
			
			//thread PARA RECEER MENSAGENS
			ClienteReceiver receiver = new ClienteReceiver(socket);
			new Thread(receiver).start();
			
            // =========================
            // MENU INICIAL
            // =========================
			System.out.println("1 - Login");
			System.out.println("2 - Cadastro");
			
			String opcao = input.nextLine();
			
			if(opcao.equals("1")) {
				Mensagem login = new Mensagem();
				login.setOp("login");
				
				System.out.println("Usuário: ");
				login.setUsuario(input.nextLine());
				System.out.println("Senha: ");
				login.setSenha(input.nextLine());
				
				out.println(gson.toJson(login));
			} else if(opcao.equals("2")) {
				Mensagem cadastro = new Mensagem();
				cadastro.setOp("cadastro");
				
				System.out.println("Nome: ");
				cadastro.setNome(input.nextLine());
				System.out.println("Usuário: ");
				cadastro.setUsuario(input.nextLine());
				System.out.println("Senha: ");
				cadastro.setSenha(input.nextLine());
				
				out.println(gson.toJson(cadastro));
				
				System.out.println("Agora faça login...");
				return;
			}
			
            // =========================
            // CHAT
            // =========================
			String token = null;
			
			System.out.println("Digite seu token (retornado pelo servidor):");
			token = input.nextLine();
			
			while(true) {
				
				System.out.println("Destinatário: ");
				String destinatario = input.nextLine();
				
				System.out.println("Mensagem: ");
				String texto = input.nextLine();
				
				if(texto.equalsIgnoreCase("/sair")) {
					break;
				}
				
				Mensagem msg = new Mensagem();
				msg.setOp("enviarMensagem");
				msg.setToken(token);
				msg.setDestinatario(destinatario);
				msg.setMensagem(texto);
				
				out.println(gson.toJson(msg));
			
			}
			
		} catch(Exception e) {
			System.out.println("❌ - Erro ao conectar: ");
			e.printStackTrace();
		}
	
	}
	
	private static void informarIPEPorta() {
		System.out.println("[]: Informe o IP do servidor: ");
		ip = input.nextLine();
			
		System.out.println("[]: Informe a Porta do servidor: ");
		porta = input.nextInt();
	}
}
