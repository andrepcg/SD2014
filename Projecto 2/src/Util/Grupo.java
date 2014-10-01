package Util;

import java.io.Serializable;
import java.util.ArrayList;

public class Grupo implements Serializable {

	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="idManager"
	 */
	private int idManager;
	/**
	 * @uml.property  name="nome"
	 */
	private String nome;
	/**
	 * @uml.property  name="manager"
	 */
	private String manager;
	/**
	 * @uml.property  name="topicos"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="Util.Topico"
	 */
	private ArrayList<Topico> topicos;
	/**
	 * @uml.property  name="membros"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="Util.User"
	 */
	private ArrayList<User> membros;

	public Grupo(int id, String nome, int idManager) {
		this.id = id;
		this.nome = nome;
		this.idManager = idManager;
	}

	public Grupo(int id, String nome, int idManager, String manager) {
		this.id = id;
		this.nome = nome;
		this.idManager = idManager;
		this.manager = manager;
		topicos = new ArrayList<Topico>();
	}

	public void addTopico(Topico t) {
		topicos.add(t);
	}

	public ArrayList<Topico> getTopicos() {
		return topicos;
	}

	public void addUser(User u) {
		membros.add(u);
	}

	public ArrayList<User> getmembros() {
		return membros;
	}

	public void setMembros(ArrayList<User> userList) {
		membros = userList;
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
	 * @uml.property  name="idManager"
	 */
	public int getIdManager() {
		return idManager;
	}

	/**
	 * @param idManager
	 * @uml.property  name="idManager"
	 */
	public void setIdManager(int idManager) {
		this.idManager = idManager;
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
	 * @uml.property  name="manager"
	 */
	public String getManager() {
		return manager;
	}

	/**
	 * @param manager
	 * @uml.property  name="manager"
	 */
	public void setManager(String manager) {
		this.manager = manager;
	}

}
