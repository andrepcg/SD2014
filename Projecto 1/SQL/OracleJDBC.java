package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import Util.Ideia;
import Util.Share;
import Util.Topico;
import Util.Transaccao;
import Util.User;

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
			// String url =
			// "jdbc:mysql://localhost:3306/sd?user=sd&password=123456";
			connection = DriverManager.getConnection(url);

		} catch (SQLException e) {
			System.out.println("Falhou a conexao");
			e.printStackTrace();
			return;
		}

		if (connection != null) {
			System.out.println("Conexao feita com sucesso");

		} else {
			System.out.println("Falhou a conexao!");
		}
	}

	public boolean registo(String user, String pass) {

		try {

			String sql = "INSERT into utilizadores(username,password) Values(?,?)";

			// creating PreparedStatement object to execute query
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setString(1, user);
			stm.setString(2, pass);

			stm.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public User login(String user, String pass) {

		User usera = null;

		try {

			ResultSet rs;
			PreparedStatement stm = null;
			String sql = "Select * from utilizadores where username=? and password=?";
			stm = connection.prepareStatement(sql);
			stm.setString(1, user);
			stm.setString(2, pass);

			rs = stm.executeQuery();

			while (rs.next()) {
				usera = new User(rs.getInt(1), rs.getString(2), rs.getDouble(4));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());

		}
		return usera;
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
			String sql = "Select * from historicotransaccoes WHERE idUser = ? ORDER BY timestamp DESC LIMIT ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setInt(2, limit);
			rs = stm.executeQuery();
			while (rs.next()) {
				ts.add(new Transaccao(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getDouble(6), rs.getString(7), rs.getTimestamp(8)));
			}

		} catch (SQLException e) {
			// TODO: handle exception
		}
		return ts;
	}

	public ArrayList<Topico> mostraTopicos() {
		ArrayList<Topico> topicos = new ArrayList<Topico>();
		try {
			String sql = "Select * from topicos ORDER BY id";
			PreparedStatement stm = connection.prepareStatement(sql);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				topicos.add(new Topico(rs.getInt(1), rs.getString(2)));
			}

		} catch (SQLException e) {

		}
		return topicos;

	}

	public ArrayList<Ideia> mostraIdeias(int idTopico, int idUser) {
		ArrayList<Ideia> ideias = new ArrayList<Ideia>();
		try {

			String sql = "";
			PreparedStatement stm = null;

			if (idTopico > 0 && idUser == 0) {
				sql = "Select * from ideia,ideiastopicos,utilizadores where ideia.id=ideiastopicos.idIdeia and ideiastopicos.idTopicos=? and ideia.idUser = utilizadores.id";
				stm = connection.prepareStatement(sql);
				stm.setInt(1, idTopico);

			} else if (idTopico == 0 && idUser > 0) {
				sql = "Select * from ideia,ideiastopicos,utilizadores where ideia.id=ideiastopicos.idIdeia and ideia.idUser = utilizadores.id and ideia.idUser = ?";
				stm = connection.prepareStatement(sql);
				stm.setInt(1, idUser);

			} else if (idTopico > 0 && idUser > 0) {
				sql = "Select * from ideia,ideiastopicos,utilizadores where ideia.id=ideiastopicos.idIdeia and ideiastopicos.idTopicos=? and ideia.idUser = utilizadores.id and ideia.idUser = ?";
				stm = connection.prepareStatement(sql);
				stm.setInt(1, idTopico);
				stm.setInt(2, idUser);
			}

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ideias.add(new Ideia(rs.getInt(1), rs.getInt(2), rs.getString(9), rs.getString(3), rs.getTimestamp(4)));
			}

		} catch (SQLException e) {
		}
		return ideias;
	}

	public ArrayList<Share> seleccionarShares(int idUser) {
		ArrayList<Share> shares = new ArrayList<Share>();
		try {
			String sql = "select shares.id, shares.idUser, shares.num_shares, ideia.texto, ideia.id  FROM shares LEFT JOIN ideia ON ideia.id = shares.idIdeia  where shares.idUser = ?";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				shares.add(new Share(rs.getInt(1), rs.getInt(5), rs.getInt(2), rs.getString(4), rs.getInt(3)));
			}
		} catch (Exception e) {

		}

		return shares;
	}

	public boolean criarIdeia(int idUser, int idTopico, String texto, double preco, String data) {
		try {
			int idIdeia = -1;
			String sql = "Insert into ideia(idUser,texto,timestamp) values (?,?,?)";

			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setString(2, texto);
			stm.setTimestamp(3, Timestamp.valueOf(data));
			stm.executeUpdate();

			String sql2 = "Select id FROM ideia where idUser=? and texto=? and timestamp=?";
			stm = connection.prepareStatement(sql2);
			stm.setInt(1, idUser);
			stm.setString(2, texto);
			stm.setTimestamp(3, Timestamp.valueOf(data));
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				idIdeia = rs.getInt(1);
			}
			String sql3 = "Insert into ideiastopicos(idTopicos,idIdeia) values(?,?)";
			stm = connection.prepareStatement(sql3);
			stm.setInt(1, idTopico);
			stm.setInt(2, idIdeia);
			stm.executeUpdate();

			String sql4 = "Insert into shares(idUser,idIdeia,num_shares) values(?,?,1000)";
			stm = connection.prepareStatement(sql4);
			stm.setInt(1, idUser);
			stm.setInt(2, idIdeia);
			stm.executeUpdate();

			String sql5 = "Insert into ordensvenda(idIdeia,idUser,num_shares,preco,timestamp) values(?,?,?,?,?)";
			stm = connection.prepareStatement(sql5);
			stm.setInt(1, idIdeia);

			stm.setInt(2, idUser);
			stm.setInt(3, 1000);
			stm.setDouble(4, preco);
			stm.setTimestamp(5, Timestamp.valueOf(data));

			stm.executeUpdate();

			return true;
		} catch (SQLException e) {
		}
		return false;

	}

}
