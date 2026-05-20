 package br.com.cliente;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;

import br.com.model.Mensagem;
//import jdk.internal.misc.FileSystemOption;
//import jdk.internal.org.jline.terminal.TerminalBuilder;

public class ClienteMain {
    private static final Scanner INPUT = new Scanner(System.in);
    private static String ip;
    private static int porta;

    public static volatile String token = null;

    public static void main(String[] args) {
    	try {
    		informarIPEPorta();

            Socket socket = new Socket(ip, porta);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Gson gson = new Gson();
            ClienteReceiver receiver = new ClienteReceiver(socket);
            new Thread(receiver).start();

            System.out.println("[servidor]: Conectado!");
           
            String opcaoMenu = null;
            
            do {
            	Mensagem msg = new Mensagem();

        		System.out.println("==============================");
                System.out.println("💬 CHAT DE MENSAGENS 🗨️");
                System.out.println("==============================");
                System.out.println("[1] - Login");
                System.out.println("[2] - Cadastrar");
                System.out.println("[3] - Consultar");
                System.out.println("[4] - Atualizar");
                System.out.println("[5] - Deletar");
                System.out.println("[6] - Logout");
                System.out.println("[7] - Enviar mensagem");
//                System.out.println("==============================");
//                System.out.println("💬 Adm Menu 🗨️");
//                System.out.println("==============================");
//                System.out.println("[8] - ");
                System.out.println("[0] - Sair");
                System.out.println("==============================\n");
                
                opcaoMenu = INPUT.nextLine();
                
                switch(opcaoMenu) {
                	case "1":
                    	msg.setOp("login");
                    	
                    	System.out.print("Usuario: ");
                    	msg.setUsuario(INPUT.nextLine());
                    	System.out.print("Senha: ");
                    	msg.setSenha(INPUT.nextLine());
                    	
                    	System.out.println("[Enviando]: "+gson.toJson(msg));
                    	out.println(gson.toJson(msg));
                    	break;
                	case "2":
                		msg.setOp("cadastrarUsuario");
                		
                		System.out.print("Nome: ");
                		msg.setNome(INPUT.nextLine());
                		System.out.println("Usuário: ");
                		msg.setUsuario(INPUT.nextLine());
                		System.out.println("Senha: ");
                		msg.setSenha(INPUT.nextLine());
                		
                		System.out.println("[Enviando]: "+gson.toJson(msg));
                		out.println(gson.toJson(msg));
                		break;
                	case "3":
                		msg.setOp("consultarUsuario");
                		
                		//update to test at room 
                		System.out.println("Informe o token que deseja consultar:");
                		token = INPUT.nextLine();
                		msg.setToken(token);
                		//update to test at room
                		
                		System.out.println("[Enviando]: "+gson.toJson(msg));
                		out.println(gson.toJson(msg));
                		break;
                	case "4":
                		msg.setOp("atualizarUsuario");
                		msg.setToken(token);
                        System.out.print("Novo nome: ");
                        msg.setNome(INPUT.nextLine());
                        System.out.print("Nova Senha: ");
                        msg.setSenha(INPUT.nextLine());
                        
                        System.out.println("[Enviando]: "+gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;
                	case "5":
                		msg.setOp("deletarUsuario");
                		msg.setToken(token);
                		
                		System.out.println("[Enviando]: "+gson.toJson(msg));
                		out.println(gson.toJson(msg));
                		
                		token = null;
                		break;
                	case "6":
                		msg.setOp("logout");
                		msg.setToken(token);
                		
                		System.out.println("[Enviando]: "+gson.toJson(msg));
                		out.println(gson.toJson(msg));
                		
                		token = null;
                		break;
                	case "7":
                		System.out.print("Destinatário: ");
                		String dest = INPUT.nextLine();
                		System.out.println("Mensagem: ");
                		String texto = INPUT.nextLine();
                		
						msg.setOp("enviarMensagem");
						msg.setToken(token);
						msg.setDestinatario(dest);
						msg.setMensagem(texto);
						
						System.out.println("[Enviando]: "+gson.toJson(msg));
						out.println(gson.toJson(msg));
						break;
                	case "0":
                		System.out.println("Encerrando...⌛");
                		socket.close();
                		break;
                	default: 
                		System.out.println("Opção inválida!");
                		break;
                }
            	
            } while(!opcaoMenu.equals("0"));
    	} catch(IOException e) {
    		token = null;
    	}
    } //FIM MAIN
           

    //MÉTODOS UTEIS
    private static void informarIPEPorta() {
        System.out.print("IP: ");
        ip = INPUT.nextLine();

        System.out.print("Porta: ");
        porta = Integer.parseInt(INPUT.nextLine());
    }
}

        
        