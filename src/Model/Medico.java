package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Medico extends Pessoa {

  private String crm = "";

  public String getCRM() {
    return crm;
  }

  public boolean setCRM(String crm) {
    // CRM/SP 123456
    if (crm.length() == 13) {
      this.crm = crm;
      return true;
    }
    return false;
  }

  private String especialidade = "";

  public String getEspecialidade() {
    return especialidade;
  }

  public Boolean setEspecialidade(String especialidade) {
    if (especialidade.length() < 3) {
      return false;
    }
    this.especialidade = especialidade;
    return true;
  }

  public Boolean souMedico(Connection conexao) throws SQLException {
    Boolean fecharConexao = false;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      fecharConexao = true;
    }
    String sql = "SELECT * FROM pessoas AS `pe` INNER JOIN medicos AS `me` ON `pe`.id = `me`.pessoaId WHERE `pe`.id = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setLong(1, id);
      ResultSet rst = ps.executeQuery();
      if (!rst.next()) {
        return false;
      }
    } finally {
      if (fecharConexao) {
        conexao.close();
      }
    }
    return true;
  }

  @Override
  public void validar() throws EntidadeInvalida {
    try {
      super.validar();
    } catch (EntidadeInvalida excep) {
      throw new EntidadeInvalida(excep.getMessage(), "Médico");
    }
    if (crm.isEmpty()) {
      throw new EntidadeInvalida("CPF inválido", "Médico");
    }
    if (especialidade.isEmpty()) {
      throw new EntidadeInvalida("Médico sem especialidade", "Médico");
    }
  }

  @Override
  protected void mapear(ResultSet resultSet) throws SQLException {
    super.mapear(resultSet);
    crm = resultSet.getString("crm");
    especialidade = resultSet.getString("especialidade");
  }

  protected Boolean possuoCRMUnico(Connection conexao) throws SQLException {
    boolean manterConexao = true;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      manterConexao = false;
    }
    boolean crmUnico = true;
    String sql = "SELECT * FROM medicos WHERE crm = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, crm);
      ResultSet rst = ps.executeQuery();
      if (rst.next() && rst.getLong("pessoaId") != id) {
        crmUnico = false;
      }
    }
    if (!manterConexao) {
      conexao.close();
    }
    return crmUnico;
  }

  @Override
  protected void criar(Connection conexao)
      throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar médico que já possui um id", "Médico");
    }

    validar();

    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Médico");
    }
    if (!possuoCRMUnico(conexao)) {
      throw new ConflitoDeEntidade("CRM em uso", "Médico");
    }

    conexao.setAutoCommit(false);

    super.criar(conexao);

    String sql = "INSERT INTO medicos(pessoaId, crm, especialidade) VALUES (?, ?, ?)";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.setString(2, crm);
      ps.setString(3, especialidade);
      ps.execute();
    }

    conexao.commit();
  }

  @Override
  protected void atualizar(Connection conexao)
      throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de atualizar médico sem um id", "Médico");
    }

    validar();

    if (!souMedico(conexao)) {
      throw new ConflitoDeEntidade("Tentativa de atualizar médico sem registro", "Médico");
    }
    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Médico");
    }
    if (!possuoCRMUnico(conexao)) {
      throw new ConflitoDeEntidade("CRM em uso", "Médico");
    }

    super.atualizar(conexao);

    String sql = "UPDATE medicos SET crm = ?, especialidade = ? WHERE pessoaId = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, crm);
      ps.setString(2, especialidade);
      ps.setLong(3, id);
      ps.execute();
    }
  }

  @Override
  protected void remover(Connection conexao) throws SQLException, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de remover médico sem id", "Médico");
    }
    if (!souMedico(conexao)) {
      throw new ConflitoDeEntidade(
          "Tentativa de remover pessoa que não existe ou que não é um médico", "Médico");
    }
    super.remover(conexao);
  }

  public static Optional<Medico> buscar(String crm) throws SQLException {
    Medico medico = null;
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM medicos AS `me` INNER JOIN pessoas AS `pe` ON `me`.pessoaId = `pe`.id WHERE `me`.crm = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, crm);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
          medico = new Medico();
          medico.mapear(result);
        }
      }
    }
    return Optional.ofNullable(medico);
  }

  public static List<Medico> buscarTodos() throws SQLException {
    List<Medico> medicos = new ArrayList<>();
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM medicos AS `me` INNER JOIN pessoas AS `pe` ON `me`.pessoaId = `pe`.id";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ResultSet result = ps.executeQuery();
        while (result.next()) {
          Medico medico = new Medico();
          medico.mapear(result);
          medicos.add(medico);
        }
      }
    }
    return medicos;
  }

  public static void main(String[] args) {
    // fluxo de teste que parte-se do pressuposto que o médico não exista no banco
    Medico novoMedico = new Medico();
    novoMedico.setNome("Oswaldinho");
    novoMedico.setDataDeNascimento("22/06/1997");
    novoMedico.setEmail("oswaldo3@gmail.com");
    novoMedico.setEndereco("Rua do Oswaldo");
    novoMedico.setCelular("91919191");
    novoMedico.setTelefone("81818181");
    novoMedico.setCRM("CRM/SP 123456");
    novoMedico.setEspecialidade("Cardiologia");
    try {
      novoMedico.criar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
    novoMedico.setNome("Oswaldao");
    try {
      novoMedico.atualizar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }

    try {
      Optional<Medico> optPaciente = buscar("CRM/SP 123456");
      if (optPaciente.isEmpty()) {
        System.out.println("médico não encontrado");
      } else {
        Medico medico = optPaciente.get();
        System.out.println(medico.id);
        System.out.println(medico.nome);
        System.out.println(medico.email);
        System.out.println(medico.endereco);
        System.out.println(medico.crm);
        System.out.println(medico.especialidade);
        System.out.println(medico.dataDeNascimento);
        System.out.println(medico.celular);
        System.out.println(medico.telefone);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    try {
      for (Medico medico : buscarTodos()) {
        System.out.println(medico.id);
        System.out.println(medico.nome);
        System.out.println(medico.email);
        System.out.println(medico.endereco);
        System.out.println(medico.crm);
        System.out.println(medico.especialidade);
        System.out.println(medico.dataDeNascimento);
        System.out.println(medico.celular);
        System.out.println(medico.telefone);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

//    try {
//      novoMedico.remover();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
  }
}
