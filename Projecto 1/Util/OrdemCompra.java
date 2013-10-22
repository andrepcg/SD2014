package Util;

import java.sql.Timestamp;

public class OrdemCompra extends Ordem {

	public OrdemCompra(int idideia, int idUser, int num_shares, double preco_por_share, Timestamp timestamp) {
		super(idideia, idUser, num_shares, preco_por_share, timestamp);
	}

}
