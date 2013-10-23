package Util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Mercado implements Serializable {

	private ArrayList<OrdemCompra> ordensCompra;
	private ArrayList<OrdemVenda> ordensVenda;

	public Mercado() {
		ordensCompra = new ArrayList<>();
		ordensVenda = new ArrayList<>();
	}

	public Ordem[] ordensPrecoMatch() {
		ArrayList<Ordem> ordensC = new ArrayList<>();
		ArrayList<Ordem> ordensV = new ArrayList<>();

		for (OrdemCompra oC : ordensCompra) {
			for (OrdemVenda oV : ordensVenda) {
				if (oC.getPreco_por_share() == oV.getPreco_por_share()) {
					if (!ordensC.contains(oC))
						ordensC.add(oC);
					if (!ordensV.contains(oV))
						ordensV.add(oV);
				}
			}
		}

		if (ordensC.size() > 0 && ordensV.size() > 0) {

			OrdemCompra menorC = null;
			OrdemVenda menorV = null;

			if (ordensC.size() > 1) {
				menorC = (OrdemCompra) menorTimestamp(ordensC);
			} else
				menorC = (OrdemCompra) ordensC.get(0);

			if (ordensV.size() > 1) {
				menorV = (OrdemVenda) menorTimestamp(ordensV);
			} else
				menorV = (OrdemVenda) ordensV.get(0);

			Ordem[] match = new Ordem[2];
			match[0] = menorC;
			match[1] = menorV;

			return match;
		}

		return null;
	}

	private Ordem menorTimestamp(ArrayList<Ordem> lista) {
		Timestamp menor = Timestamp.valueOf("9999-01-01 12:12:12");
		Ordem menorOrdem = null;

		for (Ordem o : lista)
			if (o.getTimestamp().compareTo(menor) < 0) {
				menorOrdem = o;
				menor = o.getTimestamp();
			}

		return menorOrdem;
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
			return x;
		} else
			return x + "null";
	}

	public String getOrdensVendaString() {
		String x = "";

		if (ordensVenda.size() > 0) {
			for (OrdemVenda o : ordensVenda) {
				x += o.toString() + "|";
			}

			x = x.substring(0, x.length() - 1);
			return x;
		} else
			return x + "null";
	}

}
