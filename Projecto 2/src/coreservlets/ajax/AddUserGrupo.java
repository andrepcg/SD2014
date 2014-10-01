package coreservlets.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;

@WebServlet("/ajax/AddUserGrupo")
public class AddUserGrupo extends HttpServlet {

	/**
	 * @uml.property  name="rmi"
	 * @uml.associationEnd  
	 */
	RMI rmi;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		int idGrupo = -1;
		int uid = -1;
		String uname = "";
		try {
			idGrupo = Integer.parseInt(request.getParameter("grupo"));

			if (request.getParameter("uid").matches("\\b[0-9]+\\b")) {
				uid = Integer.parseInt(request.getParameter("uid"));
			} else
				uname = request.getParameter("uid");
		} catch (Exception e) {

		}

		if (idGrupo > 0 && (uid > 0 || uname != "")) {

			boolean v = rmi.addUserGrupo(idGrupo, uid, uname);
			PrintWriter out = response.getWriter();
			out.println((v == true) ? ((uid > 0 ? uid : uname) + ";adicionado") : "-1;erro");
		}
	}

}