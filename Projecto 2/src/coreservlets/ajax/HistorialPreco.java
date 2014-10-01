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

import RMI.RMI;
import Util.Transaccao;

@WebServlet("/ajax/HistorialPreco")
public class HistorialPreco extends HttpServlet {

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
			ArrayList<Transaccao> tx = rmi.getHistorialPreco(idIdeia);
			JSONArray res = new JSONArray();

			for (Transaccao t : tx) {
				JSONArray a = new JSONArray();
				a.add(t.getTimestamp().getTime());
				a.add(t.getPreco());
				res.add(a);
			}

			PrintWriter out = response.getWriter();
			out.print(res.toJSONString());
		}
	}

}
