package Util;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ideia implements Serializable {

	private int idIdeia;
	private int idUser;
	private String username;
	private String texto;
	private Timestamp data;
	private int idIdeiaPrincipal;
	private int idIdeiaSecundaria;
	private int nivel;
	private String sentimento;

	// ArrayList<Ideia> respostas;

	public Ideia(int idIdeia, int idUser, String username, String texto, Timestamp data, int nivel) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);
		this.nivel = nivel;

		// respostas = new ArrayList<>();

	}

	public Ideia(int idIdeia, int idUser, String username, String texto, Timestamp data, String sentimento) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);
		this.nivel = nivel;
		this.sentimento = sentimento;
		// respostas = new ArrayList<>();

	}

	public Ideia(int idIdeia, int idUser, String texto, Timestamp data) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);

		// respostas = new ArrayList<>();

	}

	public Ideia(int idIdeia, int idUser, String texto, Timestamp data, int idIdeiaPrincipal, int idIdeiaSecundaria, String username, String sentimento) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.idIdeiaPrincipal = idIdeiaPrincipal;
		this.idIdeiaSecundaria = idIdeiaSecundaria;
		this.sentimento = sentimento;

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

	public String toString() {
		return idIdeia + " " + texto;
	}

	public int getIdIdeiaSecundaria() {
		return idIdeiaSecundaria;
	}

	public void setIdIdeiaSecundaria(int idIdeiaSecundaria) {
		this.idIdeiaSecundaria = idIdeiaSecundaria;
	}

	public int getIdIdeiaPrincipal() {
		return idIdeiaPrincipal;
	}

	public void setIdIdeiaPrincipal(int idIdeiaPrincipal) {
		this.idIdeiaPrincipal = idIdeiaPrincipal;
	}

	public String getSentimento() {
		return sentimento;
	}

	public void setSentimento(String sentimento) {
		this.sentimento = sentimento;
	}

}