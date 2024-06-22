package Model;

import java.sql.Connection;
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

  public Boolean souPaciente(Connection conexao) throws SQLException {
    Boolean fecharConexao = false;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      fecharConexao = true;
    }
    String sql = "SELECT * FROM pessoas AS `pe` INNER JOIN pacientes AS `pa` ON `pe`.id = `pa`.pessoaId WHERE `pe`.id = ?";
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
      throw new EntidadeInvalida(excep.getMessage(), "Paciente");
    }
    if (cpf.isEmpty()) {
      throw new EntidadeInvalida("CPF inválido", "Paciente");
    }
  }

  @Override
  protected void mapear(ResultSet resultSet) throws SQLException {
    super.mapear(resultSet);
    cpf = resultSet.getString("cpf");
  }

  protected Boolean possuoCPFUnico(Connection conexao) throws SQLException {
    boolean manterConexao = true;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      manterConexao = false;
    }
    boolean crmUnico = true;
    String sql = "SELECT * FROM pacientes WHERE cpf = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, cpf);
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

  @Override protected void criar(Connection conexao) throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    if (id > 0) {
      throw new ConflitoDeEntidade("Tentativa de criar paciente que já possui um id", "Paciente");
    }

    validar();

    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Paciente");
    }
    if (!possuoCPFUnico(conexao)) {
      throw new ConflitoDeEntidade("CPF em uso", "Paciente");
    }

    super.criar(conexao);

    String sql = "INSERT INTO pacientes(pessoaId, cpf) VALUES (?, ?)";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.setString(2, cpf);
      ps.execute();
    }
  }

  @Override protected void atualizar(Connection conexao) throws SQLException, ConflitoDeEntidade, EntidadeInvalida {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de atualizar paciente sem um id", "Paciente");
    }

    validar();

    if (!souPaciente(conexao)) {
      throw new ConflitoDeEntidade("Tentativa de atualizar paciente sem registro", "Paciente");
    }
    if (!possuoEmailUnico(conexao)) {
      throw new ConflitoDeEntidade("E-mail em uso", "Paciente");
    }
    if (!possuoCPFUnico(conexao)) {
      throw new ConflitoDeEntidade("CPF em uso", "Paciente");
    }

    super.atualizar(conexao);

    String sql = "UPDATE pacientes SET cpf = ? WHERE pessoaId = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, cpf);
      ps.setLong(2, id);
      ps.execute();
    }
  }

  @Override public void remover() throws SQLException, ConflitoDeEntidade {
    if (id == 0) {
      throw new ConflitoDeEntidade("Tentativa de remover paciente sem id", "Paciente");
    }

    try (Connection conexao = BancoDeDados.pegarConexao()) {
      if (!souPaciente(conexao)) {
        throw new ConflitoDeEntidade(
            "Tentativa de remover pessoa que não existe ou que não é um paciente", "Paciente");
      }
      super.remover(conexao);
    }
  }

  public static Optional<Paciente> buscar(String cpf) throws SQLException {
    Paciente paciente = null;
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM pacientes AS `pa` INNER JOIN pessoas AS `pe` ON `pa`.pessoaId = `pe`.id WHERE `pa`.cpf = ?";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ps.setString(1, cpf);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
          paciente = new Paciente();
          paciente.mapear(result);
        }
      }
    }
    return Optional.ofNullable(paciente);
  }

  public static List<Paciente> buscarTodos() throws SQLException {
    List<Paciente> pacientes = new ArrayList<>();
    try (Connection conexao = BancoDeDados.pegarConexao()) {
      String sql = "SELECT * FROM pacientes AS `pa` INNER JOIN pessoas AS `pe` ON `pa`.pessoaId = `pe`.id";
      try (PreparedStatement ps = conexao.prepareStatement(sql)) {
        ResultSet result = ps.executeQuery();
        while (result.next()) {
          Paciente paciente = new Paciente();
          paciente.mapear(result);
          pacientes.add(paciente);
        }
      }
    }
    return pacientes;
  }

  public static void main(String[] args) {
    // fluxo de teste que parte-se do pressuposto que o paciente não exista no banco
    Paciente novoPaciente = new Paciente();
    novoPaciente.setNome("Vitor3");
    novoPaciente.setDataDeNascimento("22/06/1999");
    novoPaciente.setEmail("vv3@gmail.com");
    novoPaciente.setEndereco("Rua Do vitor2");
    novoPaciente.setCelular("1122111");
    novoPaciente.setTelefone("2221122");
    novoPaciente.setCPF("137.145.155-15");
    try {
      novoPaciente.criar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }

    novoPaciente.setNome("Jefferson");
    try {
      novoPaciente.atualizar();
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
    }

    try {
      Optional<Paciente> optPaciente = buscar("137.145.155-15");
      if (optPaciente.isEmpty()) {
        System.out.println("paciente não encontrado");
      } else {
        Paciente paciente = optPaciente.get();
        System.out.println(paciente.id);
        System.out.println(paciente.nome);
        System.out.println(paciente.email);
        System.out.println(paciente.endereco);
        System.out.println(paciente.cpf);
        System.out.println(paciente.dataDeNascimento);
        System.out.println(paciente.celular);
        System.out.println(paciente.telefone);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    String cpfProcurado = "134-879-266-36";
    try {
      for (Paciente paciente : buscarTodos()) {
        if (paciente.cpf.equals(cpfProcurado)) {
          System.out.println("achei o paciente");
        }
//        System.out.println(paciente.id);
//        System.out.println(paciente.nome);
//        System.out.println(paciente.email);
//        System.out.println(paciente.endereco);
//        System.out.println(paciente.cpf);
//        System.out.println(paciente.dataDeNascimento);
//        System.out.println(paciente.celular);
//        System.out.println(paciente.telefone);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

//    try {
//      novoPaciente.remover();
//    } catch (Exception exception) {
//      System.out.println(exception.getMessage());
//    }
  }
}
