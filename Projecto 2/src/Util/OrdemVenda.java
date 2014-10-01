package Util;

import java.sql.Timestamp;

public class OrdemVenda extends Ordem {

	public OrdemVenda(int idIdeia, int num_shares, double preco_por_share) {
		super(idIdeia, num_shares, preco_por_share);
		// TODO Auto-generated constructor stub
	}

	public OrdemVenda(int id, int idideia, int idUser, int num_shares, double preco_por_share, int tipo) {
		super(id, idideia, idUser, num_shares, preco_por_share, tipo);
		// TODO Auto-generated constructor stub
	}

	public OrdemVenda(int id, int idideia, int idUser, int num_shares, double preco_por_share, int tipo, Timestamp timestamp) {
		super(id, idideia, idUser, num_shares, preco_por_share, tipo, timestamp);

	}

}
