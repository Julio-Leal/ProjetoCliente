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
                System.out.println("[3] - Consultar Meus Dados");
                System.out.println("[4] - Atualizar Meus Dados");
                System.out.println("[5] - Deletar Minha Conta");
                System.out.println("[6] - Logout");
                System.out.println("[7] - Enviar Mensagem Direta (Chat)");
                System.out.println("--- Operações ADM ---");
                System.out.println("[8] - [ADM] Consultar Todos Usuários");
                System.out.println("[9] - [ADM] Consultar Usuário Específico");
                System.out.println("[10] - [ADM] Atualizar Cadastro de Usuário");
                System.out.println("[11] - [ADM] Deletar Usuário");
                System.out.println("[0] - Sair");
                System.out.println("==============================\n");
                System.out.print("Escolha uma opção: ");

                opcaoMenu = INPUT.nextLine();

                switch (opcaoMenu) {
                    case "1": // Login
                        msg.setOp("login");
                        System.out.print("Usuário: ");
                        msg.setUsuario(INPUT.nextLine());
                        System.out.print("Senha: ");
                        msg.setSenha(INPUT.nextLine());

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "2": // Cadastrar
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

                    case "3": // Consultar meus dados
                        msg.setOp("consultarUsuario");
                        msg.setToken(token);

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "4": // Atualizar meus dados (usuário comum)
                        msg.setOp("atualizarUsuario");
                        msg.setToken(token);

                        System.out.print("Novo Nome (deixe em branco para não alterar): ");
                        String novoNome = INPUT.nextLine();
                        if (!novoNome.trim().isEmpty()) {
                            msg.setNome(novoNome);
                        } else {
                        	novoNome = "";
                        	msg.setNome(novoNome);
                        }
                        System.out.print("Nova Senha (6 dígitos numéricos, deixe em branco para não alterar): ");
                        String novaSenha = INPUT.nextLine();
                        if (!novaSenha.trim().isEmpty()) {
                            msg.setSenha(novaSenha);
                        } else {
                        	novaSenha = "";
                        	msg.setSenha(novaSenha);
                        }

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "5": // Deletar minha conta (usuário comum)
                        msg.setOp("deletarUsuario");
                        msg.setToken(token);

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        token = null;
                        break;

                    case "6": // Logout
                        msg.setOp("logout");
                        msg.setToken(token);

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        token = null;
                        break;

                    case "7": // Enviar mensagem direta
                        msg.setOp("enviarMensagem");
                        msg.setToken(token);
                        System.out.print("Destinatário (Login): ");
                        msg.setDestinatario(INPUT.nextLine());
                        System.out.print("Mensagem: ");
                        msg.setMensagem(INPUT.nextLine());

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "8": // ADM - Consultar todos os usuários
                        msg.setOp("consultarUsuariosAdmin");
//                        msg.setToken_admin(token);
                        msg.setToken(token);

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "9": // ADM - Consultar usuário específico
                        msg.setOp("consultarUsuarioAdmin");
//                        msg.setToken_admin(token);
                        msg.setToken(token);
                        System.out.print("Login do usuário a consultar: ");
                        msg.setUsuario(INPUT.nextLine());

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "10": // ADM - Atualizar dados de outro usuário
                        msg.setOp("atualizarUsuarioAdmin");
//                        msg.setToken_admin(token);
                        msg.setToken(token);
                        System.out.print("Login do usuário a atualizar: ");
                        msg.setUsuario(INPUT.nextLine());
                        System.out.print("Novo Nome (deixe em branco para não alterar): ");
                        String nomeAdm = INPUT.nextLine();
                        if (!nomeAdm.trim().isEmpty()) {
                            msg.setNome(nomeAdm);
                        }
                        System.out.print("Nova Senha (6 dígitos numéricos, deixe em branco para não alterar): ");
                        String senhaAdm = INPUT.nextLine();
                        if (!senhaAdm.trim().isEmpty()) {
                            msg.setSenha(senhaAdm);
                        }

                        System.out.println("[Enviando]: " + gson.toJson(msg));
                        out.println(gson.toJson(msg));
                        break;

                    case "11": // ADM - Deletar usuário
                        msg.setOp("deletarUsuarioAdmin");
//                        msg.setToken_admin(token);
                        msg.setToken(token);
                        System.out.print("Login do usuário a deletar: ");
                        msg.setUsuario(INPUT.nextLine());

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

            } while (!opcaoMenu.equals("0"));

        } catch (IOException e) {
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
