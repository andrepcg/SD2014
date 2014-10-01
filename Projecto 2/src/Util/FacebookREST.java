package Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FacebookREST implements Serializable {

	/**
	 * @uml.property  name="accessToken"
	 */
	private String accessToken;
	/**
	 * @uml.property  name="uid"
	 */
	private String uid;
	/**
	 * @uml.property  name="graph"
	 */
	private final String graph = "https://graph.facebook.com/";

	public FacebookREST(String user_id, String access_token) {
		this.uid = user_id;
		this.accessToken = access_token;
	}

	public FacebookREST(String user_id) {
		this.uid = user_id;
		this.accessToken = "327065317436007|yqzXHSWr5YVawemw50gHFECMqiY";
	}

	public JSONObject commentPost(String postID, String msg) {
		String parameters = "method=POST&format=json&message=" + msg + "&access_token=" + accessToken;
		return post(parameters, graph + postID + "/comments");
	}

	public JSONObject publishFeed(String msg) {
		String parameters = "method=POST&format=json&message=" + msg + "&access_token=" + accessToken;
		return post(parameters, graph + uid + "/feed");

	}

	public JSONObject delete(String id) {
		String parameters = "method=DELETE&format=json&access_token=" + accessToken;
		return post(parameters, graph + id);
	}

	private JSONObject post(String parameters, String urla) {

		try {
			URL url = new URL(urla);
			URLConnection conn = url.openConnection();

			conn.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(parameters);
			writer.flush();

			String line = null;
			String finali = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((line = reader.readLine()) != null) {
				finali += line;
			}
			writer.close();
			reader.close();

			JSONParser p = new JSONParser();
			JSONObject obj = null;
			try {
				obj = (JSONObject) p.parse(finali);
			} catch (ParseException e) {
				// e.printStackTrace();
				obj.put("result", finali);
			}

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return
	 * @uml.property  name="uid"
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid
	 * @uml.property  name="uid"
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}
}
