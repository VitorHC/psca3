package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class BancoDeDados {
    private static String servidor="jdbc:mysql://127.0.0.1:3306/clinica";
    private static String usuario="root";
    private static String senha="";
    private static String driver="com.mysql.jdbc.Driver";

    public static void main(String[] args){
        testarConexao();
    }

    public static Connection pegarConexao() throws SQLException {
        try {
            Class.forName(driver).newInstance();
        } catch (Exception except) {
            throw new SQLException("Impossível localizar driver de conexão ao banco de dados");
        }
        return DriverManager.getConnection(servidor, usuario, senha);
    }

    public static void testarConexao() {
        try {
            pegarConexao();
            System.out.println("Conexão ao banco de dados aberta com sucesso");
        } catch (Exception except) {
            System.out.println("Erro ao conectar ao banco de dados");
            System.out.println(except.getMessage());
        }
    }
}
