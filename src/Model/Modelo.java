package Model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Modelo {
  public void atualizar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    Connection conexao = BancoDeDados.pegarConexao();
    conexao.setAutoCommit(false);
    try {
      atualizar(conexao);
      conexao.commit();
    } catch (Exception e) {
      conexao.rollback();
      throw e;
    } finally {
      conexao.close();
    }
  }

  protected void atualizar(Connection conexao) throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
  }

  public void criar() throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
    Connection conexao = BancoDeDados.pegarConexao();
    conexao.setAutoCommit(false);
    try {
      criar(conexao);
      conexao.commit();
    } catch (Exception e) {
      conexao.rollback();
      throw e;
    } finally {
      conexao.close();
    }
  }

  protected void criar(Connection conexao) throws SQLException, EntidadeInvalida, ConflitoDeEntidade {
  }

  public void remover() throws SQLException, ConflitoDeEntidade {
    Connection conexao = BancoDeDados.pegarConexao();
    conexao.setAutoCommit(false);
    try {
      remover(conexao);
      conexao.commit();
    } catch (Exception e) {
      conexao.rollback();
      throw e;
    } finally {
      conexao.close();
    }
  }

  protected void remover(Connection conexao) throws SQLException, ConflitoDeEntidade {
  }

  public void validar() throws EntidadeInvalida {
  }

  protected void mapear(ResultSet resultSet) throws SQLException {
  }
}
