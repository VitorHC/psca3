package Model;

public class EntidadeInvalida extends ExcecaoDeEntidade {

  EntidadeInvalida(String mensagem) {
    super(mensagem);
  }

  EntidadeInvalida(String mensagem, String nomeDaEntidade) {
    super(mensagem, nomeDaEntidade);
  }
}
