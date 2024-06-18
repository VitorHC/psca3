package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Paciente extends Pessoa {
  private String cpf = "";

  public String getCPF() {
    return cpf;
  }

  public Boolean setCPF(String cpf) {
    if (cpf.length() != 14) {
      return false;
    }
    this.cpf = cpf;
    return true;
  }

  public static void main(String[] args) {
   Paciente paciente = new Paciente();
   paciente.setNome("Vitor3");
    paciente.setDataDeNascimento("22/06/1999");
    paciente.setEmail("vv3@gmail.com");
   paciente.setEndereco("Rua Do vitor2");
   paciente.setCelular("1122111");
   paciente.setTelefone("2221122");
   paciente.setCPF("137.145.155-15");
   try {
     paciente.criar();
   } catch (Exception exception) {
     System.out.println(exception.getMessage());
   }

//    try {
//      paciente.atualizar();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
//
//    try {
//      paciente.remover();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
//    try {
//      Optional<Paciente> optPaciente = buscar("137.145.155-15");
//      if (optPaciente.isEmpty()) {
//        System.out.println("paciente não encontrado");
//      } else {
//        Paciente paciente = optPaciente.get();
//        System.out.println(paciente.id);
//        System.out.println(paciente.nome);
//        System.out.println(paciente.email);
//        System.out.println(paciente.endereco);
//        System.out.println(paciente.cpf);
//        System.out.println(paciente.dataDeNascimento);
//        System.out.println(paciente.celular);
//        System.out.println(paciente.telefone);
//      }
//    } catch (Exception e) {
//      System.out.println(e.getMessage());
//    }

//    try {
//      for (Paciente paciente : buscarTodos()) {
//        System.out.println(paciente.id);
//        System.out.println(paciente.nome);
//        System.out.println(paciente.email);
//        System.out.println(paciente.endereco);
//        System.out.println(paciente.cpf);
//        System.out.println(paciente.dataDeNascimento);
//        System.out.println(paciente.celular);
//        System.out.println(paciente.telefone);
//      }
//    } catch (Exception e) {
//      System.out.println(e.getMessage());
//    }

  }

  public Boolean souPaciente(Conexao banco) throws SQLException {
    Boolean fecharConexao = false;
    if (banco == null) {
      banco = new Conexao();
      fecharConexao = true;
    }
    if (banco.con == null) {
      banco.AbrirConexao();
      fecharConexao = true;
    }
    String sql = "SELECT * FROM pessoas AS `pe` INNER JOIN pacientes AS `pa` ON `pe`.id = `pa`.pessoaId WHERE `pe`.id = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setLong(1, id);
      ResultSet rst = ps.executeQuery();
      if (!rst.next()) {
        return false;
      }
    } finally {
      if (fecharConexao) {
        banco.FecharConexao();
      }
    }
    return true;
  }

  @Override public void validar() throws EntidadeInvalida {
    try {
      super.validar();
    } catch (EntidadeInvalida excep) {
      throw new EntidadeInvalida(excep.getMessage(), "Paciente");
    }
    if (cpf.isEmpty()) {
      throw new EntidadeInvalida("CPF inválido", "Paciente");
    }
  }

  @Override public void criar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar paciente que já possui um id", "Paciente");
    }

    validar();

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    String sql = "SELECT * FROM pessoas AS `pe` INNER JOIN pacientes AS `pa` ON `pe`.id = `pa`.pessoaId WHERE email = ? OR cpf = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.setString(2, cpf);
      ResultSet rst = ps.executeQuery();
      while (rst.next()) {
        if (rst.getString("email").equals(email)) {
          throw new ConflitoDeEntidade("E-mail em uso", "Paciente");
        }
        throw new ConflitoDeEntidade("CPF em uso", "Paciente");
      }
    } catch (Exception e) {
      banco.FecharConexao();
      throw e;
    }

    banco.con.setAutoCommit(false);

    sql = "INSERT INTO pessoas(nome, endereco, email, dataDeNascimento, telefone, celular) VALUES(?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = banco.con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
      banco.con.rollback();
      banco.FecharConexao();
      throw e;
    }

    sql = "INSERT INTO pacientes(pessoaId, cpf) VALUES (?, ?)";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.setString(2, cpf);
      ps.execute();
    } catch (Exception e) {
      banco.con.rollback();
      banco.FecharConexao();
      throw e;
    }

    banco.con.commit();
    banco.FecharConexao();
  }

  @Override public void atualizar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de atualizar paciente sem um id", "Paciente");
    }

    validar();

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    if (!souPaciente(banco)) {
      banco.FecharConexao();
      throw new ConflitoDeEntidade("Tentativa de atualizar paciente sem registro", "Paciente");
    }

    String sql = "SELECT * FROM pacientes AS `pa` INNER JOIN pessoas AS `pe` ON `pa`.pessoaId = `pe`.id WHERE email = ? OR cpf = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.setString(2, cpf);
      ResultSet rst = ps.executeQuery();
      while (rst.next()) {
        Long idExistente = rst.getLong("id");
        if (!idExistente.equals(id)) {
          if (rst.getString("email").equals(email)) {
            throw new ConflitoDeEntidade("E-mail em uso", "Paciente");
          }
          throw new ConflitoDeEntidade("CPF em uso", "Paciente");
        }
      }
    } catch (Exception e) {
      banco.FecharConexao();
      throw e;
    }

    banco.con.setAutoCommit(false);

    sql = "UPDATE pessoas SET nome = ?, endereco = ?, email = ?, dataDeNascimento = ?, telefone = ?, celular = ? WHERE id = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, nome);
      ps.setString(2, endereco);
      ps.setString(3, email);
      ps.setString(4, dataDeNascimento);
      ps.setString(5, telefone);
      ps.setString(6, celular);
      ps.setLong(7, id);
      ps.execute();
    } catch (Exception e) {
      banco.con.rollback();
      banco.FecharConexao();
      throw e;
    }

    sql = "UPDATE pacientes SET cpf = ? WHERE pessoaId = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, cpf);
      ps.setLong(2, id);
      ps.execute();
    } catch (Exception e) {
      banco.con.rollback();
      banco.FecharConexao();
      throw e;
    }

    banco.con.commit();
    banco.FecharConexao();
  }

  @Override public void remover() throws SQLException, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de remover paciente sem id", "Paciente");
    }

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    if (!souPaciente(banco)) {
      banco.FecharConexao();
      throw new ConflitoDeEntidade(
          "Tentativa de remover pessoa que não existe ou que não é um paciente", "Paciente");
    }

    String sql = "DELETE FROM pessoas WHERE id = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.execute();
    } finally {
      banco.FecharConexao();
    }
  }

  public static Optional<Paciente> buscar(String cpf) throws SQLException {
    Conexao banco = new Conexao();
    banco.AbrirConexao();

    Paciente paciente = null;
    String sql = "SELECT * FROM pacientes AS `pa` INNER JOIN pessoas AS `pe` ON `pa`.pessoaId = `pe`.id WHERE `pa`.cpf = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, cpf);
      ResultSet result = ps.executeQuery();
      if (result.next()) {
        paciente = new Paciente();
        paciente.id = result.getLong("id");
        paciente.nome = result.getString("nome");
        paciente.endereco = result.getString("endereco");
        paciente.email = result.getString("email");
        paciente.dataDeNascimento = result.getString("dataDeNascimento");
        paciente.cpf = result.getString("cpf");
        paciente.celular = result.getString("celular");
        paciente.telefone = result.getString("telefone");
      }
    }
    banco.FecharConexao();
    return Optional.ofNullable(paciente);
  }

  public static List<Paciente> buscarTodos() throws SQLException {
    Conexao banco = new Conexao();
    banco.AbrirConexao();

    List<Paciente> pacientes = new ArrayList<>();

    String sql = "SELECT * FROM pacientes AS `pa` INNER JOIN pessoas AS `pe` ON `pa`.pessoaId = `pe`.id";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ResultSet result = ps.executeQuery();
      while (result.next()) {
        Paciente paciente = new Paciente();
        paciente.id = result.getLong("id");
        paciente.nome = result.getString("nome");
        paciente.endereco = result.getString("endereco");
        paciente.email = result.getString("email");
        paciente.dataDeNascimento = result.getString("dataDeNascimento");
        paciente.cpf = result.getString("cpf");
        paciente.celular = result.getString("celular");
        paciente.telefone = result.getString("telefone");
        pacientes.add(paciente);
      }
    }

    return pacientes;
  }
}
