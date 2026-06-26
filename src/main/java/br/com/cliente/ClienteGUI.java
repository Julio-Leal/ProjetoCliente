package br.com.cliente;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;
import br.com.model.Mensagem;
import br.com.model.Usuario;

public class ClienteGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Conexão
    private JTextField txtIP;
    private JTextField txtPorta;
    
    // Login/Cadastro
    private JTextField txtUsuarioLogin;
    private JPasswordField txtSenhaLogin;
    private JTextField txtNomeCad;
    private JTextField txtUsuarioCad;
    private JPasswordField txtSenhaCad;
    
    // Chat
    private JTextArea txtChat;
    private JTextField txtMensagem;
    private JList<String> listUsuarios;
    private DefaultListModel<String> modelUsuarios;
    private JLabel lblStatus;
    
    // Perfil
    private JTextField txtMeuNome;
    private JTextField txtMeuUsuario;
    private JPasswordField txtMinhaSenha;
    
    // Administrador
    private JTable tblAdmin;
    private DefaultTableModel modelAdmin;
    private JTextField txtAdminAlvo;
    private JTextField txtAdminNovoNome;
    private JPasswordField txtAdminNovaSenha;
    
    private Socket socket;
    private PrintWriter out;
    private Gson gson = new Gson();
    private String token = null;
    private String meuUsuario = null;

    public ClienteGUI() {
        setTitle("Cliente de Chat - EP3");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                sair();
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(criarPainelConexao(), "CONEXAO");
        mainPanel.add(criarPainelLoginCad(), "LOGIN");
        mainPanel.add(criarPainelPrincipal(), "PRINCIPAL");

        getContentPane().add(mainPanel);
        cardLayout.show(mainPanel, "CONEXAO");
    }

    private JPanel criarPainelConexao() {
        JPanel pnl = new JPanel(new GridBagLayout());

        GridBagConstraints gbc0 = new GridBagConstraints();
        gbc0.insets = new Insets(10, 10, 10, 10);
        gbc0.gridx = 0; gbc0.gridy = 0;
        pnl.add(new JLabel("IP do Servidor:"), gbc0);

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(10, 10, 10, 10);
        gbc1.gridx = 1; gbc1.gridy = 0;
        txtIP = new JTextField("localhost", 15);
        pnl.add(txtIP, gbc1);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 10, 10, 10);
        gbc2.gridx = 0; gbc2.gridy = 1;
        pnl.add(new JLabel("Porta:"), gbc2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(10, 10, 10, 10);
        gbc3.gridx = 1; gbc3.gridy = 1;
        txtPorta = new JTextField("12345", 5);
        pnl.add(txtPorta, gbc3);

        JButton btnConectar = new JButton("Conectar ao Servidor");
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(10, 10, 10, 10);
        gbc4.gridx = 0; gbc4.gridy = 2; gbc4.gridwidth = 2;
        pnl.add(btnConectar, gbc4);
        btnConectar.addActionListener(e -> conectar());
        
        return pnl;
    }

    private JPanel criarPainelLoginCad() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Login
        JPanel pnlLogin = new JPanel(new GridBagLayout());
        pnlLogin.setBorder(new TitledBorder("Login"));

        GridBagConstraints gbcL0 = new GridBagConstraints();
        gbcL0.insets = new Insets(5, 5, 5, 5);
        gbcL0.fill = GridBagConstraints.HORIZONTAL;
        gbcL0.gridx = 0; gbcL0.gridy = 0;
        pnlLogin.add(new JLabel("Usuário:"), gbcL0);

        GridBagConstraints gbcL1 = new GridBagConstraints();
        gbcL1.insets = new Insets(5, 5, 5, 5);
        gbcL1.fill = GridBagConstraints.HORIZONTAL;
        gbcL1.gridx = 1; gbcL1.gridy = 0;
        txtUsuarioLogin = new JTextField(10);
        pnlLogin.add(txtUsuarioLogin, gbcL1);

        GridBagConstraints gbcL2 = new GridBagConstraints();
        gbcL2.insets = new Insets(5, 5, 5, 5);
        gbcL2.fill = GridBagConstraints.HORIZONTAL;
        gbcL2.gridx = 0; gbcL2.gridy = 1;
        pnlLogin.add(new JLabel("Senha:"), gbcL2);

        GridBagConstraints gbcL3 = new GridBagConstraints();
        gbcL3.insets = new Insets(5, 5, 5, 5);
        gbcL3.fill = GridBagConstraints.HORIZONTAL;
        gbcL3.gridx = 1; gbcL3.gridy = 1;
        txtSenhaLogin = new JPasswordField(10);
        pnlLogin.add(txtSenhaLogin, gbcL3);

        JButton btnLogin = new JButton("Entrar");
        GridBagConstraints gbcL4 = new GridBagConstraints();
        gbcL4.insets = new Insets(5, 5, 5, 5);
        gbcL4.fill = GridBagConstraints.HORIZONTAL;
        gbcL4.gridx = 0; gbcL4.gridy = 2; gbcL4.gridwidth = 2;
        pnlLogin.add(btnLogin, gbcL4);
        btnLogin.addActionListener(e -> login());

        // Cadastro
        JPanel pnlCad = new JPanel(new GridBagLayout());
        pnlCad.setBorder(new TitledBorder("Cadastro de Novo Usuário"));

        GridBagConstraints gbcC0 = new GridBagConstraints();
        gbcC0.insets = new Insets(5, 5, 5, 5);
        gbcC0.fill = GridBagConstraints.HORIZONTAL;
        gbcC0.gridx = 0; gbcC0.gridy = 0;
        pnlCad.add(new JLabel("Nome:"), gbcC0);

        GridBagConstraints gbcC1 = new GridBagConstraints();
        gbcC1.insets = new Insets(5, 5, 5, 5);
        gbcC1.fill = GridBagConstraints.HORIZONTAL;
        gbcC1.gridx = 1; gbcC1.gridy = 0;
        txtNomeCad = new JTextField(10);
        pnlCad.add(txtNomeCad, gbcC1);

        GridBagConstraints gbcC2 = new GridBagConstraints();
        gbcC2.insets = new Insets(5, 5, 5, 5);
        gbcC2.fill = GridBagConstraints.HORIZONTAL;
        gbcC2.gridx = 0; gbcC2.gridy = 1;
        pnlCad.add(new JLabel("Usuário:"), gbcC2);

        GridBagConstraints gbcC3 = new GridBagConstraints();
        gbcC3.insets = new Insets(5, 5, 5, 5);
        gbcC3.fill = GridBagConstraints.HORIZONTAL;
        gbcC3.gridx = 1; gbcC3.gridy = 1;
        txtUsuarioCad = new JTextField(10);
        pnlCad.add(txtUsuarioCad, gbcC3);

        GridBagConstraints gbcC4 = new GridBagConstraints();
        gbcC4.insets = new Insets(5, 5, 5, 5);
        gbcC4.fill = GridBagConstraints.HORIZONTAL;
        gbcC4.gridx = 0; gbcC4.gridy = 2;
        pnlCad.add(new JLabel("Senha:"), gbcC4);

        GridBagConstraints gbcC5 = new GridBagConstraints();
        gbcC5.insets = new Insets(5, 5, 5, 5);
        gbcC5.fill = GridBagConstraints.HORIZONTAL;
        gbcC5.gridx = 1; gbcC5.gridy = 2;
        txtSenhaCad = new JPasswordField(10);
        pnlCad.add(txtSenhaCad, gbcC5);

        JButton btnCad = new JButton("Cadastrar");
        GridBagConstraints gbcC6 = new GridBagConstraints();
        gbcC6.insets = new Insets(5, 5, 5, 5);
        gbcC6.fill = GridBagConstraints.HORIZONTAL;
        gbcC6.gridx = 0; gbcC6.gridy = 3; gbcC6.gridwidth = 2;
        pnlCad.add(btnCad, gbcC6);
        btnCad.addActionListener(e -> cadastrar());

        pnl.add(pnlLogin);
        pnl.add(pnlCad);
        return pnl;
    }

    private JPanel criarPainelPrincipal() {
        JPanel pnl = new JPanel(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Chat", criarPainelChat());
        tabs.addTab("Meu Perfil", criarPainelPerfil());
        tabs.addTab("Administração", criarPainelAdmin());

        pnl.add(tabs, BorderLayout.CENTER);
        
        JPanel pnlStatus = new JPanel(new BorderLayout());
        lblStatus = new JLabel("Logado como: ");
        pnlStatus.add(lblStatus, BorderLayout.WEST);
        
        JPanel pnlAcoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Logout");
        JButton btnSair = new JButton("SAIR DA APLICAÇÃO");
        pnlAcoes.add(btnLogout);
        pnlAcoes.add(btnSair);
        pnlStatus.add(pnlAcoes, BorderLayout.EAST);
        
        pnl.add(pnlStatus, BorderLayout.SOUTH);

        btnLogout.addActionListener(e -> logout());
        btnSair.addActionListener(e -> sair());

        return pnl;
    }

    private JPanel criarPainelChat() {
        JPanel pnl = new JPanel(new BorderLayout());
        
        modelUsuarios = new DefaultListModel<>();
        listUsuarios = new JList<>(modelUsuarios);
        JScrollPane scpUsers = new JScrollPane(listUsuarios);
        scpUsers.setBorder(new TitledBorder("Usuários Online"));
        scpUsers.setPreferredSize(new Dimension(180, 0));
        pnl.add(scpUsers, BorderLayout.EAST);

        txtChat = new JTextArea();
        txtChat.setEditable(false);
        pnl.add(new JScrollPane(txtChat), BorderLayout.CENTER);

        JPanel pnlEnvio = new JPanel(new BorderLayout());
        txtMensagem = new JTextField();
        pnlEnvio.add(txtMensagem, BorderLayout.CENTER);
        
        JPanel pnlBotoes = new JPanel(new FlowLayout());
        JButton btnEnviar = new JButton("Enviar Direta");
        JButton btnBroadcast = new JButton("Broadcast");
        pnlBotoes.add(btnEnviar);
        pnlBotoes.add(btnBroadcast);
        pnlEnvio.add(pnlBotoes, BorderLayout.EAST);
        
        pnl.add(pnlEnvio, BorderLayout.SOUTH);

        btnEnviar.addActionListener(e -> enviarMensagem(false));
        btnBroadcast.addActionListener(e -> enviarMensagem(true));
        
        return pnl;
    }

    private JPanel criarPainelPerfil() {
        JPanel pnl = new JPanel(new GridBagLayout());

        GridBagConstraints gbc0 = new GridBagConstraints();
        gbc0.insets = new Insets(10, 10, 10, 10);
        gbc0.fill = GridBagConstraints.HORIZONTAL;
        gbc0.gridx = 0; gbc0.gridy = 0;
        pnl.add(new JLabel("Meu Nome:"), gbc0);

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(10, 10, 10, 10);
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.gridx = 1; gbc1.gridy = 0;
        txtMeuNome = new JTextField(20);
        pnl.add(txtMeuNome, gbc1);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(10, 10, 10, 10);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0; gbc2.gridy = 1;
        pnl.add(new JLabel("Meu Usuário:"), gbc2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(10, 10, 10, 10);
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.gridx = 1; gbc3.gridy = 1;
        txtMeuUsuario = new JTextField(20);
        txtMeuUsuario.setEditable(false);
        pnl.add(txtMeuUsuario, gbc3);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(10, 10, 10, 10);
        gbc4.fill = GridBagConstraints.HORIZONTAL;
        gbc4.gridx = 0; gbc4.gridy = 2;
        pnl.add(new JLabel("Nova Senha:"), gbc4);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.insets = new Insets(10, 10, 10, 10);
        gbc5.fill = GridBagConstraints.HORIZONTAL;
        gbc5.gridx = 1; gbc5.gridy = 2;
        txtMinhaSenha = new JPasswordField(20);
        pnl.add(txtMinhaSenha, gbc5);

        JPanel pnlBotoes = new JPanel(new FlowLayout());
        JButton btnConsultar = new JButton("Consultar Meus Dados");
        JButton btnAtualizar = new JButton("Atualizar Meus Dados");
        JButton btnDeletar = new JButton("Deletar Minha Conta");
        btnDeletar.setForeground(Color.RED);
        
        pnlBotoes.add(btnConsultar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnDeletar);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.insets = new Insets(10, 10, 10, 10);
        gbc6.fill = GridBagConstraints.HORIZONTAL;
        gbc6.gridx = 0; gbc6.gridy = 3; gbc6.gridwidth = 2;
        pnl.add(pnlBotoes, gbc6);

        btnConsultar.addActionListener(e -> operacao("consultarUsuario"));
        btnAtualizar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("atualizarUsuario");
            m.setToken(token);
            m.setNome(txtMeuNome.getText());
            m.setSenha(new String(txtMinhaSenha.getPassword()));
            out.println(gson.toJson(m));
            
            //LOG MOSTRADO NO TERMINAL
            System.out.println("[Enviado]: " + gson.toJson(m)); 
        });
        btnDeletar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar sua conta?") == JOptionPane.YES_OPTION) {
                operacao("deletarUsuario");
            }
        });

        return pnl;
    }

    private JPanel criarPainelAdmin() {
        JPanel pnl = new JPanel(new BorderLayout());
        
        String[] cols = {"Nome", "Usuário"};
        modelAdmin = new DefaultTableModel(cols, 0);
        tblAdmin = new JTable(modelAdmin);
        pnl.add(new JScrollPane(tblAdmin), BorderLayout.CENTER);

        JPanel pnlControle = new JPanel(new GridBagLayout());
        pnlControle.setBorder(new TitledBorder("Operações Administrativas"));

        GridBagConstraints gbc0 = new GridBagConstraints();
        gbc0.insets = new Insets(5, 5, 5, 5);
        gbc0.fill = GridBagConstraints.HORIZONTAL;
        gbc0.gridx = 0; gbc0.gridy = 0;
        pnlControle.add(new JLabel("Usuário Alvo:"), gbc0);

        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(5, 5, 5, 5);
        gbc1.fill = GridBagConstraints.HORIZONTAL;
        gbc1.gridx = 1; gbc1.gridy = 0;
        txtAdminAlvo = new JTextField(15);
        pnlControle.add(txtAdminAlvo, gbc1);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(5, 5, 5, 5);
        gbc2.fill = GridBagConstraints.HORIZONTAL;
        gbc2.gridx = 0; gbc2.gridy = 1;
        pnlControle.add(new JLabel("Novo Nome:"), gbc2);

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new Insets(5, 5, 5, 5);
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.gridx = 1; gbc3.gridy = 1;
        txtAdminNovoNome = new JTextField(15);
        pnlControle.add(txtAdminNovoNome, gbc3);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new Insets(5, 5, 5, 5);
        gbc4.fill = GridBagConstraints.HORIZONTAL;
        gbc4.gridx = 0; gbc4.gridy = 2;
        pnlControle.add(new JLabel("Nova Senha:"), gbc4);

        GridBagConstraints gbc5 = new GridBagConstraints();
        gbc5.insets = new Insets(5, 5, 5, 5);
        gbc5.fill = GridBagConstraints.HORIZONTAL;
        gbc5.gridx = 1; gbc5.gridy = 2;
        txtAdminNovaSenha = new JPasswordField(15);
        pnlControle.add(txtAdminNovaSenha, gbc5);

        JPanel pnlBotoes = new JPanel(new FlowLayout());
        JButton btnListar = new JButton("Listar Todos");
        JButton btnConsultar = new JButton("Consultar Um");
        JButton btnAtualizar = new JButton("Atualizar Alvo");
        JButton btnDeletar = new JButton("Deletar Alvo");
        
        pnlBotoes.add(btnListar);
        pnlBotoes.add(btnConsultar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnDeletar);

        GridBagConstraints gbc6 = new GridBagConstraints();
        gbc6.insets = new Insets(5, 5, 5, 5);
        gbc6.fill = GridBagConstraints.HORIZONTAL;
        gbc6.gridx = 0; gbc6.gridy = 3; gbc6.gridwidth = 2;
        pnlControle.add(pnlBotoes, gbc6);
        
        pnl.add(pnlControle, BorderLayout.SOUTH);

        btnListar.addActionListener(e -> operacao("consultarUsuariosAdmin"));
        btnConsultar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("consultarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            out.println(gson.toJson(m));
            
            //LOG MOSTRADO NO TERMINAL
            System.out.println("[Enviado]: " + gson.toJson(m));
        });
        btnAtualizar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("atualizarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            m.setNome(txtAdminNovoNome.getText());
            m.setSenha(new String(txtAdminNovaSenha.getPassword()));
            out.println(gson.toJson(m));
            
            //LOG MOSTRADO NO TERMINAL
            System.out.println("[Enviado]: " + gson.toJson(m));
        });
        btnDeletar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("deletarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            out.println(gson.toJson(m));
            
            //LOG MOSTRADO NO TERMINAL
            System.out.println("[Enviado]: " + gson.toJson(m));
        });

        return pnl;
    }

    private void operacao(String op) {
        Mensagem m = new Mensagem();
        m.setOp(op);
        m.setToken(token);
        out.println(gson.toJson(m));
        
        //LOG MOSTRADO NO TERMINAL
        System.out.println("[Enviado]: " + gson.toJson(m));
    }

    private void conectar() {
        try {
            socket = new Socket(txtIP.getText(), Integer.parseInt(txtPorta.getText()));
            out = new PrintWriter(socket.getOutputStream(), true);
            new Thread(new ClienteReceiverGUI(socket, this)).start();
            cardLayout.show(mainPanel, "LOGIN");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar: " + ex.getMessage());
        }
    }

    private void login() {
        Mensagem m = new Mensagem();
        m.setOp("login");
        m.setUsuario(txtUsuarioLogin.getText());
        m.setSenha(new String(txtSenhaLogin.getPassword()));
        meuUsuario = m.getUsuario();
        out.println(gson.toJson(m));
        
        //LOG MOSTRADO NO TERMINAL
        System.out.println("[Enviado]: " + gson.toJson(m));
    }

    private void cadastrar() {
        Mensagem m = new Mensagem();
        m.setOp("cadastrarUsuario");
        m.setNome(txtNomeCad.getText());
        m.setUsuario(txtUsuarioCad.getText());
        m.setSenha(new String(txtSenhaCad.getPassword()));
        out.println(gson.toJson(m));
        
        //LOG MOSTRADO NO TERMINAL
        System.out.println("[Enviado]: " + gson.toJson(m));
    }

    private void logout() {
        Mensagem m = new Mensagem();
        m.setOp("logout");
        m.setToken(token);
        out.println(gson.toJson(m));
        
        //LOG MOSTRADO NO TERMINAL
        System.out.println("[Enviado]: " + gson.toJson(m));
    }

    private void sair() {
        if (socket != null && !socket.isClosed()) {
            try { socket.close(); } catch (IOException e) {}
        }
        System.exit(0);
    }

    private void enviarMensagem(boolean broadcast) {
        String texto = txtMensagem.getText();
        if (texto.isEmpty()) 
        	return;
        Mensagem m = new Mensagem();
        m.setOp("enviarMensagem");
        m.setToken(token);
        m.setMensagem(texto);
        if (broadcast) {
            m.setDestinatario("/todos");
        } else {
            String dest = listUsuarios.getSelectedValue();
            if (dest == null) {
                JOptionPane.showMessageDialog(this, "Selecione um destinatário na lista.");
                return;
            }
            m.setDestinatario(dest);
        }
        out.println(gson.toJson(m));
        txtMensagem.setText("");
        exibirMensagem("Eu -> " + m.getDestinatario() + ": " + texto);
        
        //LOG MOSTRADO NO TERMINAL
        System.out.println("[Enviado]: " + gson.toJson(m));
    }

    public void exibirMensagem(String msg) {
        SwingUtilities.invokeLater(() -> {
            txtChat.append(msg + "\n");
            txtChat.setCaretPosition(txtChat.getDocument().getLength());
        });
    }

    public void setToken(String token) {
        this.token = token;
        if (token != null) {
            SwingUtilities.invokeLater(() -> {
                lblStatus.setText("Logado como: " + meuUsuario);
                cardLayout.show(mainPanel, "PRINCIPAL");
            });
        } else {
            SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "LOGIN"));
        }
    }

    public void atualizarDadosPerfil(String nome, String usuario) {
        SwingUtilities.invokeLater(() -> {
            txtMeuNome.setText(nome);
            txtMeuUsuario.setText(usuario);
        });
    }

    public void atualizarTabelaAdmin(List<Usuario> usuarios) {
        SwingUtilities.invokeLater(() -> {
            modelAdmin.setRowCount(0);
            for (Usuario u : usuarios) {
                modelAdmin.addRow(new Object[]{u.getNome(), u.getUsuario()});
            }
        });
    }

    public void preencherDadosAdmin(String nome, String usuario) {
        SwingUtilities.invokeLater(() -> {
            txtAdminNovoNome.setText(nome);
            txtAdminAlvo.setText(usuario);
        });
    }

    public void atualizarListaUsuarios(List<Usuario> usuarios) {
        SwingUtilities.invokeLater(() -> {
            modelUsuarios.clear();
            for (Usuario u : usuarios) {
                if (!u.getUsuario().equalsIgnoreCase(meuUsuario)) {
                    modelUsuarios.addElement(u.getUsuario());
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClienteGUI().setVisible(true));
    }
}