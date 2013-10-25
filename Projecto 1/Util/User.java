package Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class User implements Serializable {

	private int id;
	private String username;
	private double deicoins;
	private Timestamp ultimoLogin;
	private ArrayList<Transaccao> historico;

	public User(int id, String username, double deicoins, Timestamp timestamp) {
		this.id = id;
		this.username = username;
		this.deicoins = deicoins;
		this.ultimoLogin = timestamp;

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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getDeicoins() {
		return deicoins;
	}

	public void setDeicoins(double deicoins) {
		this.deicoins = deicoins;
	}

	public String toString() {
		return id + " " + username + " " + deicoins;
	}

	public Timestamp getUltimoLogin() {
		return ultimoLogin;
	}

	public void setUltimoLogin(Timestamp ultimoLogin) {
		this.ultimoLogin = ultimoLogin;
	}

	public ArrayList<Transaccao> getHistorico() {
		return historico;
	}

	public void setHistorico(ArrayList<Transaccao> historico) {
		this.historico = historico;
	}

}
