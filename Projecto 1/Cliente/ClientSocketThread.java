package Cliente;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Util.RemoteHost;
import Util.Response;

public class ClientSocketThread extends Thread {

	private Socket s;
	private boolean ligado;
	private boolean running;
	private DataOutputStream os;
	private DataInputStream is;
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

	public ClientSocketThread(RemoteHost loader) {
		this.loader = loader;
		running = true;

		outboundPacketQueue = new LinkedBlockingQueue<String>();
		inboundPacketQueue = new LinkedBlockingQueue<String>();

		server = getServerFromLoader();
		// synchronized (server) {
		// server.notify();
		// }

		if (server != null) {
			connectSocket();
			// System.out.println("## Ligado ##\n");
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

			if (ligado && time - lastHeartBeat >= 3000) {
				heartbeat();
			} else if ((heartBeatEnviado && !heartBeatRecebido) && time - lastHeartBeat >= 500)
				reconnect();
		}
	}

	public boolean getLogin() {
		return login;
	}

	private void comandos(String input) {

		if (input.contains("heartbeat")) {
			heartBeatRecebido = true;

		} else if (input.contains("LOGIN")) {

			if (input.contains("TRUE"))
				sincronizar(resposta, input);

			else if (input.contains("FALSE"))
				sincronizar(resposta, "FALSE");

		} else if (input.contains("REGISTO")) {

			if (input.contains("TRUE")) {
				sincronizar(resposta, "TRUE");
			}

		} else {
			sincronizar(resposta, input);
		}

	}

	private void sincronizar(Response resposta, String valorResposta) {
		synchronized (resposta) {
			resposta.setResposta(valorResposta);
			resposta.notify();
		}
	}

	// public boolean registar(String username, String password) {
	// password = md5(password);
	//
	// adicionarPacote("REGISTAR|" + username + "|" + password);
	//
	// }

	public void login(String username, String password) {

		adicionarPacote("LOGIN|" + username + "|" + md5(password));
	}

	public void enviarFicheiro(String dir) {
		try {
			File file = new File(dir);
			// adicionarPacote("RECEBER_FICHEIRO" + file.getName() + "|" +
			// file.length());

			byte[] mybytearray = new byte[(int) file.length()];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(mybytearray, 0, mybytearray.length);
			OutputStream os = s.getOutputStream();
			os.write(mybytearray, 0, mybytearray.length);
			os.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
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

	private synchronized boolean reconnect() {
		fecharSocket();
		lastReconnect = System.currentTimeMillis();
		if (!connectSocket()) {
			this.server = getServerFromLoader();
			if (server != null)
				return connectSocket();
			else
				return false;
		} else {
			System.out.println("## Ligacao restabelecida ##\n");
			ligado = true;
			return true;
		}
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
			// System.out.println("## Ligado ao servidor ##\n");
			return true;
		} catch (UnknownHostException e) {

			// e.printStackTrace();

		} catch (Exception e) {

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
				if (!split[1].contains("null")) {
					server = new RemoteHost(split[1], split[2]);
					return server;

				} else
					server = null;
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		return null;

	}

	public boolean getLigado() {
		return this.ligado;
	}

	public void setLigado(boolean t) {
		this.ligado = t;
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

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public RemoteHost getServer() {
		return server;
	}

	public Object receberObjecto() {
		try {
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
			Object o = ois.readObject();
			return o;
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		return null;
	}

	private class inbound implements Runnable {
		public void run() {

			while (!inThread.isInterrupted()) {
				try {
					String in = is.readUTF();
					// System.out.println(in);
					inboundPacketQueue.add(in);
				} catch (IOException e) {

					// reconnect();
				} catch (Exception e) {

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
					boolean enviado = false;
					while (!enviado) {
						try {
							os.writeUTF(p);
							enviado = true;
						} catch (Exception e) {

							reconnect();
						}
					}
				}
			}

		}
	}
}
