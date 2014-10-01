package coreservlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.User;

@WebServlet("/Fbconnect")
public class Fbconnect extends HttpServlet {

	/**
	 * @uml.property name="rmi"
	 * @uml.associationEnd
	 */
	RMI rmi;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String status = request.getParameter("status");
		String signed = request.getParameter("signedRequest");
		String auth = request.getParameter("accessToken");
		String userID = request.getParameter("userID");

		if (status != null && signed != null && auth != null && userID != null) {

			HttpSession session = request.getSession(false);
			rmi = (RMI) session.getAttribute("rmi");
			User user = (User) session.getAttribute("user");

			rmi.setUserFacebook(user.getId(), userID);

			PrintWriter out = response.getWriter();
			out.print("true");
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.print("get");
	}

}
