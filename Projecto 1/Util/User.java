package Util;

import java.io.Serializable;

public class User implements Serializable {

	private int id;
	private String username;
	private double deicoins;

	public User(int id, String username, double deicoins) {
		this.id = id;
		this.username = username;
		this.deicoins = deicoins;
	}

	public User(String user) {
		user = user.replace("LOGIN|TRUE|", "");
		String[] split = user.split("\\|");

		this.id = Integer.parseInt(split[0]);
		this.username = split[1];
		this.deicoins = Double.parseDouble(split[2]);
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

}
