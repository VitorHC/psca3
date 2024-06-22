package View;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Paciente extends JFrame {
  private Model.Paciente paciente;
  private JPanel painelDeInformacoes;
  private int informacaoGridY = 0;
  private JButton botaoAtualizar,botaoVoltar,botaoDelete;

  private void adicionarInformacao(String nomeCampo, String valorCampo) {
    JLabel rotulo = new JLabel(nomeCampo);
    rotulo.setFont(new Font("Arial", Font.BOLD, 14));
    JLabel campo = new JLabel(valorCampo);
    campo.setFont(new Font("Verdana", Font.PLAIN, 12));
    informacaoGridY++;
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 8 , 6, 8);
    gbc.anchor = GridBagConstraints.LINE_START;
    gbc.gridy = informacaoGridY;
    gbc.gridx = 1;
    painelDeInformacoes.add(rotulo, gbc);
    gbc.gridy = informacaoGridY;
    gbc.gridx = 2;
    gbc.weightx = 1;
    painelDeInformacoes.add(campo, gbc);
  }

  private void criarPainelDeInformacoes() {
    painelDeInformacoes = new JPanel(new GridBagLayout());
    adicionarInformacao("Nome:", paciente.getNome());
    adicionarInformacao("CPF:", paciente.getCPF());
    adicionarInformacao("Data de Nascimento:", paciente.getDataDeNascimento());
    adicionarInformacao("E-mail:", paciente.getEmail());
    adicionarInformacao("Telefone:", paciente.getTelefone());
    adicionarInformacao("Celular:", paciente.getCelular());
    adicionarInformacao("Endereço:", paciente.getEndereco());
  }

  private void criarBotaoAtualizar() {
    botaoAtualizar = new JButton("Atualizar");
    botaoAtualizar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        new PacienteForm(paciente).setVisible(true);
        dispose();
      }
    });
  }

  private void criarBotaoVoltar(){
    botaoVoltar = new JButton("Voltar");
    botaoVoltar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event){
        new Pacientes().setVisible(true);
        dispose();
      }
    });

  }



  Paciente(Model.Paciente p) {
    paciente = p;
    setTitle("Paciente " + paciente.getNome());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setSize(500, 400);
    setLocationRelativeTo(null);
    getContentPane().setBackground(Color.WHITE);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = null;

    JPanel painelPrincipal = new JPanel();
    painelPrincipal.setLayout(new GridBagLayout());
    painelPrincipal.setBackground(Color.WHITE);

    JLabel rotuloTitulo = new JLabel("Paciente");
    rotuloTitulo.setBackground(Color.WHITE);
    rotuloTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    painelPrincipal.add(rotuloTitulo);

    criarPainelDeInformacoes();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(20, 0, 20, 0);
    painelPrincipal.add(painelDeInformacoes, gbc);

    criarBotaoAtualizar();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 2;
    painelPrincipal.add(botaoAtualizar, gbc);

    criarBotaoVoltar();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 2;
    painelPrincipal.add(botaoVoltar, gbc);

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 1;
    gbc.weighty = 1;
    add(painelPrincipal, gbc);
  }

  private void deletarbotao(){
    botaoDelete = new JButton("Deletar");
    botaoDelete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event){
     

     new Pacientes().setVisible(true);
      dispose();
      }
    });
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        Model.Paciente p = new Model.Paciente();
        p.setNome("Oswaldinho");
        p.setDataDeNascimento("22/06/1997");
        p.setEmail("oswaldo3@gmail.com");
        p.setEndereco("Rua do Oswaldo");
        p.setCelular("91919191");
        p.setTelefone("81818181");
        p.setCPF("134.134.134-13");
        new Paciente(p).setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}


