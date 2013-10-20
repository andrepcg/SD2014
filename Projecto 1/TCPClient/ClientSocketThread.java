import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientSocketThread extends Thread {

	private Socket s;
	private boolean ligado;
	private boolean running;
	private DataOutputStream os;
	private DataInputStream is;
	private ClientTCP c;
	private RemoteHost loader;
	private RemoteHost server;

	private long lastHeartBeat;
	private long lastReconnect;

	private Thread outThread;
	private Thread inThread;

	private BlockingQueue<String> outboundPacketQueue;
	private BlockingQueue<String> inboundPacketQueue;

	private String topicos;
	boolean login;

	private Response resposta;

	private boolean heartBeatEnviado = false;
	private boolean heartBeatRecebido = false;

	public ClientSocketThread(ClientTCP c, RemoteHost loader) {
		this.c = c;
		this.loader = loader;
		running = true;

		outboundPacketQueue = new LinkedBlockingQueue<String>();
		inboundPacketQueue = new LinkedBlockingQueue<String>();

		server = getServerFromLoader();
		if (server != null) {
			if (connectSocket())
				System.out.println("## Ligado ##");
		}

		inThread = new Thread(new inbound());
		inThread.setName("inThread");
		inThread.setDaemon(true);

		outThread = new Thread(new outbound());
		outThread.setName("outThread");
		outThread.setDaemon(true);
	}

	public void run() {
		inThread.start();
		outThread.start();

		// heartbeat();
		lastHeartBeat = System.currentTimeMillis();

		while (running) {

			try {
				String input = inboundPacketQueue.peek();
				if (input != null) {
					inboundPacketQueue.take();
					comandos(input);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long time = System.currentTimeMillis();

			if (ligado && time - lastHeartBeat >= 3000)
				heartbeat();
			// else if (!(heartBeatEnviado && heartBeatRecebido) && time -
			// lastHeartBeat >= 1000)
			// if (time - lastReconnect >= 1000)
			// reconnect();
		}
	}

	public boolean getLogin() {
		return login;
	}

	private void comandos(String input) {

		if (input.contains("heartbeat")) {
			heartBeatRecebido = true;

		} else if (input.contains("LOGIN")) {
			if (input.contains("true")) {
				sincronizar(resposta, "TRUE");
			}
		} else if (input.contains("REGISTAR")) {
			if (input.contains("true")) {
				sincronizar(resposta, "TRUE");
			}
		} else if (input.startsWith("TOPICOS")) {
			sincronizar(resposta, input);

		} else if (input.startsWith("HISTORICOTRANSACCOES")) {
			sincronizar(resposta, input);
		}

	}

	private void sincronizar(Response resposta, String valorResposta) {
		synchronized (resposta) {
			resposta.setResposta(valorResposta);
			resposta.notify();
		}
	}

	public boolean registar(String username, String password) {
		password = md5(password);
		try {
			os.writeUTF("REGISTAR|" + username + "|" + password);
			boolean sucesso = is.readBoolean();
			return sucesso;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void login(String username, String password) {

		outboundPacketQueue.add("LOGIN|" + username + "|" + md5(password));
	}

	public String listarTopicos() {
		return topicos;

	}

	public synchronized void adicionarPacote(String in) {
		outboundPacketQueue.add(in);
	}

	private void heartbeat() {
		adicionarPacote("heartbeat");
		heartBeatEnviado = true;
		lastHeartBeat = System.currentTimeMillis();

	}

	private boolean reconnect() {
		fecharSocket();
		lastReconnect = System.currentTimeMillis();
		if (!connectSocket()) {
			this.server = getServerFromLoader();
			if (server != null)
				return connectSocket();
		} else {
			System.out.println("## Ligacao restabelecida ##");
			ligado = true;
			return true;
		}

		return false;
	}

	private boolean connectSocket() {
		try {
			s = new Socket(server.getHostInet(), server.getPort());
			s.setSoTimeout(1000);
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
			// ois = new ObjectInputStream(s.getInputStream());

			// br = new BufferedReader(new
			// InputStreamReader(s.getInputStream()));
			// pw = new PrintWriter(s.getOutputStream(), true);

			ligado = true;
			System.out.println("## Ligado ao servidor ##");
			return true;
		} catch (UnknownHostException e) {

			// e.printStackTrace();

		} catch (IOException e) {

			// e.printStackTrace();
		}
		ligado = false;
		return false;

	}

	private RemoteHost getServerFromLoader() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(500);
			byte[] send = "C".getBytes();

			DatagramPacket sendPacket = new DatagramPacket(send, send.length, loader.getHostInet(), loader.getPort());

			socket.send(sendPacket);
		} catch (SocketException e) {

			e.printStackTrace();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

		byte[] receive = new byte[30];
		DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);
		try {
			socket.receive(receivePacket);
			socket.close();
			String data = new String(receivePacket.getData());

			if (data.startsWith("S")) {
				RemoteHost server;
				String[] split = data.split(";");
				server = new RemoteHost(split[1], split[2]);
				return server;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;

	}

	public boolean getLigado() {
		return this.ligado;
	}

	private String md5(String string) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(string.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

	public Response getResposta() {
		return resposta;
	}

	public void setResposta(Response resposta) {
		this.resposta = resposta;
	}

	private void fecharSocket() {
		try {
			os.close();
			is.close();
			s.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private class inbound implements Runnable {
		public void run() {

			while (!inThread.isInterrupted()) {
				try {
					String in = is.readUTF();
					// System.out.println(in);
					inboundPacketQueue.add(in);
				} catch (IOException e) {

					// e.printStackTrace();
				}
			}

		}
	}

	private class outbound implements Runnable {
		public void run() {

			// int packetSize;
			while (!inThread.isInterrupted()) {
				String p = null;
				try {
					p = outboundPacketQueue.take();
				} catch (InterruptedException ie) {
				}
				if (p != null) {
					// sock.getOutputStream().write(p.getEncodedPacket());
					try {
						os.writeUTF(p);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						reconnect();
					}
				}
			}

		}
	}
}
