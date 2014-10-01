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
import Util.User;

@WebServlet("/ajax/GetUsersFromGroup")
public class GetUsersFromGroup extends HttpServlet {

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
		try {
			idGrupo = Integer.parseInt(request.getParameter("id"));
		} catch (Exception e) {

		}

		if (idGrupo > 0) {
			ArrayList<User> utilizadores = rmi.getUsersFromGroup(idGrupo);
			JSONArray list = new JSONArray();

			for (User u : utilizadores)
				list.add(u);

			PrintWriter out = response.getWriter();
			out.println(list);
		}
	}

}