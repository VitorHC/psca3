package Model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Funcionario extends Pessoa {
  private String senha = "";

  public String getSenha() {
    return senha;
  }

  public Boolean setSenha(String senha) {
    if (senha.length() < 5) {
      return false;
    }
    this.senha = senha;
    return true;
  }

  public static void main(String[] args) {
   Funcionario func = new Funcionario();
   func.setNome("Aluisio");
   func.setDataDeNascimento("21/06/1994");
   func.setEmail("aluisioordones1@gmail.com");
   func.setEndereco("Rua São Romão");
   func.setCelular("85858585");
   func.setTelefone("86868686");
   func.setSenha("12345678");
   try {
     func.criar();
   } catch (Exception exception) {
     System.out.println(exception.getMessage());
   }

//    func.setNome("Raimundo");
//
//    try {
//      func.atualizar();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
//
//    try {
//      func.remover();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }

//    try {
//      Funcionario f = login("aluisioordones1@gmail.com", "12345678");
//      if (f == null) {
//        System.out.println("Falha no login");
//      } else {
//        System.out.println(f.nome);
//        System.out.println(f.email);
//        System.out.println(f.id);
//        System.out.println(f.dataDeNascimento);
//        System.out.println(f.telefone);
//        System.out.println(f.celular);
//        System.out.println(f.senha.isEmpty());
//      }
//    } catch (Exception except) {
//      System.out.println(except.getMessage());
//    }
  }

  public Boolean souFuncionario(Conexao banco) throws SQLException {
    Boolean fecharConexao = false;
    if (banco == null) {
      banco = new Conexao();
      fecharConexao = true;
    }
    if (banco.con == null) {
      banco.AbrirConexao();
      fecharConexao = true;
    }
    String sql = "SELECT * FROM pessoas AS `p` INNER JOIN funcionarios AS `f` ON `p`.id = `f`.pessoaId WHERE `p`.id = ?";
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
      throw new EntidadeInvalida(excep.getMessage(), "Funcionário");
    }
    if (senha.isEmpty()) {
      throw new EntidadeInvalida("Senha inválida", "Funcionário");
    }
  }

  @Override public void criar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar funcionário que já possui um id", "Funcionário");
    }

    validar();

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    try (PreparedStatement ps = banco.con.prepareStatement("SELECT * FROM pessoas WHERE email = ?")) {
      ps.setString(1, email);
      ResultSet rst = ps.executeQuery();
      if (rst.next()) {
        banco.FecharConexao();
        throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
      }
    }

    banco.con.setAutoCommit(false);

    String sql = "INSERT INTO pessoas(nome, endereco, email, dataDeNascimento, telefone, celular) VALUES(?, ?, ?, ?, ?, ?)";
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

    sql = "INSERT INTO funcionarios(pessoaId, senha) VALUES (?, ?)";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.setString(2, senha);
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
      throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem um id", "Funcionário");
    }

    validar();

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    if (!souFuncionario(banco)) {
      banco.FecharConexao();
      throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem registro", "Funcionário");
    }

    String sql = "SELECT * FROM pessoas WHERE email = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, email);
      ResultSet rst = ps.executeQuery();
      if (rst.next()) {
        Long idExistente = rst.getLong("id");
        if (!idExistente.equals(id)) {
          throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
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

    sql = "UPDATE funcionarios SET senha = ? WHERE pessoaId = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, senha);
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
      throw new ConflitoDeEntidade("Tentativa de remover funcionário sem id", "Funcionário");
    }

    Conexao banco = new Conexao();
    banco.AbrirConexao();

    if (!souFuncionario(banco)) {
      banco.FecharConexao();
      throw new ConflitoDeEntidade(
          "Tentativa de remover pessoa que não existe ou que não é um funcionário", "Funcionário");
    }

    String sql = "DELETE FROM pessoas WHERE id = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.execute();
    } finally {
      banco.FecharConexao();
    }
  }

  static public Funcionario login(String email, String senha) throws SQLException {
    Conexao banco = new Conexao();
    banco.AbrirConexao();
    String sql = "SELECT * FROM funcionarios AS `f` INNER JOIN pessoas AS `p` ON `f`.pessoaId = `p`.id WHERE email = ? AND senha = ?";
    try (PreparedStatement ps = banco.con.prepareStatement(sql)) {
      ps.setString(1, email);
      ps.setString(2, senha);
      ResultSet result = ps.executeQuery();
      if (result.next()) {
        Funcionario f = new Funcionario();
        f.id = result.getLong("id");
        f.nome = result.getString("nome");
        f.email = result.getString("email");
        f.endereco = result.getString("endereco");
        f.dataDeNascimento = result.getString("dataDeNascimento");
        f.celular = result.getString("celular");
        f.telefone = result.getString("telefone");
        f.senha = "";

      
        return f;
      }
    } finally {
      banco.FecharConexao();
    }
    return null;
  }
}
