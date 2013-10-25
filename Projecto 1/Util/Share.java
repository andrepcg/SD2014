package Util;

import java.io.Serializable;

public class Share implements Serializable {

	private int id;
	private int idIdeia;
	private int idUser;
	private String ideia;
	private int num_shares;
	private double preco_por_share;

	public Share(int id, int idIdeia, int idUser, String ideia, int num_shares, int preco_por_share) {
		this.id = id;
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.ideia = ideia;
		this.num_shares = num_shares;
		this.preco_por_share = preco_por_share;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdIdeia() {
		return idIdeia;
	}

	public void setIdIdeia(int idIdeia) {
		this.idIdeia = idIdeia;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public String getIdeia() {
		return ideia;
	}

	public void setIdeia(String ideia) {
		this.ideia = ideia;
	}

	public int getNum_shares() {
		return num_shares;
	}

	public void setNum_shares(int num_shares) {
		this.num_shares = num_shares;
	}

}
