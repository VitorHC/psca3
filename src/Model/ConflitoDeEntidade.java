package Model;

public class ConflitoDeEntidade extends ExcecaoDeEntidade {

  ConflitoDeEntidade(String mensagem) {
    super(mensagem);
  }

  ConflitoDeEntidade(String mensagem, String nomeDaEntidade) {
    super(mensagem, nomeDaEntidade);
  }
}
