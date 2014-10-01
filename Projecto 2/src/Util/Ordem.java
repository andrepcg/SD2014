package Util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ordem implements Comparable, Serializable {

	/**
	 * @uml.property  name="idideia"
	 */
	private int idideia;
	/**
	 * @uml.property  name="idUser"
	 */
	private int idUser;
	/**
	 * @uml.property  name="num_shares"
	 */
	private int num_shares;
	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="preco_por_share"
	 */
	private double preco_por_share;
	/**
	 * @uml.property  name="timestamp"
	 */
	private Timestamp timestamp;
	/**
	 * @uml.property  name="tipo"
	 */
	private int tipo;

	public Ordem(int idIdeia, int num_shares, double preco_por_share) {
		this.idideia = idIdeia;
		this.num_shares = num_shares;
		this.preco_por_share = preco_por_share;
	}

	public Ordem(int id, int idideia, int idUser, int num_shares, double preco_por_share, int tipo, Timestamp timestamp) {
		this(id, idideia, idUser, num_shares, preco_por_share, tipo);
		this.timestamp = timestamp;
	}

	public Ordem(int id, int idideia, int idUser, int num_shares, double preco_por_share, int tipo) {
		this.id = id;
		this.idideia = idideia;
		this.idUser = idUser;
		this.num_shares = num_shares;
		this.preco_por_share = preco_por_share;
		this.tipo = tipo;
	}

	/**
	 * @return
	 * @uml.property  name="idideia"
	 */
	public int getIdideia() {
		return idideia;
	}

	/**
	 * @param idideia
	 * @uml.property  name="idideia"
	 */
	public void setIdideia(int idideia) {
		this.idideia = idideia;
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
	 * @uml.property  name="timestamp"
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 * @uml.property  name="timestamp"
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String toString() {
		return idideia + ";" + idUser + ";" + num_shares + ";" + preco_por_share + ";" + timestamp;
	}

	/**
	 * @return
	 * @uml.property  name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 * @uml.property  name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int compareTo(Object ordem) {
		return this.timestamp.compareTo(((Ordem) ordem).getTimestamp());
	}

	/**
	 * @return
	 * @uml.property  name="tipo"
	 */
	public int getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 * @uml.property  name="tipo"
	 */
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

}
