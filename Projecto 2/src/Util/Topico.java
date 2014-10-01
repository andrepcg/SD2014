package Util;

import java.io.Serializable;

public class Topico implements Serializable {

	/**
	 * @uml.property  name="nome"
	 */
	private String nome;
	/**
	 * @uml.property  name="tag"
	 */
	private String tag;
	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="numIdeias"
	 */
	private int numIdeias;

	public Topico(int id, String nome, String tag) {
		this.setId(id);
		this.setNome(nome);
		this.tag = tag;
	}

	public Topico(int id, String nome, String tag, int numIdeias) {
		this.setId(id);
		this.setNome(nome);
		this.tag = tag;
		this.numIdeias = numIdeias;
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
	 * @uml.property  name="nome"
	 */
	public String getNome() {
		return nome;
	}

	/**
	 * @param nome
	 * @uml.property  name="nome"
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/**
	 * @return
	 * @uml.property  name="tag"
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 * @uml.property  name="tag"
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return
	 * @uml.property  name="numIdeias"
	 */
	public int getNumIdeias() {
		return numIdeias;
	}

	/**
	 * @param numIdeias
	 * @uml.property  name="numIdeias"
	 */
	public void setNumIdeias(int numIdeias) {
		this.numIdeias = numIdeias;
	}

}