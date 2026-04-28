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
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            Gson gson = new Gson();

            String json;

            while ((json = in.readLine()) != null) {

                Mensagem msg = gson.fromJson(json, Mensagem.class);

                System.out.println("Servidor: " + json);

                if (msg.getToken() != null) {
                    ClienteMain.token = msg.getToken();
                    System.out.println("TOKEN RECEBIDO: " + msg.getToken());
                }
            }

        } catch (Exception e) {
            System.out.println("Conexão encerrada.");
        }
    }
}