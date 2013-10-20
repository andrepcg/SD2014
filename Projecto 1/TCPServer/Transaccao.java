import java.sql.Timestamp;

public class Transaccao {
	private int idIdeia;
	private int idUser;
	private int numShares;
	private double preco;
	private String tipo; // compra, venda
	private Timestamp timestamp;
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

	public int getNumShares() {
		return numShares;
	}

	public void setNumShares(int numShares) {
		this.numShares = numShares;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public double getPago() {
		return pago;
	}

	public void setPago(double pago) {
		this.pago = pago;
	}

}
