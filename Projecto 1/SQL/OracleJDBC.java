package SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import Util.Ideia;
import Util.Mercado;
import Util.Ordem;
import Util.OrdemCompra;
import Util.OrdemVenda;
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
				usera = new User(rs.getInt(1), rs.getString(2), rs.getDouble(4), rs.getTimestamp(5));
			}

			sql = "SELECT historicotransaccoes.id,historicotransaccoes.idIdeia, historicotransaccoes.idUser, historicotransaccoes.num_shares, historicotransaccoes.preco_por_share, historicotransaccoes.pago, historicotransaccoes.tipo, historicotransaccoes.timestamp FROM historicotransaccoes,utilizadores WHERE utilizadores.id = ? AND utilizadores.ultimo_login < historicotransaccoes.timestamp";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, usera.getId());
			rs = stm.executeQuery();

			ArrayList<Transaccao> historico = new ArrayList<Transaccao>();
			boolean v = false;
			while (rs.next()) {
				historico.add(new Transaccao(rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getTimestamp(8)));
				v = true;
			}

			if (v)
				usera.setHistorico(historico);
			else
				usera.setHistorico(null);

			sql = "UPDATE utilizadores SET ultimo_login = CURRENT_TIMESTAMP WHERE id = ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, usera.getId());
			stm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();

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
			limit = 30;
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
				sql = "Select * from ideia,ideiastopicos,utilizadores where ideia.id=ideiastopicos.idIdeia and ideiastopicos.idTopicos=? and ideia.idUser = utilizadores.id and ideia.principal = 1";
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
				ideias.add(new Ideia(rs.getInt(1), rs.getInt(2), rs.getString(9), rs.getString(3), rs.getTimestamp(4), 0));
			}

		} catch (SQLException e) {
		}
		return ideias;
	}

	public ArrayList<Ideia> seleccionarIdeia(int idMae) {
		ArrayList<Ideia> ideias = new ArrayList<Ideia>();
		try {
			String sql1 = "Select * from ideia, utilizadores where ideia.id=? AND ideia.idUser = utilizadores.id";
			PreparedStatement stm = connection.prepareStatement(sql1);
			stm.setInt(1, idMae);
			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ideias.add((new Ideia(rs.getInt(1), rs.getInt(2), rs.getString(7), rs.getString(3), rs.getTimestamp(4), 0)));
			}
			String sql = "Select * from ideia,relacoesideias,utilizadores where ideia.id=idIdeiaSecundaria AND ideia.idUser = utilizadores.id and relacoesideias.idMae=?";
			stm = connection.prepareStatement(sql);

			// (int idIdeia, int idUser, String username, String texto,
			// Timestamp data, String sentimento
			stm.setInt(1, idMae);
			rs = stm.executeQuery();
			while (rs.next()) {
				ideias.add(new Ideia(rs.getInt(1), rs.getInt(2), rs.getString(13), rs.getString(3), rs.getTimestamp(4), rs.getString(10)));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ideias;
	}

	public ArrayList<Ideia> ideiaRespostas(int idIdeia) {
		ArrayList<Ideia> ideias = new ArrayList<Ideia>();
		try {

			String sql = "SELECT idIdeiaSecundaria FROM relacoesideias WHERE idMae = ?";
			PreparedStatement stm = connection.prepareStatement(sql);

			stm.setInt(1, idIdeia);
			ResultSet rs = stm.executeQuery();

			ArrayList<Integer> respostas = new ArrayList<>();
			while (rs.next())
				respostas.add(rs.getInt(1));

			sql = "SELECT * FROM ideia WHERE id = ?";

			int j = 2;
			for (Integer i : respostas) {
				sql += " OR id = ?";
			}

			sql += " ORDER BY id";

			stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);
			for (Integer i : respostas) {
				stm.setInt(j++, i);
			}

			rs = stm.executeQuery();
			while (rs.next()) {
				ideias.add(new Ideia(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getTimestamp(4)));
			}

		} catch (SQLException e) {
			e.printStackTrace();
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
				shares.add(new Share(rs.getInt(1), rs.getInt(5), rs.getInt(2), rs.getString(4), rs.getInt(3), 0));
			}
		} catch (Exception e) {

		}

		return shares;
	}

	public int criarIdeia(int idUser, int idTopico, String texto, double preco, String data, int idMae, int idPrincipal, String sentimento) {

		int idIdeia = -1;
		try {

			connection.setAutoCommit(false);
			String sql = "Insert into ideia(idUser,texto,timestamp,principal) values (?,?,?,?)";

			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setString(2, texto);
			stm.setTimestamp(3, Timestamp.valueOf(data));
			stm.setInt(4, idPrincipal > 0 ? 0 : 1);
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

			String sql5 = "Insert into ordensvenda(idIdeia,idUser,num_shares,preco_por_share,timestamp) values(?,?,?,?,?)";
			stm = connection.prepareStatement(sql5);
			stm.setInt(1, idIdeia);

			stm.setInt(2, idUser);
			stm.setInt(3, 1000);
			stm.setDouble(4, preco);
			stm.setTimestamp(5, Timestamp.valueOf(data));
			stm.executeUpdate();

			if (idMae > 0) {
				String sql6 = "Insert into relacoesideias (idMae,idIdeiaPrincipal,idIdeiaSecundaria,sentimento) values(?,?,?,?)";
				stm = connection.prepareStatement(sql6);
				stm.setInt(1, idMae);
				stm.setInt(2, idPrincipal);
				stm.setInt(3, idIdeia);
				stm.setString(4, sentimento);
				stm.executeUpdate();
			}
			connection.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				connection.setAutoCommit(true);
				return idIdeia;
			} catch (SQLException e) {

				e.printStackTrace();
			}
		}

		return -1;

	}

	// void eliminaIdeias(int idIdeia, int idTopico, int idUser) {
	// try {
	// System.out.println(idIdeia);
	//
	// String sql = "Delete from ordensvenda where idIdeia=? and idUser=?";
	// PreparedStatement stm = connection.prepareStatement(sql);
	// stm = connection.prepareStatement(sql);
	//
	// stm.setInt(1, idIdeia);
	// stm.setInt(2, idUser);
	// stm.executeUpdate();
	//
	// String sql1 = "Delete from shares where idIdeia=? and idUser=?";
	// stm = connection.prepareStatement(sql1);
	//
	// stm.setInt(1, idIdeia);
	// stm.setInt(2, idUser);
	// stm.executeUpdate();
	//
	// String sql2 = "Delete from ideiastopicos where idIdeia=?";
	// stm = connection.prepareStatement(sql2);
	//
	// stm.setInt(1, idIdeia);
	//
	// stm.executeUpdate();
	//
	// String sql3 = "Delete from ideia where id=? and idUser=?";
	// stm = connection.prepareStatement(sql3);
	//
	// stm.setInt(1, idIdeia);
	// stm.setInt(2, idUser);
	// stm.executeUpdate();
	//
	// } catch (SQLException e) {
	//
	// }
	//
	// }

	public Mercado ordensUser(int idUser, int idIdeia) {
		// SELECT * FROM `ordenscompra` WHERE idIdeia = 69 and idUser = 15

		Mercado mercado = new Mercado();
		try {
			String sql = null;
			if (idIdeia > 0)
				sql = "SELECT * FROM ordenscompra WHERE idIdeia = ? and idUser = ?";
			else
				sql = "SELECT * FROM ordenscompra WHERE idUser = ?";

			PreparedStatement stm = connection.prepareStatement(sql);
			stm = connection.prepareStatement(sql);
			if (idIdeia > 0) {
				stm.setInt(1, idIdeia);
				stm.setInt(2, idUser);
			} else
				stm.setInt(1, idUser);

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				mercado.addOrdemCompra(new OrdemCompra(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

			sql = null;
			if (idIdeia > 0)
				sql = "SELECT * FROM ordensvenda WHERE idIdeia = ? and idUser = ?";
			else
				sql = "SELECT * FROM ordensvenda WHERE idUser = ?";

			stm = connection.prepareStatement(sql);
			stm = connection.prepareStatement(sql);
			if (idIdeia > 0) {
				stm.setInt(1, idIdeia);
				stm.setInt(2, idUser);
			} else
				stm.setInt(1, idUser);

			rs = stm.executeQuery();
			while (rs.next()) {
				mercado.addOrdemVenda(new OrdemVenda(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

		} catch (Exception e) {

		}

		return mercado;
	}

	public void comentarIdeia(int idUser, int idTopico, String texto, double preco, String data, int idIdeia) {
		try {

			String sql = "Select * FROM relacoesideias where idIdeiaSecundaria = ?";
			PreparedStatement stm = connection.prepareStatement(sql);

			stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);

			ResultSet rs = stm.executeQuery();
			int nivel = 1;
			int idMae = idIdeia;
			int idPrincipal = idIdeia;
			int idSecundario;
			if (rs.next()) {
				idMae = rs.getInt(6) + 1;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public Mercado mercadoShares(int idIdeia) {
		Mercado mercado = new Mercado();
		try {
			String sql = "SELECT * FROM ordenscompra WHERE idIdeia = ? ORDER BY preco_por_share DESC";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				mercado.addOrdemCompra(new OrdemCompra(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

			sql = "SELECT * FROM ordensvenda WHERE idIdeia = ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);

			rs = stm.executeQuery();
			while (rs.next()) {
				mercado.addOrdemVenda(new OrdemVenda(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

			return mercado;

		} catch (SQLException e) {

		}

		return null;
	}

	public boolean inserirFicheiro(int idIdeia, String path) {

		try {
			String sql = "INSERT into ficheiros (idIdeia,ficheiro) values(?,?)";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);
			stm.setString(2, path);
			stm.executeUpdate();
			return true;
		} catch (SQLException e) {

		}

		return false;
	}

	// public int editar

	public int criarOrdem(int tipo, int idIdeia, int idUser, int numShares, double preco_por_share, double precoTotal, String timestamp) {

		try {
			String sql = "INSERT into ordens" + (tipo == 0 ? "compra" : "venda") + " (idIdeia,idUser,num_shares,preco_por_share,timestamp) values(?,?,?,?,?)";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);
			stm.setInt(2, idUser);
			stm.setInt(3, numShares);
			stm.setDouble(4, preco_por_share);
			stm.setTimestamp(5, Timestamp.valueOf(timestamp));
			stm.executeUpdate();

			matchOrdens(idIdeia);

			return 1;
		} catch (SQLException e) {

		}

		return -1;
	}

	private int matchOrdens(int idIdeia) {

		Mercado ordensIdeia = null;
		try {
			ordensIdeia = new Mercado();

			String sql = "SELECT ordenscompra.id, ordenscompra.idIdeia, ordenscompra.idUser, ordenscompra.num_shares, ordenscompra.preco_por_share, ordenscompra.timestamp FROM ordenscompra, ordensvenda WHERE ordenscompra.idIdeia = ? AND ordenscompra.idIdeia = ordensvenda.idIdeia AND ordenscompra.idUser != ordensvenda.idUser";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);

			ResultSet rs = stm.executeQuery();
			while (rs.next()) {
				ordensIdeia.addOrdemVenda(new OrdemVenda(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

			sql = "SELECT ordensvenda.id, ordensvenda.idIdeia, ordensvenda.idUser, ordensvenda.num_shares, ordensvenda.preco_por_share, ordensvenda.timestamp FROM ordenscompra, ordensvenda WHERE ordensvenda.idIdeia = ? AND ordenscompra.idIdeia = ordensvenda.idIdeia AND ordenscompra.idUser != ordensvenda.idUser";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);
			rs = stm.executeQuery();
			while (rs.next()) {
				ordensIdeia.addOrdemCompra(new OrdemCompra(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getDouble(5), rs.getTimestamp(6)));
			}

			Ordem[] match = ordensIdeia.ordensPrecoMatch();

			int diferencaShares = (match[0].getNum_shares() <= match[1].getNum_shares()) ? match[0].getNum_shares() : match[0].getNum_shares() - match[1].getNum_shares();
			double preco = diferencaShares * match[0].getPreco_por_share();

			String update1 = "UPDATE ordenscompra SET num_shares = num_shares - ? WHERE id = ?";
			String update2 = "UPDATE ordensvenda SET num_shares = num_shares - ? WHERE id = ?";
			String update3 = "UPDATE utilizadores SET deicoins = deicoins - ? WHERE id = ?";
			String update4 = "UPDATE utilizadores SET deicoins = deicoins + ? WHERE id = ?";
			String update5 = null;
			String update6 = null;
			String historico_comprador = "INSERT INTO historicotransaccoes (idIdeia,idUser,num_shares,preco_por_share,pago,tipo) VALUES (?,?,?,?,?,?)";
			String historico_vendedor = "INSERT INTO historicotransaccoes (idIdeia,idUser,num_shares,preco_por_share,pago,tipo) VALUES (?,?,?,?,?,?)";

			boolean userCompraTemShares = false;
			sql = "SELECT * FROM shares WHERE idUser = ? AND idIdeia = ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, match[0].getIdUser());
			stm.setInt(2, idIdeia);
			rs = stm.executeQuery();
			if (rs.next()) {
				userCompraTemShares = true;
				update5 = "UPDATE shares SET num_shares = num_shares + ? WHERE idIdeia = ? AND idUser = ?";
			} else
				update5 = "INSERT INTO shares (num_shares,idIdeia,idUser) VALUES (?,?,?)";

			boolean userVendeShares = false;
			sql = "SELECT * FROM shares WHERE idUser = ? AND idIdeia = ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, match[1].getIdUser());
			stm.setInt(2, idIdeia);
			rs = stm.executeQuery();
			if (rs.next()) {
				int numsharesuser = rs.getInt(4);
				if (numsharesuser > diferencaShares) {
					update6 = "UPDATE shares SET num_shares = num_shares - ? WHERE idIdeia = ? AND idUser = ?";
					userVendeShares = true;
				} else
					update6 = "DELETE FROM shares WHERE idIdeia = ? AND idUser = ?";
			}

			connection.setAutoCommit(false);
			PreparedStatement stm1 = connection.prepareStatement(update1);
			PreparedStatement stm2 = connection.prepareStatement(update2);
			PreparedStatement stm3 = connection.prepareStatement(update3);
			PreparedStatement stm4 = connection.prepareStatement(update4);
			PreparedStatement stm5 = connection.prepareStatement(update5);
			PreparedStatement stm6 = connection.prepareStatement(update6);
			PreparedStatement stm7 = connection.prepareStatement(historico_comprador);
			PreparedStatement stm8 = connection.prepareStatement(historico_vendedor);

			stm1.setInt(1, diferencaShares);
			stm1.setInt(2, match[0].getId());
			stm1.executeUpdate();

			stm2.setInt(1, diferencaShares);
			stm2.setInt(2, match[1].getId());
			stm2.executeUpdate();

			stm3.setDouble(1, preco);
			stm3.setInt(2, match[0].getIdUser());
			stm3.executeUpdate();

			stm4.setDouble(1, preco);
			stm4.setInt(2, match[1].getIdUser());
			stm4.executeUpdate();

			stm5.setInt(1, diferencaShares);
			stm5.setInt(2, idIdeia);
			stm5.setInt(3, match[0].getIdUser());
			stm5.executeUpdate();

			if (userVendeShares) {
				stm6.setInt(1, diferencaShares);
				stm6.setInt(2, idIdeia);
				stm6.setInt(3, match[1].getIdUser());

			} else {
				stm6.setInt(1, idIdeia);
				stm6.setInt(2, match[1].getIdUser());

			}
			stm6.executeUpdate();

			stm7.setInt(1, idIdeia);
			stm7.setInt(2, match[0].getIdUser());
			stm7.setInt(3, diferencaShares);
			stm7.setDouble(4, match[0].getPreco_por_share());
			stm7.setDouble(5, preco);
			stm7.setString(6, "compra");
			stm7.executeUpdate();

			stm8.setInt(1, idIdeia);
			stm8.setInt(2, match[1].getIdUser());
			stm8.setInt(3, diferencaShares);
			stm8.setDouble(4, match[1].getPreco_por_share());
			stm8.setDouble(5, preco);
			stm8.setString(6, "venda");
			stm8.executeUpdate();

			connection.commit();

			sql = "DELETE FROM ordenscompra WHERE num_shares = 0";
			stm1 = connection.prepareStatement(sql);
			stm1.executeUpdate();

			sql = "DELETE FROM ordensvenda WHERE num_shares = 0";
			stm2 = connection.prepareStatement(sql);
			stm2.executeUpdate();
			connection.commit();

		} catch (SQLException e) {
			if (connection != null) {
				try {
					System.err.print("Transaction is being rolled back");
					connection.rollback();
				} catch (SQLException excep) {

				}
			}
		} finally {

			try {
				connection.setAutoCommit(true);
				return 1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -1;
	}

	public boolean eliminaIdeias(int idIdeia, int idUser) {
		try {
			String sql = "Select * from relacoesideias where idIdeiaPrincipal=?";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idIdeia);
			ResultSet rs = stm.executeQuery();
			if (!rs.next()) {

				int idTopico = 0;
				sql = "Select * from ideiastopicos where idIdeia = ?";
				stm = connection.prepareStatement(sql);
				stm.setInt(1, idIdeia);
				rs = stm.executeQuery();
				if (rs.next()) {
					idTopico = rs.getInt(2);
				}

				sql = "Select * from ideia,ideiastopicos,shares where ideiastopicos.idTopicos=? and ideia.idUser=? and num_shares=1000 and shares.idUser=ideia.idUser and shares.idIdeia=ideia.id and ideiastopicos.idIdeia=ideia.id ";
				stm = connection.prepareStatement(sql);
				stm.setInt(1, idTopico);
				stm.setInt(2, idUser);
				rs = stm.executeQuery();
				if (rs.next()) {

					try {

						sql = "Delete from relacoesideias where idIdeiaSecundaria=?";
						stm = connection.prepareStatement(sql);
						stm.setInt(1, idIdeia);

						stm.executeUpdate();
						sql = "Delete from ordensvenda where idIdeia=? and idUser=?";
						stm = connection.prepareStatement(sql);

						stm.setInt(1, idIdeia);
						stm.setInt(2, idUser);
						stm.executeUpdate();
						sql = "Delete from shares where idIdeia=? and idUser=?";
						stm = connection.prepareStatement(sql);
						stm.setInt(1, idIdeia);
						stm.setInt(2, idUser);
						stm.executeUpdate();
						sql = "Delete from ideiastopicos where idIdeia=?";
						stm = connection.prepareStatement(sql);
						stm.setInt(1, idIdeia);

						stm.executeUpdate();
						sql = "Delete from ideia where id=? and idUser=?";
						stm = connection.prepareStatement(sql);
						stm.setInt(1, idIdeia);
						stm.setInt(2, idUser);
						stm.executeUpdate();

						return true;
					} catch (SQLException e) {
						e.printStackTrace();

					}
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public User getUser(int idUser) {
		User usera = null;

		try {

			ResultSet rs;
			PreparedStatement stm = null;
			String sql = "Select * from utilizadores where id= ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);

			rs = stm.executeQuery();

			while (rs.next()) {
				usera = new User(rs.getInt(1), rs.getString(2), rs.getDouble(4), rs.getTimestamp(5));
			}

		} catch (SQLException e) {

		}
		return usera;
	}

	public boolean removerOrdem(int idUser, int tipo, int idOrdem) {
		try {
			String sql = "Select idIdeia from " + (tipo == 0 ? "ordenscompra" : "ordensvenda") + " WHERE idUser = ? AND id = ?";
			PreparedStatement stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setInt(2, idOrdem);
			ResultSet rs;
			rs = stm.executeQuery();

			int idIdeia = 0;
			if (rs.next())
				idIdeia = rs.getInt(1);

			sql = "DELETE FROM " + (tipo == 0 ? "ordenscompra" : "ordensvenda") + " WHERE idUser = ? AND id = ?";
			stm = connection.prepareStatement(sql);
			stm.setInt(1, idUser);
			stm.setInt(2, idOrdem);
			stm.executeUpdate();

			matchOrdens(idIdeia);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}
}
