package Model;

public class ExcecaoDeEntidade extends Exception {
  public String nomeDaEntidade = "";
  ExcecaoDeEntidade(String mensagem) {
    super(mensagem);
  }
  ExcecaoDeEntidade(String mensagem, String nomeDaEntidade) {
    super(mensagem);
    this.nomeDaEntidade = nomeDaEntidade;
  }
}
