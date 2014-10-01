package coreservlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.Facebook;
import Util.FacebookREST;
import Util.User;

@WebServlet("/Fblogin")
public class Fblogin extends HttpServlet {

	/**
	 * @uml.property name="rmi"
	 * @uml.associationEnd
	 */
	RMI rmi;

	public void init() throws ServletException {
		try {
			rmi = (RMI) LocateRegistry.getRegistry(6565).lookup("registry");
			System.out.println("RMI ligado");

		} catch (NotBoundException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String status = request.getParameter("status");
		String signed = request.getParameter("signedRequest");
		String auth = request.getParameter("accessToken");
		String userID = request.getParameter("userID");

		if (status != null && signed != null && auth != null && userID != null) {
			HttpSession session = request.getSession();

			Facebook fb = new Facebook("e1efddd7dec79d30af7744290110c77d", signed, auth);

			if (fb.isHashCorrecta() && userID.compareTo(fb.getUser_id()) == 0) {

				User user = rmi.getUserFacebook(userID);
				if (user != null) {
					// user.fb = new FacebookREST(auth, userID);
					user.fb = new FacebookREST(userID, auth);
					user.setAccessToken(auth);

					session.setAttribute("user", user);
					session.setAttribute("username", user.getUsername());
					int id = user.getId();
					session.setAttribute("userId", id);
					session.setAttribute("rmi", rmi);

					PrintWriter out = response.getWriter();
					out.print("home.jsp");
				} else {
					PrintWriter out = response.getWriter();
					out.print("login.jsp?erro=login");
				}
				// response.sendRedirect("/ideabroker/home.jsp");
			} else {
				PrintWriter out = response.getWriter();
				out.print("login.jsp?erro=login");
			}
		} else {
			// RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp?erro=login");

			// rd.include(request, response);
			PrintWriter out = response.getWriter();
			out.print("login.jsp?erro=login");
		}

	}
}
