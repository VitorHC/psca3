package View;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.SystemColor;
import javax.swing.JButton;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame{
    private JPanel tela;
    public static String usuarioatual;
    



    public TelaPrincipal(){

        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Tela Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 200, 820, 420);

        tela = new JPanel();
        tela.setBackground(SystemColor.white);
        setContentPane(tela);
        tela.setLayout(null);

        JLabel texto = new JLabel("Clina Socorro Deus");
        texto.setBounds(220, 0,600,150);
        texto.setFont(new Font("Arial", 3, 39));
        tela.add(texto);


        JButton btnPaciente = new JButton("Pacientes");
        btnPaciente.setFont(new Font("Arial", 3, 22));
        btnPaciente.setBounds(300, 150, 200, 60);
        tela.add(btnPaciente);

        JButton btnMedico = new JButton("Médicos");
        btnMedico.setFont(new Font("Arial", 3, 22));
        btnMedico.setBounds(300, 230, 200, 60);
        tela.add(btnMedico);

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", 3, 14));
        btnSair.setBounds(20, 350, 80, 20);
        tela.add(btnSair);

        btnSair.addActionListener( new ActionListener() { public void actionPerformed(ActionEvent e)
            { System.exit(0); } });

        btnPaciente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Paciente telaPaciente = new Paciente();
                telaPaciente.setVisible(true);
                dispose();
            }
        });

   


    }
}
