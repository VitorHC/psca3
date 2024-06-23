package View;

import static javax.swing.JOptionPane.showMessageDialog;

import Model.Paciente;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Pacientes extends JFrame {

  private JTextField campoDeBusca;

  private JButton botaoBuscar, botaoAdicionar;

  private JList listaPacientes;

  private DefaultListModel<String> pacientesDescricao;

  private List<Paciente> pacientes;

  public String descricaoSelecionada;

  private String gerarDescricaoPaciente(Model.Paciente p) {
    return p.getNome() + " | " + p.getCPF();
  }

  private void criarBotaoBuscar() {
    botaoBuscar = new JButton("Buscar");
    botaoBuscar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        try {
          Optional<Model.Paciente> optPaciente = Model.Paciente.buscar(campoDeBusca.getText());
          if (optPaciente.isEmpty()) {
            showMessageDialog(getContentPane(), "Paciente não encontrado");
            return;
          }
          while (!pacientesDescricao.isEmpty()) {
            pacientesDescricao.removeElement(pacientesDescricao.get(0));
          }
          pacientesDescricao.addElement(gerarDescricaoPaciente(optPaciente.get()));
        } catch (SQLException e) {
          showMessageDialog(getContentPane(), "Algo de errado aconteceu. Contate o administrador.");
          e.printStackTrace();
        }
      }
    });
  }

  private JPanel criarPainelDeBusca() {
    JPanel painelDeBusca = new JPanel();
    painelDeBusca.setBackground(Color.DARK_GRAY);
    criarBotaoBuscar();
    campoDeBusca = new JTextField(30);
    campoDeBusca.setToolTipText("CPF");
    painelDeBusca.add(campoDeBusca);
    painelDeBusca.add(botaoBuscar);
    return painelDeBusca;
  }

  private JPanel criarPainelCorpo() {
    JPanel painelCorpo = new JPanel();
    painelCorpo.setLayout(new BoxLayout(painelCorpo, BoxLayout.Y_AXIS));
    painelCorpo.setPreferredSize(new Dimension(550, 550));
    painelCorpo.setBackground(Color.WHITE);
    return painelCorpo;
  }

  private JPanel criarPainelRotuloDaLista() {
    JPanel painelRotuloLista = new JPanel();
    painelRotuloLista.setBackground(Color.WHITE);
    JLabel rotuloLista = new JLabel("Nome | CPF");
    painelRotuloLista.add(rotuloLista);
    return painelRotuloLista;
  }

  private void criarBotaoAdicionar() {
    botaoAdicionar = new JButton("Adicionar");
    botaoAdicionar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        new PacienteForm(null).setVisible(true);
        dispose();
      }
    });
  }

  private JPanel criarPaineldoBotaoAdicionar() {
    JPanel painelBotaoAdicionar = new JPanel();
    painelBotaoAdicionar.setBackground(Color.WHITE);
    criarBotaoAdicionar();
    painelBotaoAdicionar.add(botaoAdicionar);
    return painelBotaoAdicionar;
  }

  private void buscarPacientes() {
    try {
      pacientes = Model.Paciente.buscarTodos();
      pacientes.sort(((m1, m2) -> m1.getNome().compareToIgnoreCase(m2.getNome())));
      pacientesDescricao = new DefaultListModel<String>();
      for (Model.Paciente p : pacientes) {
        pacientesDescricao.addElement(gerarDescricaoPaciente(p));
      }
    } catch (SQLException except) {
      showMessageDialog(null, "Algo deu errado. Contate o administrador.");
      except.printStackTrace();
    }
  }

  private JScrollPane criarPainelScrollDePacientes() {
    buscarPacientes();
    listaPacientes = new JList(pacientesDescricao);
    listaPacientes.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    listaPacientes.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    listaPacientes.setVisibleRowCount(-1);
    listaPacientes.setFixedCellHeight(24);
    listaPacientes.setFixedCellWidth(450);
    listaPacientes.setFont(new Font("Arial", Font.PLAIN, 12));
    listaPacientes.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
          String valorSelecionado = ((JList<String>) event.getSource()).getSelectedValue();
          if (valorSelecionado != null) {
            descricaoSelecionada = valorSelecionado;
          }
        }
      }
    });
    listaPacientes.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && descricaoSelecionada != null) {
          for (Model.Paciente p : pacientes) {
            if (gerarDescricaoPaciente(p).equals(descricaoSelecionada)) {
              new View.Paciente(p).setVisible(true);
              dispose();
              break;
            }
          }
        }
      }
    });
    JScrollPane listaPacientesScroll = new JScrollPane(listaPacientes);
    listaPacientesScroll.setPreferredSize(new Dimension(450, 450));
    return listaPacientesScroll;
  }

  private JPanel criarPainelDePacientes() {
    JPanel painelDePacientes = new JPanel();
    painelDePacientes.setBackground(Color.WHITE);
    JScrollPane painelScrollDePacientes = criarPainelScrollDePacientes();
    painelDePacientes.add(painelScrollDePacientes);
    return painelDePacientes;
  }

  Pacientes() {
    setTitle("Pacientes");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(700, 820);
    setLocationRelativeTo(null);
    getContentPane().setBackground(Color.WHITE);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = null;

    JLabel rotuloTitulo = new JLabel("Pacientes");
    rotuloTitulo.setBackground(Color.WHITE);
    rotuloTitulo.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
    add(rotuloTitulo);

    JPanel painelDeBusca = criarPainelDeBusca();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 1;
    gbc.weighty = 1;
    gbc.weightx = 1;
    add(painelDeBusca, gbc);

    JPanel painelCorpo = criarPainelCorpo();
    painelCorpo.add(criarPaineldoBotaoAdicionar());
    painelCorpo.add(criarPainelRotuloDaLista());
    painelCorpo.add(criarPainelDePacientes());
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.weighty = 4;
    add(painelCorpo, gbc);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        new Pacientes().setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
