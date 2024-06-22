package Model;

import java.sql.Connection;
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

  public Boolean souFuncionario(Connection conexao) throws SQLException {
    Boolean fecharConexao = false;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      fecharConexao = true;
    }
    String sql = "SELECT * FROM pessoas AS `p` INNER JOIN funcionarios AS `f` ON `p`.id = `f`.pessoaId WHERE `p`.id = ?";
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

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      try (PreparedStatement ps = conexao.prepareStatement("SELECT * FROM pessoas WHERE email = ?")) {
        ps.setString(1, email);
        ResultSet rst = ps.executeQuery();
        if (rst.next()) {
          throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
        }
      }

      conexao.setAutoCommit(false);

      String sql = "INSERT INTO pessoas(nome, endereco, email, dataDeNascimento, telefone, celular) VALUES(?, ?, ?, ?, ?, ?)";
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

      sql = "INSERT INTO funcionarios(pessoaId, senha) VALUES (?, ?)";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setLong(1, id);
        ps.setString(2, senha);
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
      throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem um id", "Funcionário");
    }

    validar();

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      if (!souFuncionario(conexao)) {
        throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem registro", "Funcionário");
      }

      String sql = "SELECT * FROM pessoas WHERE email = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, email);
        ResultSet rst = ps.executeQuery();
        if (rst.next()) {
          Long idExistente = rst.getLong("id");
          if (!idExistente.equals(id)) {
            throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
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

      sql = "UPDATE funcionarios SET senha = ? WHERE pessoaId = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, senha);
        ps.setLong(2, id);
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
      throw new ConflitoDeEntidade("Tentativa de remover funcionário sem id", "Funcionário");
    }
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      if (!souFuncionario(conexao)) {
        throw new ConflitoDeEntidade(
            "Tentativa de remover pessoa que não existe ou que não é um funcionário", "Funcionário");
      }
      String sql = "DELETE FROM pessoas WHERE id = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setLong(1, id);
        ps.execute();
      }
    }
  }

  static public Funcionario login(String email, String senha) throws SQLException {
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM funcionarios AS `f` INNER JOIN pessoas AS `p` ON `f`.pessoaId = `p`.id WHERE email = ? AND senha = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
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
      }
    }
    return null;
  }

  public static void main(String[] args) {
    // fluxo de teste que parte-se do pressuposto que o funcionário não exista no banco
    Funcionario func = new Funcionario();
    func.setNome("Aluisio");
    func.setDataDeNascimento("21/06/1994");
    func.setEmail("aluisio@gmail.com");
    func.setEndereco("Rua São Romão");
    func.setCelular("85858585");
    func.setTelefone("86868686");
    func.setSenha("12345");
    try {
      func.criar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
    func.setNome("Raimundo");
    try {
      func.atualizar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }
    try {
      Funcionario f = login("aluisio@gmail.com", "12345");
      if (f == null) {
        System.out.println("Falha no login");
      } else {
        System.out.println(f.nome);
        System.out.println(f.email);
        System.out.println(f.id);
        System.out.println(f.dataDeNascimento);
        System.out.println(f.telefone);
        System.out.println(f.celular);
        System.out.println(f.senha.isEmpty());
      }
    } catch (Exception except) {
      System.out.println(except.getMessage());
    }
//    try {
//      func.remover();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
  }
}
