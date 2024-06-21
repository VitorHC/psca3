package View;

import Model.ConflitoDeEntidade;
import Model.EntidadeInvalida;
import Model.Medico;
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
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import static javax.swing.JOptionPane.showMessageDialog;

public class MedicoForm extends JFrame {

  private Model.Medico medico;
  private int numeroDeCampos = 8;
  private int alturaDeCampo = 20;
  private int alturaDeRotuloDeCampo = 12;
  private int totalFormHeight = (alturaDeRotuloDeCampo + alturaDeCampo) * numeroDeCampos + 100;
  private JPanel painelFormulario;
  private
  JTextField
      campoNome, campoEmail, campoEndereco,
      campoDataDeNascimento, campoTelefone, campoCelular,
      campoCRM, campoEspecialidade;
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
    campoCRM = novoCampo(new JLabel("CRM"));
    campoEspecialidade = novoCampo(new JLabel("Especialidade"));
    campoCelular = novoCampo(new JLabel("Celular"));
    campoTelefone = novoCampo(new JLabel("Telefone"));

    if (medico != null) {
      campoNome.setText(medico.getNome());
      campoEmail.setText(medico.getEmail());
      campoDataDeNascimento.setText(medico.getDataDeNascimento());
      campoEndereco.setText(medico.getEndereco());
      campoCRM.setText(medico.getCRM());
      campoEspecialidade.setText(medico.getEspecialidade());
      campoCelular.setText(medico.getCelular());
      campoTelefone.setText(medico.getTelefone());
    }
  }

  private void criarBotaoSubmeter() {
    botaoSubmeter = new JButton(medico == null ? "Cadastrar" : "Atualizar");
    botaoSubmeter.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        Medico m = new Medico();
        if (!m.setNome(campoNome.getText())) {
          showMessageDialog(painelFormulario, "Nome inválido. Deve conter ao menos 3 caracteres.");
          return;
        }
        if (!m.setEmail(campoEmail.getText())) {
          showMessageDialog(painelFormulario, "E-mail inválido. Cheque o formato.");
          return;
        }
        if (!m.setDataDeNascimento(campoDataDeNascimento.getText())) {
          showMessageDialog(painelFormulario,
              "Data de nascimento inválida. Deve possuir formato 21/06/1994");
          return;
        }
        if (!m.setEndereco(campoEndereco.getText())) {
          showMessageDialog(painelFormulario,
              "Endereço inválido. Deve possuir ao menos 3 caracteres");
          return;
        }
        if (!m.setCRM(campoCRM.getText())) {
          showMessageDialog(painelFormulario, "CRM inválido. Deve conter 13 caracteres.");
          return;
        }
        if (!m.setEspecialidade(campoEspecialidade.getText())) {
          showMessageDialog(painelFormulario,
              "Especialidade inválida. Deve possuir ao menos 3 caracteres");
          return;
        }
        m.setTelefone(campoTelefone.getText());
        m.setCelular(campoCelular.getText());
        if (medico != null) {
          m.setId(medico.getId());
        }
        try {
          String mensagemDeSucesso;
          if (medico == null) {
            m.criar();
            mensagemDeSucesso = "Cadastro feito com sucesso";
          } else {
            m.atualizar();
            mensagemDeSucesso = "Atualizado com sucesso";
          }
          showMessageDialog(painelFormulario, mensagemDeSucesso);
          new Medicos().setVisible(true);
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

  MedicoForm(Model.Medico m) {
    medico = m;
    String title = medico == null ? "Cadastrar Médico" : "Atualizar Médico";
    setTitle(title);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(true);
    setSize(1240, 820);
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
        new MedicoForm(null).setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
