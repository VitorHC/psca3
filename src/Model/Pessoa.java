package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pessoa extends Modelo {
  protected Long id = 0L;
  protected String nome = "";
  protected String endereco = "";
  protected String email = "";
  protected String dataDeNascimento = "";
  protected String telefone = "";
  protected String celular = "";

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public Boolean setNome(String nome) {
    if (nome.length() < 3) {
      return false;
    }
    this.nome = nome;
    return true;
  }

  public String getEndereco() {
    return endereco;
  }

  public Boolean setEndereco(String endereco) {
    if (endereco.length() < 3) {
      return false;
    }
    this.endereco = endereco;
    return true;
  }

  public String getEmail() {
    return email;
  }

  public static final Pattern EMAIL_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  public Boolean setEmail(String email) {
    Matcher matcher = EMAIL_REGEX.matcher(email);
    if (!matcher.matches()) {
      return false;
    }
    this.email = email;
    return true;
  }

  public String getDataDeNascimento() {
    String[] fragments = dataDeNascimento.split("-");
    if (fragments.length == 0) {
      return "";
    }
    return fragments[2] + "/" + fragments[1] + "/" + fragments[0];
  }

  public Boolean setDataDeNascimento(String dataDeNascimento) {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT);
    try {
      LocalDate date = LocalDate.parse(dataDeNascimento, dateTimeFormatter);
    } catch (DateTimeParseException e) {
      return false;
    }
    String[] fragments = dataDeNascimento.split("/");
    if (fragments.length != 3) {
      return false;
    }
    this.dataDeNascimento = fragments[2] + "-" + fragments[1] + "-" + fragments[0];
    return true;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getCelular() {
    return celular;
  }

  public void setCelular(String celular) {
    this.celular = celular;
  }

  @Override public void validar() throws EntidadeInvalida {
    if (nome.isEmpty()) {
      throw new EntidadeInvalida("Nome inválido.");
    }
    if (endereco.isEmpty()) {
      throw new EntidadeInvalida("Endereço inválido.");
    }
    if (email.isEmpty()) {
      throw new EntidadeInvalida("E-mail inválido.");
    }
    if (dataDeNascimento.isEmpty()) {
      throw new EntidadeInvalida("Data de nascimento inválida.");
    }
  }

  protected static Boolean mapearPessoa(Pessoa pessoa, ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      pessoa.id = resultSet.getLong("id");
      pessoa.nome = resultSet.getString("nome");
      pessoa.endereco = resultSet.getString("endereco");
      pessoa.email = resultSet.getString("email");
      pessoa.dataDeNascimento = resultSet.getString("dataDeNascimento");
      pessoa.celular = resultSet.getString("celular");
      pessoa.telefone = resultSet.getString("telefone");
      return true;
    }
    return false;
  }

  protected static void criarPessoa(Pessoa pessoa, Connection conexao) throws SQLException {
    String sql = "INSERT INTO pessoas(nome, endereco, email, dataDeNascimento, telefone, celular) VALUES(?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, pessoa.nome);
      ps.setString(2, pessoa.endereco);
      ps.setString(3, pessoa.email);
      ps.setString(4, pessoa.dataDeNascimento);
      ps.setString(5, pessoa.telefone);
      ps.setString(6, pessoa.celular);
      ps.execute();
      ResultSet rst = ps.getGeneratedKeys();
      if (!rst.next()) {
        throw new SQLException("Não foi encontrado id retornado ao criar pessoa");
      }
      pessoa.id = rst.getLong(1);
    }
  }

  protected static void atualizarPessoa(Pessoa pessoa, Connection conexao) throws SQLException {
    String sql = "UPDATE pessoas SET nome = ?, endereco = ?, email = ?, dataDeNascimento = ?, telefone = ?, celular = ? WHERE id = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, pessoa.nome);
      ps.setString(2, pessoa.endereco);
      ps.setString(3, pessoa.email);
      ps.setString(4, pessoa.dataDeNascimento);
      ps.setString(5, pessoa.telefone);
      ps.setString(6, pessoa.celular);
      ps.setLong(7, pessoa.id);
      ps.execute();
    }
  }
}
