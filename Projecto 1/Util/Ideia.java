package Util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ideia implements Serializable {

	private int idIdeia;
	private int idUser;
	private String username;
	private String texto;
	private Timestamp data;

	public Ideia(int idIdeia, int idUser, String username, String texto, Timestamp data) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);

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

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public Timestamp getData() {
		return data;
	}

	public void setData(Timestamp data) {
		this.data = data;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}