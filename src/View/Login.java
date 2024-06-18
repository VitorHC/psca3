package View;

import Model.Funcionario;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ThreadPoolExecutor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {
    private JPanel tela;
    private JTextField usuario;
    private JPasswordField passsenha;
    public static String usuarioatual;

    public static void main(String[] args) {
      EventQueue.invokeLater(new Runnable() {
          public void run() {
              try {
              
                  Login  login = new Login();
                  login.setVisible(true);
              }  catch (Exception e) {
                  e.printStackTrace();
              }
          }
      });
  }

public Login(){
    
            setLocationRelativeTo(null);
            setResizable(false);
            setTitle("Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(500, 200, 426, 212);
            
            tela = new JPanel();
            tela.setBackground(SystemColor.white);
            setContentPane(tela);
            tela.setLayout(null);
            
            //JLabel ADM = new JLabel("ADM");
            
            JLabel lblIdentificao = new JLabel("Login");
            lblIdentificao.setBounds(150, 0, 139, 39);
            lblIdentificao.setFont(new Font("Arial", 3, 19));
            tela.add(lblIdentificao);
            
            JLabel lblUsuario = new JLabel("Usuario");
            lblUsuario.setBounds(24, 65, 70, 15);
            tela.add(lblUsuario);
            
            JLabel lblsenha = new JLabel("Senha");
            lblsenha.setBounds(24, 92, 70, 15);
            tela.add(lblsenha);
                               
            usuario = new JTextField();
            usuario.setBounds(112, 63, 219, 19);
            tela.add(usuario);
            usuario.setColumns(10);
            
            passsenha = new JPasswordField();
            passsenha.setBounds(112, 90, 219, 19);
            tela.add(passsenha);
            
              JButton btnSair = new JButton("Sair");
            btnSair.setBounds(30, 136, 97, 25);
            tela.add(btnSair);
            
            JButton btnEntrar = new JButton("Entrar");
            btnEntrar.setBounds(170, 136, 120, 25);
            tela.add(btnEntrar);

          

          btnEntrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                  String usuarioLogin;
                  String senhaLogin = passsenha.getText().toString();
                  usuarioLogin = usuario.getText();
                Funcionario funcionario = Funcionario.login(usuarioLogin, senhaLogin);
                System.out.println(usuarioLogin);
                System.out.println(senhaLogin);

                  if(funcionario == null){
                    JOptionPane.showMessageDialog(rootPane, "Erro Ao realizar o login");

                  }else{
                    JOptionPane.showMessageDialog(rootPane, "Sucesso ao Logar!");
                    TelaPrincipal tela = new TelaPrincipal();
                    tela.setVisible(true);
                    dispose();
                  }

                } catch (Exception ec) {
                  System.out.println("Erro ao consultar usuario " + ec.getMessage());
                }
            }
          });

          btnSair.addActionListener( new ActionListener() { public void actionPerformed(ActionEvent e)
            { System.exit(0); } });

          
    }
}
