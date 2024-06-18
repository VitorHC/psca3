package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

  public void setEspecialidade(String especialidade) {
    this.especialidade = especialidade;
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

  @Override public void validar() throws EntidadeInvalida {
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

  @Override public void criar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar médico que já possui um id", "Médico");
    }

    validar();

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM pessoas AS `pe` INNER JOIN medicos AS `me` ON `pe`.id = `me`.pessoaId WHERE email = ? OR crm = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, email);
        ps.setString(2, crm);
        ResultSet rst = ps.executeQuery();
        while (rst.next()) {
          if (rst.getString("email").equals(email)) {
            throw new ConflitoDeEntidade("E-mail em uso", "Médico");
          }
          throw new ConflitoDeEntidade("CRM em uso", "Médico");
        }
      }

      conexao.setAutoCommit(false);

      sql = "INSERT INTO pessoas(nome, endereco, email, dataDeNascimento, telefone, celular) VALUES(?, ?, ?, ?, ?, ?)";
      try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, nome);
        ps.setString(2, endereco);
        ps.setString(3, email);
        ps.setString(4, dataDeNascimento);
        ps.setString(5, telefone);
        ps.setString(6, celular);
        ps.execute();
        ResultSet rst = ps.getGeneratedKeys();
        if (rst.next()) {
          id = rst.getLong(1);
        }
      } catch (Exception e) {
        conexao.rollback();
        throw e;
      }

      sql = "INSERT INTO medicos(pessoaId, crm, especialidade) VALUES (?, ?, ?)";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setLong(1, id);
        ps.setString(2, crm);
        ps.setString(3, especialidade);
        ps.execute();
      } catch (Exception e) {
        conexao.rollback();
        throw e;
      }

      conexao.commit();
    }
  }

  @Override public void atualizar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de atualizar médico sem um id", "Médico");
    }

    validar();

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      if (!souMedico(conexao)) {
        throw new ConflitoDeEntidade("Tentativa de atualizar médico sem registro", "Médico");
      }
      String sql = "SELECT * FROM medicos AS `me` INNER JOIN pessoas AS `pe` ON `me`.pessoaId = `pe`.id WHERE email = ? OR crm = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, email);
        ps.setString(2, crm);
        ResultSet rst = ps.executeQuery();
        while (rst.next()) {
          Long idExistente = rst.getLong("id");
          if (!idExistente.equals(id)) {
            if (rst.getString("email").equals(email)) {
              throw new ConflitoDeEntidade("E-mail em uso", "Médico");
            }
            throw new ConflitoDeEntidade("CRM em uso", "Médico");
          }
        }
      }

      conexao.setAutoCommit(false);

      sql = "UPDATE pessoas SET nome = ?, endereco = ?, email = ?, dataDeNascimento = ?, telefone = ?, celular = ? WHERE id = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, nome);
        ps.setString(2, endereco);
        ps.setString(3, email);
        ps.setString(4, dataDeNascimento);
        ps.setString(5, telefone);
        ps.setString(6, celular);
        ps.setLong(7, id);
        ps.execute();
      } catch (Exception e) {
        conexao.rollback();
        throw e;
      }

      sql = "UPDATE medicos SET crm = ?, especialidade = ? WHERE pessoaId = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, crm);
        ps.setString(2, especialidade);
        ps.setLong(3, id);
        ps.execute();
      } catch (Exception e) {
        conexao.rollback();
        throw e;
      }

      conexao.commit();
    }
  }

  @Override public void remover() throws SQLException, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de remover médico sem id", "Médico");
    }

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      if (!souMedico(conexao)) {
        throw new ConflitoDeEntidade(
            "Tentativa de remover pessoa que não existe ou que não é um médico", "Médico");
      }
      String sql = "DELETE FROM pessoas WHERE id = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setLong(1, id);
        ps.execute();
      }
    }
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
          medico.id = result.getLong("id");
          medico.nome = result.getString("nome");
          medico.endereco = result.getString("endereco");
          medico.email = result.getString("email");
          medico.dataDeNascimento = result.getString("dataDeNascimento");
          medico.crm = result.getString("crm");
          medico.celular = result.getString("celular");
          medico.telefone = result.getString("telefone");
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
          medico.id = result.getLong("id");
          medico.nome = result.getString("nome");
          medico.endereco = result.getString("endereco");
          medico.email = result.getString("email");
          medico.dataDeNascimento = result.getString("dataDeNascimento");
          medico.crm = result.getString("crm");
          medico.celular = result.getString("celular");
          medico.telefone = result.getString("telefone");
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

    try {
      novoMedico.remover();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
  }
}
