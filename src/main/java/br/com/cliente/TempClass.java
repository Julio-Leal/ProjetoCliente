//package br.com.cliente;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.Scanner;
//
//import com.google.gson.Gson;
//
//import br.com.model.Mensagem;
////import jdk.internal.misc.FileSystemOption;
////import jdk.internal.org.jline.terminal.TerminalBuilder;
//
//public class TempClass {
//    private static final Scanner INPUT = new Scanner(System.in);
//    private static String ip;
//    private static int porta;
//
//    public static String token = null;
//
//    public static void teste(String[] args) {
//    	try {
//    		informarIPEPorta();
//
//            Socket socket = new Socket(ip, porta);
//            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
//            Gson gson = new Gson();
//            ClienteReceiver receiver = new ClienteReceiver(socket);
//            new Thread(receiver).start();
//
//            System.out.println("[servidor]: Conectado!");
//           
//            String opcaoMenu;
//            
//            do {
//            	System.out.println("==============================");
//                System.out.println("💬 CHAT DE MENSAGENS 🗨️");
//                System.out.println("==============================");
//                System.out.println("[1] - Login");
//                System.out.println("[2] - Cadastrar");
//                System.out.println("[0] - Sair");
//                System.out.println("==============================\n");
//                
//                opcaoMenu = INPUT.nextLine();
//                Mensagem msg = new Mensagem();
//                
//                switch(opcaoMenu) {
//                	case "1":
//                    	msg.setOp("login");
//                    	
//                    	System.out.print("Usuario: ");
//                    	msg.setUsuario(INPUT.nextLine());
//                    	System.out.print("Senha: ");
//                    	msg.setSenha(INPUT.nextLine());
//                    	
//                    	out.println(gson.toJson(msg));
//                    	
//                    	if(token != null) {
//                    		do {
//                    			System.out.println("==============================");
//                                System.out.println("👤 Logado como: "+ token);
//                                System.out.println("==============================");
//                                System.out.println("[1] - Consultar");
//                                System.out.println("[2] - Atualizar");
//                                System.out.println("[3] - Deletar");
//                                System.out.println("[4] - Logout");
//                                System.out.println("[5] - Enviar mensagem");
//                                System.out.println("==============================");
//                                
//                                opcaoMenu = INPUT.nextLine();
//                                msg = null;
//                                
//                                switch(opcaoMenu) {
//                                	case "1":
//                                		msg.setOp("consultarUsuario");
//                                		msg.setToken(token);
//                                		
//                                		out.println(gson.toJson(msg));
//                                		break;
//                                	case "2":
//                                		msg.setOp("atualizarUsuario");
//                                		msg.setToken(token);
//                                        System.out.print("Novo nome: ");
//                                        msg.setNome(INPUT.nextLine());
//                                        System.out.print("Nova Senha: ");
//                                        msg.setSenha(INPUT.nextLine());
//                                        
//                                        out.println(gson.toJson(msg));
//                                        break;
//                                	case "3":
//                                		msg.setOp("deletarUsuario");
//                                		msg.setToken(token);
//                                		
//                                		out.println(gson.toJson(msg));
//                                		
//                                		opcaoMenu = "0";
//                                		break;
//                                	case "4":
//                                		msg.setOp("logout");
//                                		msg.setToken(token);
//                                		
//                                		out.println(gson.toJson(msg));
//                                		
//                                		opcaoMenu = "0";
//                                		break;
//                                	case "5":
//                                		System.out.print("Destinatário: ");
//                                		String dest = INPUT.nextLine();
//                                		System.out.println("Mensagem: ");
//                                		String texto = INPUT.nextLine();
//                                		
//										msg.setOp("enviarMensagem");
//										msg.setToken(token);
//										msg.setDestinatario(dest);
//										msg.setMensagem(texto);
//										
//										out.println(gson.toJson(msg));
//										break;
//									default: 
//										System.out.println("Opção inválida!");
//										break;
//                                }
//                    		} while(!opcaoMenu.equals("0"));
//                    	} 
//                    	
//                		break;
//                	case "2":
//                		msg.setOp("cadastrarUsuario");
//                		
//                		System.out.print("Nome: ");
//                		msg.setNome(INPUT.nextLine());
//                		System.out.println("Usuário: ");
//                		msg.setUsuario(INPUT.nextLine());
//                		System.out.println("Senha: ");
//                		msg.setSenha(INPUT.nextLine());
//                		
//                		out.println(gson.toJson(msg));
//                		break;
//                	case "0":
//                		System.out.println("Encerrando...⌛");
//                		socket.close();
//                		break;
//                	default: 
//                		System.out.println("Opção inválida!");
//                		break;
//                }
//            } while(!opcaoMenu.equals("0"));
//    	} catch(IOException e) {
//    		
//    	}
//    } //FIM MAIN
//           
//
//    //MÉTODOS UTEIS
//    private static void informarIPEPorta() {
//        System.out.print("IP: ");
//        ip = INPUT.nextLine();
//
//        System.out.print("Porta: ");
//        porta = Integer.parseInt(INPUT.nextLine());
//    }
//}
//
//        
//        