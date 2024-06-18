package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Conexao {
    public Connection con = null;
    public Statement stmt = null;
    public ResultSet resultset = null;
    String servidor="jdbc:mysql://127.0.0.1:3306/clinica";
    String usuario="root";
    String senha="";
    String driver="com.mysql.jdbc.Driver";

    public static void main(String args[]){
        Conexao b = new Conexao();
        b.AbrirConexao();
        b.FecharConexao();
    }

    public Connection AbrirConexao(){
        try {
            Class.forName(driver).newInstance();
            con = (Connection) DriverManager.getConnection(servidor, usuario, senha);
            stmt = con.createStatement();
            System.out.println("Conexão aberta com sucesso");
        } catch (Exception e) {
            System.out.println("Erro ao acessar banco de dados");
            e.printStackTrace();
        }
        return con;
    }
    
    public void FecharConexao(){
        try{
            con.close();
            System.out.println("Conexão finalizada com sucesso");
        } catch(Exception e) {
            System.out.println("Erro ao encerrar conexão" + e.getMessage());
        }
    }
    
}
