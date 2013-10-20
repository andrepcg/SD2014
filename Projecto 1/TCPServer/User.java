public class User {

	private int id;
	private String username;
	private double deicoins;

	public User(int id, String username, double deicoins) {
		this.id = id;
		this.username = username;
		this.deicoins = deicoins;
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
}
