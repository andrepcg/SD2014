package Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

public class User implements Serializable, JSONAware {

	/**
	 * @uml.property  name="id"
	 */
	private int id;
	/**
	 * @uml.property  name="username"
	 */
	private String username;
	/**
	 * @uml.property  name="password"
	 */
	private String password;
	/**
	 * @uml.property  name="deicoins"
	 */
	private double deicoins;
	/**
	 * @uml.property  name="ultimoLogin"
	 */
	private Timestamp ultimoLogin;
	/**
	 * @uml.property  name="historico"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="Util.Transaccao"
	 */
	private ArrayList<Transaccao> historico;
	/**
	 * @uml.property  name="hash"
	 */
	private String hash;
	/**
	 * @uml.property  name="facebookID"
	 */
	private String facebookID;
	/**
	 * @uml.property  name="access_token"
	 */
	private String access_token;
	/**
	 * @uml.property  name="fb"
	 * @uml.associationEnd  
	 */
	public FacebookREST fb;

	public User(int id, String username, double deicoins, Timestamp timestamp) {
		this.id = id;
		this.username = username;
		this.deicoins = deicoins;
		this.ultimoLogin = timestamp;

	}

	public User(int id, String username) {
		this.id = id;
		this.username = username;
	}

	public User(String user) {
		user = user.replace("LOGIN|TRUE|", "");
		String[] split = user.split("\\|");

		this.id = Integer.parseInt(split[0]);
		this.username = split[1];
		this.deicoins = Double.parseDouble(split[2]);

	}

	public User(boolean t) {
		if (!t)
			this.username = "#failed#";
	}

	public ArrayList<Transaccao> historico() {
		return historico;
	}

	public void addTransaccao(Transaccao t) {
		historico.add(t);
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
	 * @uml.property  name="username"
	 */
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return username;
	}

	/**
	 * @param username
	 * @uml.property  name="username"
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return
	 * @uml.property  name="deicoins"
	 */
	public double getDeicoins() {
		return deicoins;
	}

	/**
	 * @param deicoins
	 * @uml.property  name="deicoins"
	 */
	public void setDeicoins(double deicoins) {
		this.deicoins = deicoins;
	}

	public String toString() {
		return id + " " + username + " " + deicoins;
	}

	/**
	 * @return
	 * @uml.property  name="ultimoLogin"
	 */
	public Timestamp getUltimoLogin() {
		return ultimoLogin;
	}

	/**
	 * @param ultimoLogin
	 * @uml.property  name="ultimoLogin"
	 */
	public void setUltimoLogin(Timestamp ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}

	public ArrayList<Transaccao> getHistorico() {
		return historico;
	}

	public void setHistorico(ArrayList<Transaccao> historico) {
		this.historico = historico;
	}

	public String toJSONString() {
		JSONObject obj = new JSONObject();
		obj.put("username", username);
		obj.put("id", new Integer(id));
		return obj.toString();
	}

	/**
	 * @param password
	 * @uml.property  name="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return
	 * @uml.property  name="hash"
	 */
	public String getHash() {
		return hash;
	}

	/**
	 * @param hash
	 * @uml.property  name="hash"
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}

	/**
	 * @return
	 * @uml.property  name="facebookID"
	 */
	public String getFacebookID() {
		return facebookID;
	}

	/**
	 * @param facebookID
	 * @uml.property  name="facebookID"
	 */
	public void setFacebookID(String facebookID) {
		this.facebookID = facebookID;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String accessCode) {
		this.access_token = accessCode;
	}

}
