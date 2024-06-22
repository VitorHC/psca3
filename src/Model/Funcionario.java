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

  @Override
  public void validar() throws EntidadeInvalida {
    try {
      super.validar();
    } catch (EntidadeInvalida excep) {
      throw new EntidadeInvalida(excep.getMessage(), "Funcionário");
    }
    if (senha.isEmpty()) {
      throw new EntidadeInvalida("Senha inválida", "Funcionário");
    }
  }

  @Override
  protected void mapear(ResultSet resultSet) throws SQLException {
    super.mapear(resultSet);
    senha = "";
  }

  @Override
  protected void criar(Connection conexao)
      throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar funcionário que já possui um id",
          "Funcionário");
    }

    validar();

    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
    }

    super.criar(conexao);

    String sql = "INSERT INTO funcionarios(pessoaId, senha) VALUES (?, ?)";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.setString(2, senha);
      ps.execute();
    }
  }

  @Override
  protected void atualizar(Connection conexao)
      throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem um id", "Funcionário");
    }

    validar();

    if (!souFuncionario(conexao)) {
      throw new ConflitoDeEntidade("Tentativa de atualizar funcionário sem registro",
          "Funcionário");
    }
    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Funcionário");
    }

    super.atualizar(conexao);

    String sql = "UPDATE funcionarios SET senha = ? WHERE pessoaId = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, senha);
      ps.setLong(2, id);
      ps.execute();
    }
  }

  @Override
  protected void remover(Connection conexao) throws SQLException, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de remover funcionário sem id", "Funcionário");
    }
    if (!souFuncionario(conexao)) {
      throw new ConflitoDeEntidade(
          "Tentativa de remover pessoa que não existe ou que não é um funcionário",
          "Funcionário");
    }
    super.remover(conexao);
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
          f.mapear(result);
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
