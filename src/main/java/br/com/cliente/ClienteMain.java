package br.com.cliente;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;
import br.com.model.Mensagem;

public class ClienteMain {
    private static final Scanner INPUT = new Scanner(System.in);
    private static String ip;
    private static int porta;

    // Token gerenciado dinamicamente após o login com sucesso
    public static volatile String token = null;

    public static void main(String[] args) {
        try {
            informarIPEPorta();

            Socket socket = new Socket(ip, porta);
            // Configuração explícita de Auto-flush e envio em formato UTF-8
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Gson gson = new Gson();
            
            ClienteReceiver receiver = new ClienteReceiver(socket);
            new Thread(receiver).start();

            System.out.println("[Servidor]: Conectado com sucesso!");
           
            String opcaoMenu = null;
            
            do {
                Mensagem msg = new Mensagem();

                System.out.println("\n==============================");
                System.out.println("💬 CHAT DE MENSAGENS — EP2 🗨️");
                System.out.println("==============================");
                if (token != null) {
                    System.out.println("Sessão Ativa | Token: " + token);
                } else {
                    System.out.println("Nenhum usuário autenticado");
                }
                System.out.println("------------------------------");
                System.out.println("[1] - Login");
                System.out.println("[2] - Cadastrar Novo Usuário");
                System.out.println("[3] - Consultar Dados de Cadastro");
                System.out.println("[4] - Atualizar Dados de Cadastro");
                System.out.println("[5] - Deletar Usuário");
                System.out.println("[6] - Logout");
                System.out.println("[7] - Enviar Mensagem Direta (Chat)");
                System.out.println("[0] - Sair");
                System.out.println("==============================\n");
                System.out.print("Escolha uma opção: ");
                
                opcaoMenu = INPUT.nextLine();
                
                switch(opcaoMenu) {
                    case "1":
                        msg.setOp("login");
                        System.out.print("Usuário: ");
                        msg.setUsuario(INPUT.nextLine());
                        System.out.print("Senha: ");
                        msg.setSenha(INPUT.nextLine());
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "2":
                        msg.setOp("cadastrarUsuario");
                        System.out.print("Nome Completo: ");
                        msg.setNome(INPUT.nextLine());
                        System.out.print("Nome de Usuário (Login): ");
                        msg.setUsuario(INPUT.nextLine());
                        System.out.print("Senha (6 dígitos numéricos): ");
                        msg.setSenha(INPUT.nextLine());
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "3":
                        msg.setOp("consultarUsuario");
                        msg.setToken(token);
                        
                        // SE FOR ADM PERMITE LER O CADASTRO DE TERCEIROS
                        if ("adm".equals(token)) {
                            System.out.print("Informe o login do usuário que deseja consultar (ou deixe em branco para o Admin): ");
                            String alvo = INPUT.nextLine();
                            if (!alvo.trim().isEmpty()) {
                                msg.setUsuario(alvo);
                            }
                        }
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "4":
                        msg.setOp("atualizarUsuario");
                        msg.setToken(token);
                        
                        //Se for Admin, permite indicar qual conta será editada
                        if ("adm".equals(token)) {
                            System.out.print("Informe o login do usuário que deseja atualizar: ");
                            String alvo = INPUT.nextLine();
                            msg.setUsuario(alvo);
                        }
                        
                        System.out.print("Novo Nome: ");
                        msg.setNome(INPUT.nextLine());
                        System.out.print("Nova Senha (6 dígitos numéricos): ");
                        msg.setSenha(INPUT.nextLine());
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "5":
                        msg.setOp("deletarUsuario");
                        msg.setToken(token);
                        
                        //Se for Admin, permite indicar qual conta será excluída
                        if ("adm".equals(token)) {
                            System.out.print("Informe o login do usuário que deseja deletar: ");
                            String alvo = INPUT.nextLine();
                            msg.setUsuario(alvo);
                        }
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        
                        // Se não for ADM, a própria conta foi apagada, removemos o token localmente
                        if (!"adm".equals(token)) {
                            token = null;
                        }
                        break;

                    case "6":
                        msg.setOp("logout");
                        msg.setToken(token);
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        token = null;
                        break;

                    case "7":
                        msg.setOp("enviarMensagem");
                        msg.setToken(token);
                        System.out.print("Destinatário (Login): ");
                        msg.setDestinatario(INPUT.nextLine());
                        System.out.print("Mensagem: ");
                        msg.setMensagem(INPUT.nextLine());
                        
                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "0":
                        System.out.println("Encerrando conexão...⌛");
                        socket.close();
                        break;

                    default: 
                        System.out.println("Opção inválida!");
                        break;
                }
                
                // Pequena pausa técnica para sincronizar a resposta textual do Receiver no terminal
                try { Thread.sleep(300); } catch (InterruptedException e) { }
                
            } while(!opcaoMenu.equals("0"));

        } catch(IOException e) {
            System.out.println("[Erro]: Falha na comunicação com o servidor: " + e.getMessage());
            token = null;
        }
    }

    private static void informarIPEPorta() {
        System.out.print("Digite o IP do Servidor: ");
        ip = INPUT.nextLine();

        while (true) {
            System.out.print("Digite a Porta do Servidor: ");
            try {
                porta = Integer.parseInt(INPUT.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número de porta válido.");
            }
        }
    }
}