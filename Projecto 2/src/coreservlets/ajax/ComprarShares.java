package coreservlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import RMI.RMI;
import Util.Tx;
import Util.User;

@WebServlet("/ajax/ComprarShares")
public class ComprarShares extends HttpServlet {

	/**
	 * @uml.property name="rmi"
	 * @uml.associationEnd
	 */
	RMI rmi;

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		int idIdeia = -1;
		int numShares = -1;
		String tipo = null;
		double preco_por_share = -1;
		User user = (User) session.getAttribute("user");

		try {
			idIdeia = Integer.parseInt(request.getParameter("ideia"));
			numShares = (int) Math.round(Integer.parseInt(request.getParameter("num_shares")));
			tipo = (String) request.getParameter("tipo");
			preco_por_share = Double.parseDouble(request.getParameter("preco_por_share"));
			preco_por_share = (double) Math.round(preco_por_share * 10000) / 10000;

		} catch (Exception e) {

		}

		if (idIdeia > 0 && numShares > 0 && tipo != null && preco_por_share > 0.0 && user.getId() > 0) {

			ArrayList<Tx> tx = rmi.criarOrdem(0, idIdeia, user.getId(), numShares, preco_por_share, numShares * preco_por_share, (tipo.compareTo("limit") == 0) ? true : false);
			JSONObject res = new JSONObject();
			JSONArray txs = new JSONArray();

			int shares_compradas = 0;
			double ultimo_preco = 0.0;

			for (Tx t : tx) {
				JSONObject obj = new JSONObject();
				obj.put("tipo", t.getCompravenda());
				obj.put("ideia", t.getIdIdeia());
				obj.put("user", t.getIdUser());
				obj.put("num_shares", t.getNum_shares());
				obj.put("preco", t.getPreco_por_share());
				txs.add(obj);
				shares_compradas += t.getNum_shares();
				ultimo_preco = t.getPreco_por_share();
			}

			res.put("resultado", (shares_compradas == numShares) ? "compra_total" : ((shares_compradas == 0) ? "compra_pendente" : "compra_parcial"));
			if (shares_compradas > 0) {
				res.put("shares", shares_compradas);
				res.put("ultimo_preco", ultimo_preco);
				res.put("users", txs);
				res.put("ideia", idIdeia);

				rmi.criarOrdem(1, idIdeia, user.getId(), shares_compradas, ultimo_preco, 0, true);

				String fb = rmi.getFacebookPost(idIdeia);
				if (fb != null && fb.length() > 8 && user.getFacebookID() != null && user.getFacebookID().length() > 4) {
					double percent = (shares_compradas / 100000) * 100;
					user.fb.commentPost(fb, "Acabei de comprar " + (Math.round(percent * 100) / 100) + "% dessa ideia.");
				}
			}

			PrintWriter out = response.getWriter();
			out.println(res.toJSONString());
		}
	}
}