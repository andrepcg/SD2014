package coreservlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import RMI.RMI;
import Util.Mercado;
import Util.OrdemCompra;
import Util.OrdemVenda;

@WebServlet("/ajax/MercadoOrdens")
public class MercadoAjax extends HttpServlet {

	/**
	 * @uml.property  name="rmi"
	 * @uml.associationEnd  
	 */
	RMI rmi;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		int idIdeia = -1;
		try {
			idIdeia = Integer.parseInt(request.getParameter("ideia"));
		} catch (Exception e) {

		}

		if (idIdeia > 0) {
			Mercado mercado = rmi.getMercado(idIdeia);

			JSONArray ordensCompra = new JSONArray();
			for (OrdemCompra o : mercado.getOrdensCompra()) {
				JSONObject obj = new JSONObject();
				obj.put("qtd", o.getNum_shares());
				obj.put("preco", o.getPreco_por_share());
				ordensCompra.add(obj);
			}

			JSONArray ordensVenda = new JSONArray();
			for (OrdemVenda o : mercado.getOrdensVenda()) {
				JSONObject obj = new JSONObject();
				obj.put("qtd", o.getNum_shares());
				obj.put("preco", o.getPreco_por_share());
				ordensVenda.add(obj);
			}

			JSONObject res = new JSONObject();
			res.put("compras", ordensCompra);
			res.put("vendas", ordensVenda);

			PrintWriter out = response.getWriter();
			out.print(res.toJSONString());
		}
	}

}
