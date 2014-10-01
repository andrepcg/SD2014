package coreservlets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Util.User;

@WebServlet("/WebSocket")
public class WebSocket extends WebSocketServlet {

	/**
	 * @uml.property  name="sequence"
	 */
	private final AtomicInteger sequence = new AtomicInteger(0);
	/**
	 * @uml.property  name="connections"
	 */
	private final Set<ChatMessageInbound> connections = new CopyOnWriteArraySet<ChatMessageInbound>();

	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		HttpSession session = request.getSession();
		return new ChatMessageInbound((User) session.getAttribute("user"));
	}

	/**
	 * @author  Andre
	 */
	private final class ChatMessageInbound extends MessageInbound {

		/**
		 * @uml.property  name="user"
		 * @uml.associationEnd  
		 */
		private final User user;

		private ChatMessageInbound(User user) {
			this.user = user;
		}

		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
		}

		protected void onClose(int status) {
			connections.remove(this);
		}

		protected void onTextMessage(CharBuffer message) throws IOException {
			String msg = message.toString();

			JSONParser parser = new JSONParser();
			JSONObject obj;
			JSONArray users = null;
			long ideia = 0;
			double ultimo_preco = 0;
			long shares = 0;
			String resultado;

			// {"ultimo_preco":4.5,"shares":300,"users":[{"tipo":"compra","preco":4.5,"num_shares":100,"ideia":36,"user":25},{"tipo":"compra","preco":4.5,"num_shares":200,"ideia":36,"user":26}],"resultado":"venda_parcial"}
			try {
				obj = (JSONObject) parser.parse(msg);
				ideia = (Long) obj.get("ideia");
				ultimo_preco = (Double) obj.get("ultimo_preco");
				shares = (Long) obj.get("shares");
				users = (JSONArray) obj.get("users");
			} catch (ParseException e) {
				e.printStackTrace();
			}

			broadcast(ideia, ultimo_preco, shares, users);

		}

		private void sendMessage(ChatMessageInbound connection, String msg) {
			try {
				CharBuffer buffer = CharBuffer.wrap(msg);
				connection.getWsOutbound().writeTextMessage(buffer);
			} catch (IOException ignore) {
			}
		}

		private void broadcast(long ideia, double preco, long shares, JSONArray users) throws IOException { // send message to all

			JSONArray p = new JSONArray();
			JSONObject obj = new JSONObject();
			JSONObject obj2 = new JSONObject();
			obj.put("ideia", ideia);
			obj.put("ultimo_preco", preco);
			obj.put("shares", shares);
			p.add(obj);
			obj2.put("preco", p);

			for (ChatMessageInbound connection : connections) {
				sendMessage(connection, obj2.toJSONString());

				if (users.size() > 0) {
					for (int i = 0; i < users.size(); i++) {
						JSONObject o = (JSONObject) users.get(i);
						long id = (Long) o.get("user");
						if (connection.user.getId() == id) {
							JSONArray x = new JSONArray();
							JSONObject y = new JSONObject();
							x.add(o);
							y.put("notificacao", x);

							sendMessage(connection, y.toJSONString());
						}
					}
				}
			}

		}

		protected void onBinaryMessage(ByteBuffer message) throws IOException {
			throw new UnsupportedOperationException("Binary messages not supported.");
		}
	}
}