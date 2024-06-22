package View;

import Model.Funcionario;
import javax.swing.JFrame;
import javax.swing.JLabel;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingConstants;

public class TelaPrincipal extends JFrame{
    private JPanel tela;

    private static Funcionario funcionarioAutenticado;

    public static Funcionario getFuncionarioAutenticado() {
        return funcionarioAutenticado;
    }

    public TelaPrincipal(Funcionario f) {
        funcionarioAutenticado = f;
        setTitle("Tela Principal");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 500);
        setLocationRelativeTo(null);

        tela = new JPanel();
        tela.setBackground(SystemColor.white);
        setContentPane(tela);
        tela.setLayout(null);

        JLabel rotuloNomeFuncionario = new JLabel("Olá " + f.getNome() + ".");
        rotuloNomeFuncionario.setSize(820, 100);
        rotuloNomeFuncionario.setFont(new Font("Arial", Font.BOLD, 28));
        rotuloNomeFuncionario.setHorizontalAlignment(SwingConstants.CENTER);
        tela.add(rotuloNomeFuncionario);

        JLabel texto = new JLabel("Clínica Socorro Deus");
        texto.setBounds(220, 50,410,120);
        texto.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 39));
        tela.add(texto);

        JButton btnPaciente = new JButton("Pacientes");
        btnPaciente.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
        btnPaciente.setBounds(300, 150, 200, 60);
        tela.add(btnPaciente);

        JButton btnMedico = new JButton("Médicos");
        btnMedico.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 22));
        btnMedico.setBounds(300, 230, 200, 60);
        tela.add(btnMedico);

        JButton btnFuncionario = new JButton("Novo Funcionário");
        btnFuncionario.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 18));
        btnFuncionario.setBounds(300, 310, 200, 60);
        tela.add(btnFuncionario);

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", 3, 14));
        btnSair.setBounds(20, 430, 80, 20);
        tela.add(btnSair);

        btnPaciente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new View.Pacientes().setVisible(true);
                dispose();
            }
        });

        btnMedico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new View.Medicos().setVisible(true);
                dispose();
            }
        });

        btnFuncionario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new View.FuncionarioForm().setVisible(true);
                dispose();
            }
        });

        btnSair.addActionListener( new ActionListener() { public void actionPerformed(ActionEvent e)
        { System.exit(0); } });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Funcionario f = Funcionario.login("aluisioordones1@gmail.com", "12345");
                if (f == null) {
                    showMessageDialog(null, "Funcionário não encontrado. Fechando aplicação");
                    System.exit(0);
                    return;
                }
                new TelaPrincipal(f).setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
