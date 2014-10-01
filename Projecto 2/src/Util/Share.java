package Util;

import java.io.Serializable;

public class Share implements Serializable {

	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="idIdeia"
	 */
	private int idIdeia;
	/**
	 * @uml.property  name="idUser"
	 */
	private int idUser;
	/**
	 * @uml.property  name="ideia"
	 */
	private String ideia;
	/**
	 * @uml.property  name="titulo"
	 */
	private String titulo;
	/**
	 * @uml.property  name="num_shares"
	 */
	private int num_shares;
	/**
	 * @uml.property  name="preco_por_share"
	 */
	private double preco_por_share;

	public Share(int id, int idIdeia, int idUser, String ideia, int num_shares, int preco_por_share) {
		this(id, idIdeia, idUser, num_shares);
		this.ideia = ideia;
		this.preco_por_share = preco_por_share;
	}

	// s.id, s.idIdeia, i.titulo, s.num_shares

	public Share(int id, int idIdeia, String titulo, int num_shares) {
		this.id = id;
		this.idIdeia = idIdeia;
		this.titulo = titulo;
		this.num_shares = num_shares;
	}

	public Share(int id, int idIdeia, int idUser, int num_shares) {
		this.id = id;
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.num_shares = num_shares;
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
	 * @uml.property  name="ideia"
	 */
	public String getIdeia() {
		return ideia;
	}

	/**
	 * @param ideia
	 * @uml.property  name="ideia"
	 */
	public void setIdeia(String ideia) {
		this.ideia = ideia;
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
	 * @uml.property  name="titulo"
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 * @uml.property  name="titulo"
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
