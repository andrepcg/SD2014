package coreservlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.Ideia;
import Util.Strings;
import Util.Topico;

@WebServlet("/Pesquisar")
public class Pesquisar extends HttpServlet {

	/**
	 * @uml.property  name="rmi"
	 * @uml.associationEnd  
	 */
	RMI rmi;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pesquisa = Strings.replaceSpecialChars(request.getParameter("pesquisa"));
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		PrintWriter out = response.getWriter();

		if (pesquisa.matches("(\\s)*#(\\w)+")) {
			// pesquisa topico
			String[] p = pesquisa.split(" ");
			pesquisa = p[0].replace("#", "");
			ArrayList<Topico> topicos = rmi.pesquisaTopico(pesquisa);
			// for (Topico t : topicos)
			// out.println(t.getTag());

			request.setAttribute("topicos", topicos);
			request.setCharacterEncoding("UTF-8");
			request.getRequestDispatcher("/WEB-INF/pesquisaTopicos.jsp").include(request, response);
		} else {
			String[] p = pesquisa.split(" ");
			ArrayList<Ideia> ideias = rmi.pesquisarIdeias(p);
			// for (Ideia i : ideias)
			// out.println(i.getTexto());
			request.setAttribute("ideias", ideias);
			request.setCharacterEncoding("UTF-8");
			request.getRequestDispatcher("/WEB-INF/pesquisaIdeias.jsp").include(request, response);
		}

	}
}