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

@WebServlet("/ajax/EditarOrdem")
public class EditarOrdem extends HttpServlet {

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
		int idOrdem = -1;
		String tipo = null;
		double preco_por_share = -1;
		String accao = null;
		// compra ou venda
		String classe = null;
		User user = (User) session.getAttribute("user");

		try {
			idIdeia = Integer.parseInt(request.getParameter("ideia"));
			idOrdem = Integer.parseInt(request.getParameter("idOrdem"));
			numShares = (int) Math.round(Integer.parseInt(request.getParameter("num_shares")));
			tipo = (String) request.getParameter("tipo");
			accao = (String) request.getParameter("accao");
			classe = (String) request.getParameter("classe");
			preco_por_share = Double.parseDouble(request.getParameter("preco_por_share"));
			preco_por_share = (double) Math.round(preco_por_share * 100) / 100;

		} catch (Exception e) {

		}

		if (idOrdem > 0 && idIdeia > 0 && accao != null && classe != null && tipo != null && preco_por_share > 0.0 && user.getId() > 0) {

			String s = (classe.compareTo("compra") == 0) ? "compra" : "venda";
			int st = (classe.compareTo("compra") == 0) ? 0 : 1;
			PrintWriter out = response.getWriter();

			if (accao.compareTo("editar") == 0) {
				ArrayList<Tx> tx = rmi.editarOrdem((classe.compareTo("compra") == 0) ? 0 : 1, idOrdem, user.getId(), numShares, preco_por_share, (tipo.compareTo("limit") == 0) ? true : false);

				JSONObject res = new JSONObject();
				JSONArray txs = new JSONArray();

				int shares_trocadas = 0;
				double ultimo_preco = 0.0;

				for (Tx t : tx) {

					JSONObject obj = new JSONObject();
					obj.put("tipo", t.getCompravenda());
					obj.put("ideia", t.getIdIdeia());
					obj.put("user", t.getIdUser());
					obj.put("num_shares", t.getNum_shares());
					obj.put("preco", t.getPreco_por_share());
					txs.add(obj);
					shares_trocadas += t.getNum_shares();
					ultimo_preco = t.getPreco_por_share();
				}

				res.put("resultado", (shares_trocadas == numShares) ? s + "_total" : ((shares_trocadas == 0) ? s + "_pendente" : s + "_parcial"));
				if (shares_trocadas > 0) {
					res.put("shares", shares_trocadas);
					res.put("ultimo_preco", ultimo_preco);
					res.put("ideia", idIdeia);
					res.put("users", txs);

					if (classe.equals("compra"))
						rmi.criarOrdem(1, idIdeia, user.getId(), shares_trocadas, ultimo_preco, 0, true);

					if (classe.equals("venda")) {
						for (Tx t : tx) {
							rmi.criarOrdem(1, idIdeia, t.getIdUser(), shares_trocadas, ultimo_preco, 0, true);
						}
					}
				}

				out.println(res.toJSONString());
			} else if (accao.compareTo("remover") == 0) {
				boolean r = rmi.removerOrdem(user.getId(), st, idOrdem);
				JSONObject res = new JSONObject();
				res.put("resultado", (r == true) ? "removido" : "erro");
				out.println(res.toJSONString());
			}
		}
	}

}