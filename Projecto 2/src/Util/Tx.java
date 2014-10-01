package Util;

import java.io.Serializable;

public class Tx implements Serializable {

	/**
	 * @uml.property  name="idIdeia"
	 */
	private int idIdeia;
	/**
	 * @uml.property  name="idUser"
	 */
	private int idUser;
	/**
	 * @uml.property  name="preco_por_share"
	 */
	private double preco_por_share;
	/**
	 * @uml.property  name="num_shares"
	 */
	private int num_shares;
	/**
	 * @uml.property  name="compravenda"
	 */
	private String compravenda;

	/**
	 * @return
	 * @uml.property  name="idIdeia"
	 */
	public int getIdIdeia() {
		return idIdeia;
	}

	/**
	 * @param idIdeia
	 * @uml.property  name="idIdeia"
	 */
	public void setIdIdeia(int idIdeia) {
		this.idIdeia = idIdeia;
	}

	/**
	 * @return
	 * @uml.property  name="idUser"
	 */
	public int getIdUser() {
		return idUser;
	}

	/**
	 * @param idUser
	 * @uml.property  name="idUser"
	 */
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	/**
	 * @return
	 * @uml.property  name="preco_por_share"
	 */
	public double getPreco_por_share() {
		return preco_por_share;
	}

	/**
	 * @param preco_por_share
	 * @uml.property  name="preco_por_share"
	 */
	public void setPreco_por_share(double preco_por_share) {
		this.preco_por_share = preco_por_share;
	}

	/**
	 * @return
	 * @uml.property  name="num_shares"
	 */
	public int getNum_shares() {
		return num_shares;
	}

	/**
	 * @param num_shares
	 * @uml.property  name="num_shares"
	 */
	public void setNum_shares(int num_shares) {
		this.num_shares = num_shares;
	}

	public Tx(int idIdeia, int idUser, double preco_por_share, int num_shares, int compravenda) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.preco_por_share = preco_por_share;
		this.num_shares = num_shares;
		this.compravenda = (compravenda == 0) ? "compra" : "venda";
	}

	/**
	 * @return
	 * @uml.property  name="compravenda"
	 */
	public String getCompravenda() {
		return compravenda;
	}

	/**
	 * @param compravenda
	 * @uml.property  name="compravenda"
	 */
	public void setCompravenda(String compravenda) {
		this.compravenda = compravenda;
	}

}
