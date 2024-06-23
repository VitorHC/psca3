package View;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.ConflitoDeEntidade;

public class Medico extends JFrame {

  private Model.Medico medico;
  private JPanel painelDeInformacoes;
  private int informacaoGridY = 0;
  private JButton botaoAtualizar, botaoVoltar, botaoDelete;

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

  private void criarPainelDeInformacoes(Model.Medico medico) {
    painelDeInformacoes = new JPanel(new GridBagLayout());
    adicionarInformacao("Nome:", medico.getNome());
    adicionarInformacao("CRM:", medico.getCRM());
    adicionarInformacao("Especialidade:", medico.getEspecialidade());
    adicionarInformacao("Data de Nascimento:", medico.getDataDeNascimento());
    adicionarInformacao("E-mail:", medico.getEmail());
    adicionarInformacao("Telefone:", medico.getTelefone());
    adicionarInformacao("Celular:", medico.getCelular());
    adicionarInformacao("Endereço:", medico.getEndereco());
  }

  private void criarBotaoAtualizar() {
    botaoAtualizar = new JButton("Atualizar");
    botaoAtualizar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        new MedicoForm(medico).setVisible(true);
        dispose();
      }
    });
  }

  private void criarBotaoVoltar(){
    botaoVoltar = new JButton("Voltar");
    botaoVoltar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event){
        new Medicos().setVisible(true);
        dispose();
      }
    });

  }

  private void deletarbotao(){
    botaoDelete = new JButton("Deletar");

    botaoDelete.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event){

      try {
        medico.remover();
        new Medicos().setVisible(true);
        dispose();
        
      } catch (ConflitoDeEntidade | SQLException e) {
        e.printStackTrace();
      }
     
      }
    });
  }

  Medico(Model.Medico m) {
    medico = m;
    setTitle("Médico " + medico.getNome());
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

    JLabel rotuloTitulo = new JLabel("Médico");
    rotuloTitulo.setBackground(Color.WHITE);
    rotuloTitulo.setFont(new Font("Arial", Font.BOLD, 18));
    painelPrincipal.add(rotuloTitulo);

    criarPainelDeInformacoes(medico);
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

    deletarbotao();
    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.PAGE_START;
    gbc.gridy = 2;
    painelPrincipal.add(botaoDelete, gbc);
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      try {
        Model.Medico medico = new Model.Medico();
        medico.setNome("Oswaldinho");
        medico.setDataDeNascimento("22/06/1997");
        medico.setEmail("oswaldo3@gmail.com");
        medico.setEndereco("Rua do Oswaldo");
        medico.setCelular("91919191");
        medico.setTelefone("81818181");
        medico.setCRM("CRM/SP 123456");
        medico.setEspecialidade("Cardiologia");
        new Medico(medico).setVisible(true);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
