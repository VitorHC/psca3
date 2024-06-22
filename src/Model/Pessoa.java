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

  @Override
  public void validar() throws EntidadeInvalida {
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

  @Override
  protected void mapear(ResultSet resultSet) throws SQLException {
    id = resultSet.getLong("id");
    nome = resultSet.getString("nome");
    endereco = resultSet.getString("endereco");
    email = resultSet.getString("email");
    dataDeNascimento = resultSet.getString("dataDeNascimento");
    celular = resultSet.getString("celular");
    telefone = resultSet.getString("telefone");
  }

  @Override
  protected void criar(Connection conexao)
      throws SQLException, ConflitoDeEntidade, EntidadeInvalida {
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
      if (!rst.next()) {
        throw new SQLException("Não foi encontrado id retornado ao criar pessoa");
      }
      id = rst.getLong(1);
    }
  }

  @Override
  protected void atualizar(Connection conexao)
      throws SQLException, ConflitoDeEntidade, EntidadeInvalida {
    String sql = "UPDATE pessoas SET nome = ?, endereco = ?, email = ?, dataDeNascimento = ?, telefone = ?, celular = ? WHERE id = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, nome);
      ps.setString(2, endereco);
      ps.setString(3, email);
      ps.setString(4, dataDeNascimento);
      ps.setString(5, telefone);
      ps.setString(6, celular);
      ps.setLong(7, id);
      ps.execute();
    }
  }

  protected void remover(Connection conexao) throws SQLException, ConflitoDeEntidade {
    String sql = "DELETE FROM pessoas WHERE id = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setLong(1, id);
      ps.execute();
    }
  }

  protected Boolean possuoEmailUnico(Connection conexao) throws SQLException {
    boolean manterConexao = true;
    if (conexao == null) {
      conexao = BancoDeDados.pegarConexao();
      manterConexao = false;
    }
    boolean emailUnico = true;
    String sql = "SELECT * FROM pessoas WHERE email = ?";
    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
      ps.setString(1, email);
      ResultSet rst = ps.executeQuery();
      if (rst.next() && rst.getLong("id") != id) {
        emailUnico = false;
      }
    }
    if (!manterConexao) {
      conexao.close();
    }
    return emailUnico;
  }
}
