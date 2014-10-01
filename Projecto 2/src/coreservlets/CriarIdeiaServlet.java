package coreservlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.json.simple.JSONObject;

import RMI.RMI;
import Util.Strings;
import Util.User;

@WebServlet("/CriarIdeiaServlet")
@MultipartConfig
public class CriarIdeiaServlet extends HttpServlet {

	/**
	 * @uml.property name="rmi"
	 * @uml.associationEnd
	 */
	RMI rmi;

	/**
	 * @uml.property name="filePath"
	 */
	private String filePath;
	/**
	 * @uml.property name="maxFileSize"
	 */
	private int maxFileSize = 50 * 1024;
	/**
	 * @uml.property name="maxMemSize"
	 */
	private int maxMemSize = 4 * 1024;
	/**
	 * @uml.property name="file"
	 */
	private File file;

	/**
	 * @uml.property name="fileDir"
	 */
	private final String fileDir = "C:\\Users\\Andre\\Dropbox\\LEI\\Java\\ideabroker\\WebContent\\ficheiros\\";

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		rmi = (RMI) session.getAttribute("rmi");

		String titulo = request.getParameter("titulo");
		String texto = request.getParameter("texto");
		String topicos = Strings.lowercase(Strings.replaceSpecialChars(request.getParameter("topicosEscolhidos")));
		double investir = Double.parseDouble(request.getParameter("investir"));
		int idGrupo = Integer.parseInt(request.getParameter("grupo"));

		if (titulo != null && texto != null && topicos != null && investir > 0.0 && idGrupo >= 0) {

			ArrayList<String> topicos_strip = new ArrayList<String>();
			String[] topicos2 = topicos.split(" ");
			for (int i = 0; i < topicos2.length; i++) {
				if (topicos2[i].matches("(\\s)*#(\\w)+")) {
					topicos_strip.add(topicos2[i].substring(topicos2[i].indexOf("#") + 1, topicos2[i].length()));
				}
			}

			// TODO verificar se existem topicos

			if (topicos_strip.size() > 0) {

				PrintWriter out = response.getWriter();
				/*
				 * for (String s : topicos_strip) out.println(s);
				 */
				Part filePart = request.getPart("file");
				String filename = getFilename(filePart);
				String fPath = null;
				if (filename.length() > 1)
					fPath = saveFile(filePart.getInputStream(), filename);

				// session.setAttribute("username", user.getUsername());

				User user = (User) session.getAttribute("user");

				String fb_id = null;

				if (user.fb != null) {
					String t = "";
					for (int i = 0; i < topicos_strip.size(); i++)
						t += "#" + topicos_strip.get(i);

					JSONObject fb = user.fb.publishFeed("[" + titulo + "] " + texto + " " + t);
					fb_id = (String) fb.get("id");

				}

				int idNovaIdeia = rmi.criarIdeia(user.getId(), topicos_strip, titulo, texto, investir, idGrupo, fPath, fb_id);

				response.sendRedirect("/ideabroker/ideia.jsp?id=" + idNovaIdeia);
			} else
				response.sendRedirect("/ideabroker/criarIdeia.jsp?erro=campos");
		} else
			response.sendRedirect("/ideabroker/criarIdeia.jsp?erro=campos");
	}

	private String saveFile(InputStream stream, String filename) {
		try {
			String fName = fileDir + System.currentTimeMillis() + "_" + filename;
			FileOutputStream outputStream = new FileOutputStream(new File(fName));

			int read = 0;
			byte[] bytes = new byte[50 * 1024];

			while ((read = stream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			outputStream.close();

			return fName;
		} catch (Exception e) {

		}

		return null;
	}

	private static String getFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}

}
