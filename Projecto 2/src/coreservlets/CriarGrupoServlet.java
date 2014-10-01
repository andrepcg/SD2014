package coreservlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.User;

@WebServlet("/CriarGrupoServlet")
@MultipartConfig
public class CriarGrupoServlet extends HttpServlet {

	RMI rmi;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		String nome = request.getParameter("nome");

		if (nome != null) {
			PrintWriter out = response.getWriter();
			User user = (User) session.getAttribute("user");

			int idgrupo = rmi.criarGrupo(nome, user.getId());

			if (idgrupo > 0)
				response.sendRedirect("/ideabroker/grupos.jsp");
			else
				response.sendRedirect("/ideabroker/criarGrupo.jsp");
		} else
			response.sendRedirect("/ideabroker/criarGrupo.jsp?erro=campos");
	}

}
