package Model;

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

  protected void setId(Long id) {
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

  public void setEndereco(String endereco) {
    this.endereco = endereco;
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
}
