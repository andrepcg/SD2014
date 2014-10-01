package coreservlets.filters;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import RMI.RMI;
import Util.Config;
import Util.Strings;
import Util.User;

@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter {

	private ServletContext context;
	RMI rmi;
	protected Config config = new Config();

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		this.context = fConfig.getServletContext();

		int rmiport = 6565;
		try {
			rmiport = Integer.parseInt(config.getProperty("rmiport"));
		} catch (Exception e) {
		}

		try {
			rmi = (RMI) LocateRegistry.getRegistry(config.getProperty("rmihost"), rmiport).lookup(config.getProperty("rmiregistry"));

			System.out.println("AUTH: RMI ligado");
			this.context.log("AUTH: RMI ligado");

		} catch (NotBoundException ex) {
			ex.printStackTrace();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}

		this.context.log("AuthenticationFilter initialized");
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String uri = req.getRequestURI();
		// this.context.log("Requested Resource::" + uri);

		HttpSession session = req.getSession(false);

		if (session != null)
			session.setAttribute("rmi", rmi);

		User user = null;
		String username = null;
		String sec = null;
		int userid = -1;
		try {
			username = (String) session.getAttribute("username");
			user = (User) session.getAttribute("user");
		} catch (Exception e) {

		}

		if (user == null) {
			Cookie[] cookies = ((HttpServletRequest) request).getCookies();
			if (cookies != null)
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals("username"))
						username = cookie.getValue();
					else if (cookie.getName().equals("sec"))
						sec = cookie.getValue();
					else if (cookie.getName().equals("userid"))
						try {
							userid = Integer.parseInt(cookie.getValue());
						} catch (Exception e) {
							// TODO: handle exception
						}

				}

			String userAgent = ((HttpServletRequest) request).getHeader("User-Agent");

			if (username != null && sec != null && userid != -1) {
				User temp = rmi.getUser(userid);
				String md5 = Strings.MD5(username + userAgent + temp.getPassword());
				if (md5.compareTo(sec) == 0) {
					user = temp;
					session = req.getSession(true);
					session.setAttribute("username", temp.getUsername());
					session.setAttribute("userId", temp.getId());
					session.setAttribute("user", temp);
				}
			}
		}

		if ((session == null || user == null || session.getAttribute("rmi") == null) && !(uri.endsWith("LoginServlet") || uri.endsWith("Fblogin") || uri.endsWith("Fbconnect") || uri.endsWith("login.jsp") || uri.endsWith("css") || uri.endsWith("js"))) {
			this.context.log("Unauthorized access request");
			res.sendRedirect("login.jsp");
		} else {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}

	}

	public void destroy() {
		// close any resources here
	}

}