package Util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ordem implements Serializable {

	private int idideia, idUser, num_shares, id;
	private double preco_por_share;
	private Timestamp timestamp;

	public Ordem(int id, int idideia, int idUser, int num_shares, double preco_por_share, Timestamp timestamp) {
		this.id = id;
		this.idideia = idideia;
		this.idUser = idUser;
		this.num_shares = num_shares;
		this.preco_por_share = preco_por_share;
		this.timestamp = timestamp;
	}

	public int getIdideia() {
		return idideia;
	}

	public void setIdideia(int idideia) {
		this.idideia = idideia;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public int getNum_shares() {
		return num_shares;
	}

	public void setNum_shares(int num_shares) {
		this.num_shares = num_shares;
	}

	public double getPreco_por_share() {
		return preco_por_share;
	}

	public void setPreco_por_share(double preco_por_share) {
		this.preco_por_share = preco_por_share;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String toString() {
		return idideia + ";" + idUser + ";" + num_shares + ";" + preco_por_share + ";" + timestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
