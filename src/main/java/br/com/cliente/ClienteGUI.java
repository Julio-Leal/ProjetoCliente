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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; pnl.add(new JLabel("IP do Servidor:"), gbc);
        txtIP = new JTextField("localhost", 15);
        gbc.gridx = 1; pnl.add(txtIP, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnl.add(new JLabel("Porta:"), gbc);
        txtPorta = new JTextField("12345", 5);
        gbc.gridx = 1; pnl.add(txtPorta, gbc);

        JButton btnConectar = new JButton("Conectar ao Servidor");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pnl.add(btnConectar, gbc);
        btnConectar.addActionListener(e -> conectar());
        
        return pnl;
    }

    private JPanel criarPainelLoginCad() {
        JPanel pnl = new JPanel(new GridLayout(1, 2, 10, 10));
        pnl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Login
        JPanel pnlLogin = new JPanel(new GridBagLayout());
        pnlLogin.setBorder(new TitledBorder("Login"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlLogin.add(new JLabel("Usuário:"), gbc);
        txtUsuarioLogin = new JTextField(10);
        gbc.gridx = 1; pnlLogin.add(txtUsuarioLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlLogin.add(new JLabel("Senha:"), gbc);
        txtSenhaLogin = new JPasswordField(10);
        gbc.gridx = 1; pnlLogin.add(txtSenhaLogin, gbc);

        JButton btnLogin = new JButton("Entrar");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        pnlLogin.add(btnLogin, gbc);
        btnLogin.addActionListener(e -> login());

        // Cadastro
        JPanel pnlCad = new JPanel(new GridBagLayout());
        pnlCad.setBorder(new TitledBorder("Cadastro de Novo Usuário"));
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; pnlCad.add(new JLabel("Nome:"), gbc);
        txtNomeCad = new JTextField(10);
        gbc.gridx = 1; pnlCad.add(txtNomeCad, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlCad.add(new JLabel("Usuário:"), gbc);
        txtUsuarioCad = new JTextField(10);
        gbc.gridx = 1; pnlCad.add(txtUsuarioCad, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlCad.add(new JLabel("Senha:"), gbc);
        txtSenhaCad = new JPasswordField(10);
        gbc.gridx = 1; pnlCad.add(txtSenhaCad, gbc);

        JButton btnCad = new JButton("Cadastrar");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnlCad.add(btnCad, gbc);
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnl.add(new JLabel("Meu Nome:"), gbc);
        txtMeuNome = new JTextField(20);
        gbc.gridx = 1; pnl.add(txtMeuNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnl.add(new JLabel("Meu Usuário:"), gbc);
        txtMeuUsuario = new JTextField(20);
        txtMeuUsuario.setEditable(false);
        gbc.gridx = 1; pnl.add(txtMeuUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnl.add(new JLabel("Nova Senha:"), gbc);
        txtMinhaSenha = new JPasswordField(20);
        gbc.gridx = 1; pnl.add(txtMinhaSenha, gbc);

        JPanel pnlBotoes = new JPanel(new FlowLayout());
        JButton btnConsultar = new JButton("Consultar Meus Dados");
        JButton btnAtualizar = new JButton("Atualizar Meus Dados");
        JButton btnDeletar = new JButton("Deletar Minha Conta");
        btnDeletar.setForeground(Color.RED);
        
        pnlBotoes.add(btnConsultar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnDeletar);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnl.add(pnlBotoes, gbc);

        btnConsultar.addActionListener(e -> operacao("consultarUsuario"));
        btnAtualizar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("atualizarUsuario");
            m.setToken(token);
            m.setNome(txtMeuNome.getText());
            m.setSenha(new String(txtMinhaSenha.getPassword()));
            out.println(gson.toJson(m));
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlControle.add(new JLabel("Usuário Alvo:"), gbc);
        txtAdminAlvo = new JTextField(15);
        gbc.gridx = 1; pnlControle.add(txtAdminAlvo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlControle.add(new JLabel("Novo Nome:"), gbc);
        txtAdminNovoNome = new JTextField(15);
        gbc.gridx = 1; pnlControle.add(txtAdminNovoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlControle.add(new JLabel("Nova Senha:"), gbc);
        txtAdminNovaSenha = new JPasswordField(15);
        gbc.gridx = 1; pnlControle.add(txtAdminNovaSenha, gbc);

        JPanel pnlBotoes = new JPanel(new FlowLayout());
        JButton btnListar = new JButton("Listar Todos");
        JButton btnConsultar = new JButton("Consultar Um");
        JButton btnAtualizar = new JButton("Atualizar Alvo");
        JButton btnDeletar = new JButton("Deletar Alvo");
        
        pnlBotoes.add(btnListar);
        pnlBotoes.add(btnConsultar);
        pnlBotoes.add(btnAtualizar);
        pnlBotoes.add(btnDeletar);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        pnlControle.add(pnlBotoes, gbc);
        
        pnl.add(pnlControle, BorderLayout.SOUTH);

        btnListar.addActionListener(e -> operacao("consultarUsuariosAdmin"));
        btnConsultar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("consultarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            out.println(gson.toJson(m));
        });
        btnAtualizar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("atualizarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            m.setNome(txtAdminNovoNome.getText());
            m.setSenha(new String(txtAdminNovaSenha.getPassword()));
            out.println(gson.toJson(m));
        });
        btnDeletar.addActionListener(e -> {
            Mensagem m = new Mensagem();
            m.setOp("deletarUsuarioAdmin");
            m.setToken(token);
            m.setUsuario(txtAdminAlvo.getText());
            out.println(gson.toJson(m));
        });

        return pnl;
    }

    private void operacao(String op) {
        Mensagem m = new Mensagem();
        m.setOp(op);
        m.setToken(token);
        out.println(gson.toJson(m));
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
    }

    private void cadastrar() {
        Mensagem m = new Mensagem();
        m.setOp("cadastrarUsuario");
        m.setNome(txtNomeCad.getText());
        m.setUsuario(txtUsuarioCad.getText());
        m.setSenha(new String(txtSenhaCad.getPassword()));
        out.println(gson.toJson(m));
    }

    private void logout() {
        Mensagem m = new Mensagem();
        m.setOp("logout");
        m.setToken(token);
        out.println(gson.toJson(m));
    }

    private void sair() {
        if (socket != null && !socket.isClosed()) {
            try { socket.close(); } catch (IOException e) {}
        }
        System.exit(0);
    }

    private void enviarMensagem(boolean broadcast) {
        String texto = txtMensagem.getText();
        if (texto.isEmpty()) return;
        Mensagem m = new Mensagem();
        m.setOp("enviarMensagem");
        m.setToken(token);
        m.setMensagem(texto);
        if (broadcast) {
            m.setDestinatario("todos");
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
