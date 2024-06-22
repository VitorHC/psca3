package View;

import static javax.swing.JOptionPane.showMessageDialog;

import Model.ConflitoDeEntidade;
import Model.EntidadeInvalida;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PacienteForm extends JFrame {

  private Model.Paciente paciente;
  private int numeroDeCampos = 7;
  private int alturaDeCampo = 20;
  private int alturaDeRotuloDeCampo = 12;
  private int totalFormHeight = (alturaDeRotuloDeCampo + alturaDeCampo) * numeroDeCampos + 100;
  private JPanel painelFormulario;
  private
  JTextField
      campoNome, campoEmail, campoEndereco,
      campoDataDeNascimento, campoTelefone, campoCelular,
      campoCPF;
  private JButton botaoSubmeter;

  private JTextField novoCampo(JLabel label) {
    JPanel painel = new JPanel();
    JTextField campo = new JTextField();
    painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
    painel.setBorder(BorderFactory.createEtchedBorder());
    label.setFont(new Font("Arial", Font.BOLD, alturaDeRotuloDeCampo));
    campo.setPreferredSize(new Dimension(190, alturaDeCampo));
    painel.add(label);
    painel.add(campo);
    painelFormulario.add(painel);
    return campo;
  }

  private JLabel criarRotuloTitulo(String titulo) {
    JLabel rotuloTitulo = new JLabel(titulo);
    rotuloTitulo.setBackground(Color.WHITE);
    rotuloTitulo.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
    rotuloTitulo.setHorizontalAlignment(SwingConstants.CENTER);
    rotuloTitulo.setVerticalAlignment(SwingConstants.NORTH);
    return rotuloTitulo;
  }

  private void criarPainelFormulario() {
    painelFormulario = new JPanel();
    painelFormulario.setLayout(new GridLayout(numeroDeCampos, 0));
    painelFormulario.setPreferredSize(new Dimension(400, totalFormHeight));
    painelFormulario.setBackground(Color.lightGray);

    campoNome = novoCampo(new JLabel("Nome"));
    campoEmail = novoCampo(new JLabel("E-mail"));
    campoDataDeNascimento = novoCampo(new JLabel("Data de nascimento"));
    campoEndereco = novoCampo(new JLabel("Endereço"));
    campoCPF = novoCampo(new JLabel("CPF"));
    campoCelular = novoCampo(new JLabel("Celular"));
    campoTelefone = novoCampo(new JLabel("Telefone"));

    if (paciente != null) {
      campoNome.setText(paciente.getNome());
      campoEmail.setText(paciente.getEmail());
      campoDataDeNascimento.setText(paciente.getDataDeNascimento());
      campoEndereco.setText(paciente.getEndereco());
      campoCPF.setText(paciente.getCPF());
      campoCelular.setText(paciente.getCelular());
      campoTelefone.setText(paciente.getTelefone());
    }
  }

  private void criarBotaoSubmeter() {
    botaoSubmeter = new JButton(paciente == null ? "Cadastrar" : "Atualizar");
    botaoSubmeter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        Model.Paciente p = new Model.Paciente();
        if (!p.setNome(campoNome.getText())) {
          showMessageDialog(painelFormulario, "Nome inválido. Deve conter ao menos 3 caracteres.");
          return;
        }
        if (!p.setEmail(campoEmail.getText())) {
          showMessageDialog(painelFormulario, "E-mail inválido. Cheque o formato.");
          return;
        }
        if (!p.setDataDeNascimento(campoDataDeNascimento.getText())) {
          showMessageDialog(painelFormulario,
              "Data de nascimento inválida. Deve possuir formato 21/06/1994");
          return;
        }
        if (!p.setEndereco(campoEndereco.getText())) {
          showMessageDialog(painelFormulario,
              "Endereço inválido. Deve possuir ao menos 3 caracteres");
          return;
        }
        if (!p.setCPF(campoCPF.getText())) {
          showMessageDialog(painelFormulario, "CPF inválido. Deve conter 14 caracteres.");
          return;
        }
        p.setTelefone(campoTelefone.getText());
        p.setCelular(campoCelular.getText());
        if (paciente != null) {
          p.setId(paciente.getId());
        }
        try {
          String mensagemDeSucesso;
          if (paciente == null) {
            p.criar();
            mensagemDeSucesso = "Cadastro feito com sucesso";
          } else {
            p.atualizar();
            mensagemDeSucesso = "Atualizado com sucesso";
          }
          showMessageDialog(painelFormulario, mensagemDeSucesso);
          new View.Pacientes().setVisible(true);
          dispose();
        } catch (ConflitoDeEntidade | EntidadeInvalida excep) {
          showMessageDialog(painelFormulario, excep.getMessage());
        } catch (SQLException excep) {
          showMessageDialog(painelFormulario, "Algo errado aconteceu. Contate o administrador.");
          excep.printStackTrace();
        }
      }
    });
  }

  PacienteForm(Model.Paciente p) {
    paciente = p;
    String title = paciente == null ? "Cadastrar Paciente" : "Atualizar Paciente";
    setTitle(title);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(600, 500);
    setLocationRelativeTo(null);
    getContentPane().setBackground(Color.WHITE);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = null;

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    gbc.weightx = 1;
    add(criarRotuloTitulo(title), gbc);

    criarPainelFormulario();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 2;
    add(painelFormulario, gbc);

    criarBotaoSubmeter();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 3;
    gbc.weighty = 4;
    add(botaoSubmeter, gbc);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        new View.PacienteForm(null).setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
