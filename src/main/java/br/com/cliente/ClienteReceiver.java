package br.com.cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import com.google.gson.Gson;
import br.com.model.Mensagem;

public class ClienteReceiver implements Runnable {

    private Socket socket;
    
    public ClienteReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // Leitura explicitamente padronizada em UTF-8 conforme o documento de requisitos
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            Gson gson = new Gson();

            String json;

            // Fica em loop aguardando as respostas assíncronas do servidor
            while ((json = in.readLine()) != null) {
                Mensagem msg = gson.fromJson(json, Mensagem.class);
                
                // Exibe no console exatamente o payload bruto recebido (Obrigatório para a avaliação)
                System.out.println("\n[Recebido do Servidor]: " + json);
                
                // Se a resposta for sucesso de login (200) e contiver um token válido, armazena dinamicamente
                if (msg.getResposta() != null && msg.getResposta().equals("200") && msg.getToken() != null) {
                    ClienteMain.token = msg.getToken();
                }
                
                // Se o próprio usuário comum se autodeletou ou realizou logout com sucesso, limpa a sessão local
                if (msg.getResposta() != null && msg.getResposta().equals("200") && "Logout efetuado".equalsIgnoreCase(msg.getMensagem())) {
                    ClienteMain.token = null;
                }
                
                System.out.print("Escolha uma opção: "); // Mantém a interface do menu alinhada
            }
        } catch (Exception e) {
            System.out.println("\n[Aviso]: Conexão com o servidor foi encerrada.");
            ClienteMain.token = null;
        }
    }
}