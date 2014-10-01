package Util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Transaccao implements Serializable {
	/**
	 * @uml.property  name="idIdeia"
	 */
	private int idIdeia;
	/**
	 * @uml.property  name="idUser"
	 */
	private int idUser;
	/**
	 * @uml.property  name="numShares"
	 */
	private int numShares;
	/**
	 * @uml.property  name="preco"
	 */
	private double preco;
	/**
	 * @uml.property  name="tipo"
	 */
	private String tipo; // compra, venda
	/**
	 * @uml.property  name="timestamp"
	 */
	private Timestamp timestamp;
	/**
	 * @uml.property  name="pago"
	 */
	private double pago;

	public Transaccao(int idIdeia, int idUser, int numShares, double preco, double pago, String tipo, Timestamp timestamp) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.numShares = numShares;
		this.preco = preco;
		this.setPago(pago);
		this.timestamp = timestamp;
		this.tipo = tipo;
	}

	public Transaccao(int idIdeia, double preco, Timestamp timestamp) {
		this.idIdeia = idIdeia;
		this.preco = preco;
		this.timestamp = timestamp;

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
	 * @uml.property  name="numShares"
	 */
	public int getNumShares() {
		return numShares;
	}

	/**
	 * @param numShares
	 * @uml.property  name="numShares"
	 */
	public void setNumShares(int numShares) {
		this.numShares = numShares;
	}

	/**
	 * @return
	 * @uml.property  name="preco"
	 */
	public double getPreco() {
		return preco;
	}

	/**
	 * @param preco
	 * @uml.property  name="preco"
	 */
	public void setPreco(double preco) {
		this.preco = preco;
	}

	/**
	 * @return
	 * @uml.property  name="tipo"
	 */
	public String getTipo() {
		return tipo;
	}

	/**
	 * @param tipo
	 * @uml.property  name="tipo"
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
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

	/**
	 * @return
	 * @uml.property  name="pago"
	 */
	public double getPago() {
		return pago;
	}

	/**
	 * @param pago
	 * @uml.property  name="pago"
	 */
	public void setPago(double pago) {
		this.pago = pago;
	}

}
