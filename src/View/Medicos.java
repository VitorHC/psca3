package View;

import Model.Medico;
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

import static javax.swing.JOptionPane.showMessageDialog;

public class Medicos extends JFrame {

  private JTextField campoDeBusca;

  private JButton botaoBuscar, botaoAdicionar;

  private JList listaMedicos;

  private DefaultListModel<String> medicosDescricao;

  private List<Medico> medicos;

  private String descricaoSelecionada;

  private String gerarDescricaoMedico(Medico medico) {
    return medico.getNome()
        + " | "
        + medico.getCRM()
        + " | "
        + medico.getEspecialidade();
  }

  private void criarBotaoBuscar() {
    botaoBuscar = new JButton("Buscar");
    botaoBuscar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        try {
          Optional<Medico> optMedico = Medico.buscar(campoDeBusca.getText());
          if (optMedico.isEmpty()) {
            showMessageDialog(getContentPane(), "Médico não encontrado");
            return;
          }
          while (!medicosDescricao.isEmpty()) {
            medicosDescricao.removeElement(medicosDescricao.get(0));
          }
          medicosDescricao.addElement(gerarDescricaoMedico(optMedico.get()));
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
    campoDeBusca.setToolTipText("CRM");
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
    JLabel rotuloLista = new JLabel("Médico | CRM | Especialidade");
    painelRotuloLista.add(rotuloLista);
    return painelRotuloLista;
  }

  private void criarBotaoAdicionar() {
    botaoAdicionar = new JButton("Adicionar");
    botaoAdicionar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        new MedicoForm(null).setVisible(true);
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

  private void buscarMedicos() {
    try {
      medicos = Medico.buscarTodos();
      medicos.sort(((m1, m2) -> m1.getNome().compareToIgnoreCase(m2.getNome())));
      medicosDescricao = new DefaultListModel<String>();
      for (Medico medico : medicos) {
        medicosDescricao.addElement(gerarDescricaoMedico(medico));
      }
    } catch (SQLException except) {
      showMessageDialog(null, "Algo deu errado. Contate o administrador.");
      except.printStackTrace();
    }
  }

  private JScrollPane criarPainelScrollDeMedicos() {
    buscarMedicos();
    listaMedicos = new JList(medicosDescricao);
    listaMedicos.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    listaMedicos.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    listaMedicos.setVisibleRowCount(-1);
    listaMedicos.setFixedCellHeight(24);
    listaMedicos.setFixedCellWidth(450);
    listaMedicos.setFont(new Font("Arial", Font.PLAIN, 12));
    listaMedicos.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (!event.getValueIsAdjusting()) {
          descricaoSelecionada = ((JList<?>) event.getSource()).getSelectedValue().toString();
        }
      }
    });
    listaMedicos.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2 && descricaoSelecionada != null) {
          for (Medico medico : medicos) {
            if (gerarDescricaoMedico(medico).equals(descricaoSelecionada)) {
              new View.Medico(medico).setVisible(true);
              dispose();
              break;
            }
          }
        }
      }
    });
    JScrollPane listaMedicosScroll = new JScrollPane(listaMedicos);
    listaMedicosScroll.setPreferredSize(new Dimension(450, 450));
    return listaMedicosScroll;
  }

  private JPanel criarPainelDeMedicos() {
    JPanel painelDeMedicos = new JPanel();
    painelDeMedicos.setBackground(Color.WHITE);
    JScrollPane painelScrollDeMedicos = criarPainelScrollDeMedicos();
    painelDeMedicos.add(painelScrollDeMedicos);
    return painelDeMedicos;
  }

  Medicos() {
    setTitle("Médicos");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(1240, 820);
    getContentPane().setBackground(Color.WHITE);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = null;

    JLabel rotuloTitulo = new JLabel("Médicos");
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
    painelCorpo.add(criarPainelDeMedicos());
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
        new Medicos().setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
