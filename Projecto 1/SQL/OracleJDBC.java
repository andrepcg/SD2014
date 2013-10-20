package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Util.Topico;
import Util.Transaccao;

public class OracleJDBC {

	Connection connection = null;

	public OracleJDBC() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e) {

			System.out.println("Erro JDBC Driver");
			e.printStackTrace();
			return;

		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}

		try {
			String url = "jdbc:mysql://andrepcg.myftp.org:3306/sd?user=sd&password=123456";
			connection = DriverManager.getConnection(url);

		} catch (SQLException e) {
			System.out.println("Falhou a conexão");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("Conexão feita com sucesso");

		} else {
			System.out.println("Falhou a conexão!");
		}
	}

	public boolean registo(String user, String pass) {

		try {

			String sql = "INSERT into utilizadores(username,password) Values(?,?)";

			// creating PreparedStatement object to execute query
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setString(1, user);
			stm.setString(2, MD5(pass));

			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public int login(String user, String pass) {

		try {

			ResultSet rs;
			PreparedStatement stm = null;
			String sql = "Select id,username,password from utilizadores where username=? and password=?";
			stm = connection.prepareStatement(sql);
			stm.setString(1, user);
			stm.setString(2, MD5(pass));

			rs = stm.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getInt(1));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());

		}
		return -1;
	}

	public boolean criarTopico(String nome) {

		try {
			String sql = "INSERT into topicos(nome) values(?)";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setString(1, nome);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}

	}

	public ArrayList<Transaccao> transaccoes(int idUser, int limit) {
		if (limit == 0)
			limit = 10;
		PreparedStatement stm = null;
		ResultSet rs;
		ArrayList<Transaccao> ts = new ArrayList<Transaccao>();
		try {
			String sql = "Select * from historicotransaccoes WHERE idUser = ? ORDER BY date DESC LIMIT ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setInt(2, limit);
			rs = stm.executeQuery();
			while (rs.next()) {
				ts.add(new Transaccao(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDouble(4), rs.getDouble(5), rs.getString(6), rs.getTimestamp(7)));
			}
		} catch (SQLException e) {
			// TODO: handle exception
		}
		return ts;
	}

	public ArrayList<Topico> mostraTopicos() {
		ArrayList<Topico> topicos = new ArrayList<Topico>();
		try {
			String sql = "Select * from topicos";
			PreparedStatement stm = connection.prepareStatement(sql);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				topicos.add(new Topico(rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {

		}
		return topicos;

	}

	private String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
}
