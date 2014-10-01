package coreservlets;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.Config;
import Util.FacebookREST;
import Util.Strings;
import Util.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

	protected Config config = new Config();
	RMI rmi;

	public void init() throws ServletException {
		int rmiport = 6565;
		try {
			rmiport = Integer.parseInt(config.getProperty("rmiport"));
		} catch (Exception e) {
		}

		try {
			rmi = (RMI) LocateRegistry.getRegistry(config.getProperty("rmihost"), rmiport).lookup(config.getProperty("rmiregistry"));

			System.out.println("AUTH: RMI ligado");

		} catch (NotBoundException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = Strings.MD5(request.getParameter("password"));
		String remember = request.getParameter("remember");
		String userAgent = request.getHeader("User-Agent");
		String accao = request.getParameter("accao");

		if (accao != null && accao.compareTo("Login") == 0) {

			HttpSession session = request.getSession();
			session.setAttribute("rmi", rmi);

			User user = rmi.login(username, password);
			if (user != null) {

				if (user.getFacebookID() != null)
					user.fb = new FacebookREST(user.getFacebookID());

				session.setAttribute("username", user.getUsername());
				int id = user.getId();
				session.setAttribute("userId", id);

				session.setMaxInactiveInterval(30 * 60);

				if (remember != null) {
					Cookie userName = new Cookie("username", user.getUsername());
					userName.setMaxAge(30 * 60);
					response.addCookie(userName);
					Cookie userid = new Cookie("userid", (String) (user.getId() + ""));
					userid.setMaxAge(30 * 60);
					response.addCookie(userid);
					String hash = Strings.MD5(user.getUsername() + userAgent + user.getPassword());
					Cookie sec = new Cookie("sec", hash);
					userName.setMaxAge(30 * 60);
					response.addCookie(sec);
					user.setHash(hash);
				}

				session.setAttribute("user", user);
				response.sendRedirect("/ideabroker/home.jsp");

			} else {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp?erro=login");

				rd.include(request, response);
			}
		} else if (accao != null && accao.compareTo("Registar") == 0) {
			boolean registo = rmi.registo(username, password);
			if (!registo) {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp?registo=erro");
				rd.include(request, response);
			} else {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.jsp?registo=sucesso");
				rd.include(request, response);
			}

		}
	}

}
