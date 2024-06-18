package View;

import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Paciente extends JFrame{
    private JPanel telaPaciente;

public Paciente(){

    setLocationRelativeTo(null);
    setResizable(false);
    setTitle("Paciente");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(600, 150, 1240, 820);

    telaPaciente = new JPanel();
    telaPaciente.setBackground(SystemColor.white);
    setContentPane(telaPaciente);
    telaPaciente.setLayout(null);

    JLabel texto = new JLabel("Pacientes");
    texto.setBounds(620, 0,520,100);
    texto.setFont(new Font("Arial", 3, 30));
    telaPaciente.add(texto);

    JTextField pesquisar  = new JTextField("Nome ou CPF:");
    pesquisar.setBounds(40,130,670,30);
    telaPaciente.add(pesquisar);

    JButton btnPesquisar = new JButton("Pesquisar");
    btnPesquisar.setBounds(690, 130, 130, 30);
    telaPaciente.add(btnPesquisar);

   /* JPanel dados = new JPanel();
    dados.setBackground(SystemColor.white);
    dados.setBounds(400, 100,150,100);
    setContentPane(dados);
    dados.setLayout(null);*/

    JLabel nome = new JLabel("Nome: ");
    nome.setBounds(40, 200, 150,30);
    telaPaciente.add(nome);

    JLabel CPF = new JLabel("CPF: ");
    CPF.setBounds(250, 200, 100,30);
    telaPaciente.add(CPF);
    

    }


}


