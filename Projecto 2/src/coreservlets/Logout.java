package coreservlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Logout")
public class Logout extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie username = null;
		Cookie userid = null;
		Cookie sec = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username"))
					username = cookie;
				else if (cookie.getName().equals("sec"))
					sec = cookie;
				else if (cookie.getName().equals("userid"))
					userid = cookie;
			}
		}
		if (username != null && userid != null && sec != null) {
			username.setMaxAge(0);
			response.addCookie(username);

			userid.setMaxAge(0);
			response.addCookie(userid);

			sec.setMaxAge(0);
			response.addCookie(sec);
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		response.sendRedirect("login.jsp");
	}

}
