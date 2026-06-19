package br.com.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JOptionPane;
import com.google.gson.Gson;
import br.com.model.Mensagem;

public class ClienteReceiverGUI implements Runnable {
    private Socket socket;
    private ClienteGUI gui;
    private Gson gson = new Gson();

    public ClienteReceiverGUI(Socket socket, ClienteGUI gui) {
        this.socket = socket;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String json;
            while ((json = in.readLine()) != null) {
                System.out.println("[Recebido]: " + json);
                Mensagem msg = gson.fromJson(json, Mensagem.class);
                processarMensagem(msg);
            }
        } catch (Exception e) {
            gui.exibirMensagem("[Erro]: Conexão perdida com o servidor.");
            gui.setToken(null);
        }
    }

    private void processarMensagem(Mensagem msg) {
        // Respostas de Sucesso/Erro
        if (msg.getResposta() != null) {
            if (msg.getResposta().equals("200")) {
                if (msg.getToken() != null) {
                    gui.setToken(msg.getToken());
                } else if ("Logout efetuado".equalsIgnoreCase(msg.getMensagem())) {
                    gui.setToken(null);
                } else if ("Deletado com sucesso".equalsIgnoreCase(msg.getMensagem())) {
                    JOptionPane.showMessageDialog(gui, "Sua conta foi deletada.");
                    gui.setToken(null);
                } else if (msg.getLista_usuarios() != null) {
                    // Resposta de listagem admin
                    gui.atualizarTabelaAdmin(msg.getLista_usuarios());
                } else if (msg.getNome() != null && msg.getUsuario() != null) {
                    // Resposta de consulta (Perfil ou Admin)
                    if (msg.getUsuario().equalsIgnoreCase(msg.getUsuario())) {
                        gui.atualizarDadosPerfil(msg.getNome(), msg.getUsuario());
                        gui.preencherDadosAdmin(msg.getNome(), msg.getUsuario());
                    }
                } else if (msg.getMensagem() != null) {
                    JOptionPane.showMessageDialog(gui, msg.getMensagem());
                }
            } else {
                JOptionPane.showMessageDialog(gui, "Erro: " + msg.getMensagem());
            }
        }

        // Eventos Assíncronos
        if ("receberMensagem".equals(msg.getOp())) {
            gui.exibirMensagem(msg.getUsuario() + ": " + msg.getMensagem());
        }

        if ("listaUsuariosLogados".equals(msg.getOp())) {
            gui.atualizarListaUsuarios(msg.getLista_usuarios());
        }
    }
}
