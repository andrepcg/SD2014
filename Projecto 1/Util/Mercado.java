package Util;

import java.io.Serializable;
import java.util.ArrayList;

public class Mercado implements Serializable {

	private ArrayList<OrdemCompra> ordensCompra;
	private ArrayList<OrdemVenda> ordensVenda;

	public Mercado() {
		ordensCompra = new ArrayList<>();
		ordensVenda = new ArrayList<>();
	}

	public ArrayList<OrdemCompra> getOrdensCompra() {
		return ordensCompra;
	}

	public ArrayList<OrdemVenda> getOrdensVenda() {
		return ordensVenda;
	}

	public void addOrdemVenda(OrdemVenda o) {
		ordensVenda.add(o);
	}

	public void addOrdemCompra(OrdemCompra o) {
		ordensCompra.add(o);
	}

	public String getOrdensCompraString() {
		String x = "";

		if (ordensCompra.size() > 0) {
			for (OrdemCompra o : ordensCompra) {
				x += o.toString() + "|";
			}

			x = x.substring(0, x.length() - 1);
		}
		return x + "null";
	}

	public String getOrdensVendaString() {
		String x = "";

		if (ordensVenda.size() > 0) {
			for (OrdemVenda o : ordensVenda) {
				x += o.toString() + "|";
			}

			x = x.substring(0, x.length() - 1);
		}

		return x + "null";
	}

}
