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

@WebServlet("/ApagarIdeiaServlet")
@MultipartConfig
public class ApagarIdeia extends HttpServlet {

	/**
	 * @uml.property name="rmi"
	 * @uml.associationEnd
	 */
	RMI rmi;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");
		int idIdeia = 0;
		try {
			idIdeia = Integer.parseInt(request.getParameter("idIdeia"));
		} catch (Exception e) {
		}

		if (idIdeia > 0) {
			PrintWriter out = response.getWriter();
			User user = (User) session.getAttribute("user");
			String fb = rmi.getFacebookPost(idIdeia);

			if (rmi.eliminaIdeias(idIdeia, user.getId())) {
				if (fb != null)
					user.fb.delete(fb);
			}

			response.sendRedirect("/ideabroker/ideias.jsp");
		}

	}

}
