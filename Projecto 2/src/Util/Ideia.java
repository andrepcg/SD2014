package Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Ideia implements Serializable {

	/**
	 * @uml.property  name="idIdeia"
	 */
	private int idIdeia;
	/**
	 * @uml.property  name="idUser"
	 */
	private int idUser;
	/**
	 * @uml.property  name="username"
	 */
	private String username;
	/**
	 * @uml.property  name="texto"
	 */
	private String texto;
	/**
	 * @uml.property  name="data"
	 */
	private Timestamp data;
	/**
	 * @uml.property  name="idIdeiaPrincipal"
	 */
	private int idIdeiaPrincipal;
	/**
	 * @uml.property  name="idIdeiaSecundaria"
	 */
	private int idIdeiaSecundaria;
	/**
	 * @uml.property  name="nivel"
	 */
	private int nivel;
	/**
	 * @uml.property  name="sentimento"
	 */
	private String sentimento;
	/**
	 * @uml.property  name="titulo"
	 */
	private String titulo;
	/**
	 * @uml.property  name="facebookPost"
	 */
	private String facebookPost;

	/**
	 * @uml.property  name="respostas"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="Util.Ideia"
	 */
	ArrayList<Ideia> respostas = new ArrayList<Ideia>();

	public Ideia(int idIdeia, int idUser, String username, String texto, Timestamp data, int nivel) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);
		this.nivel = nivel;

	}

	public Ideia(int idIdeia, String texto, Timestamp data, String username) {
		this.idIdeia = idIdeia;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);

	}

	public Ideia(int idIdeia, String texto, Timestamp data, String username, String titulo) {
		this.idIdeia = idIdeia;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);
		this.titulo = titulo;
	}

	public Ideia(int idIdeia, int idUser, String username, String texto, Timestamp data, String sentimento, String titulo) {
		this.idIdeia = idIdeia;
		this.idUser = idUser;
		this.texto = texto;
		this.data = data;
		this.setUsername(username);
		this.titulo = titulo;
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

	public void addRespostas(Ideia i) {
		respostas.add(i);
	}

	public ArrayList<Ideia> getRespostas() {
		return respostas;
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
	 * @uml.property  name="texto"
	 */
	public String getTexto() {
		return texto;
	}

	/**
	 * @param texto
	 * @uml.property  name="texto"
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}

	/**
	 * @return
	 * @uml.property  name="data"
	 */
	public Timestamp getData() {
		return data;
	}

	/**
	 * @param data
	 * @uml.property  name="data"
	 */
	public void setData(Timestamp data) {
		this.data = data;
	}

	/**
	 * @return
	 * @uml.property  name="username"
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 * @uml.property  name="username"
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String toString() {
		return idIdeia + " " + texto;
	}

	/**
	 * @return
	 * @uml.property  name="idIdeiaSecundaria"
	 */
	public int getIdIdeiaSecundaria() {
		return idIdeiaSecundaria;
	}

	/**
	 * @param idIdeiaSecundaria
	 * @uml.property  name="idIdeiaSecundaria"
	 */
	public void setIdIdeiaSecundaria(int idIdeiaSecundaria) {
		this.idIdeiaSecundaria = idIdeiaSecundaria;
	}

	/**
	 * @return
	 * @uml.property  name="idIdeiaPrincipal"
	 */
	public int getIdIdeiaPrincipal() {
		return idIdeiaPrincipal;
	}

	/**
	 * @param idIdeiaPrincipal
	 * @uml.property  name="idIdeiaPrincipal"
	 */
	public void setIdIdeiaPrincipal(int idIdeiaPrincipal) {
		this.idIdeiaPrincipal = idIdeiaPrincipal;
	}

	/**
	 * @return
	 * @uml.property  name="sentimento"
	 */
	public String getSentimento() {
		return sentimento;
	}

	/**
	 * @param sentimento
	 * @uml.property  name="sentimento"
	 */
	public void setSentimento(String sentimento) {
		this.sentimento = sentimento;
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

	/**
	 * @return
	 * @uml.property  name="facebookPost"
	 */
	public String getFacebookPost() {
		return facebookPost;
	}

	/**
	 * @param facebookPost
	 * @uml.property  name="facebookPost"
	 */
	public void setFacebookPost(String facebookPost) {
		this.facebookPost = facebookPost;
	}

}