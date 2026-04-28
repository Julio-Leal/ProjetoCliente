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

    public static String token;

    public static void main(String[] args) {

        try {
            informarIPEPorta();

            Socket socket = new Socket(ip, porta);

            
            System.out.println("[servidor]: Conectado!");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Gson gson = new Gson();

            ClienteReceiver receiver = new ClienteReceiver(socket);
            new Thread(receiver).start();

            while (true) {

                System.out.println("\n========== MENU ==========");
                System.out.println("1 - Login");
                System.out.println("2 - Cadastrar");
                System.out.println("3 - Consultar");
                System.out.println("4 - Atualizar");
                System.out.println("5 - Deletar");
                System.out.println("6 - Logout");
                System.out.println("7 - Enviar mensagem\n");

                String opcao = input.nextLine();

                Mensagem msg = new Mensagem();

                switch (opcao) {

                    case "1":
                        msg.setOp("login");

                        System.out.print("Usuário: ");
                        msg.setUsuario(input.nextLine());

                        System.out.print("Senha: ");
                        msg.setSenha(input.nextLine());

                        out.println(gson.toJson(msg));
                        break;

                    case "2":
                        msg.setOp("cadastrarUsuario");

                        System.out.print("Nome: ");
                        msg.setNome(input.nextLine());

                        System.out.print("Usuário: ");
                        msg.setUsuario(input.nextLine());

                        System.out.print("Senha: ");
                        msg.setSenha(input.nextLine());

                        out.println(gson.toJson(msg));
                        break;

                    case "3":
                        msg.setOp("consultarUsuario");
                        msg.setToken(token);
                        out.println(gson.toJson(msg));
                        break;

                    case "4":
                        msg.setOp("atualizarUsuario");
                        msg.setToken(token);

                        System.out.print("Novo nome: ");
                        msg.setNovoNome(input.nextLine());
                        
                        msg.setNovoUsuario(input.nextLine());
                        
                        System.out.print("Nova senha: ");
                        msg.setNovaSenha(input.nextLine());

                        out.println(gson.toJson(msg));
                        break;

                    case "5":
                        msg.setOp("deletarUsuario");
                        msg.setToken(token);
                        out.println(gson.toJson(msg));
                        break;

                    case "6":
                        msg.setOp("logout");
                        msg.setToken(token);
                        out.println(gson.toJson(msg));
                        token = null;
                        break;

                    case "7":

                        System.out.print("Destinatário: ");
                        String dest = input.nextLine();

                        System.out.print("Mensagem: ");
                        String texto = input.nextLine();

                        msg.setOp("enviarMensagem");
                        msg.setToken(token);
                        msg.setDestinatario(dest);
                        msg.setMensagem(texto);

                        out.println(gson.toJson(msg));
                        break;
                   default:
                	   System.out.println("Opção inválida!");
                	   break;
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao conectar");
            e.printStackTrace();
        }
    }

    private static void informarIPEPorta() {

        System.out.print("IP: ");
        ip = input.nextLine();

        System.out.print("Porta: ");
        porta = Integer.parseInt(input.nextLine());
    }
}
