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

import Util.Objecto;
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
	private Thread releaseLock;
	private Thread inThreadObj;

	private BlockingQueue<String> outboundPacketQueue;
	public BlockingQueue<Object> inboundPacketQueueObj;

	private String topicos;
	boolean login;

	private Response resposta;
	private Objecto obj;

	private boolean heartBeatEnviado = false;
	private boolean heartBeatRecebido = false;
	private boolean loaderStatus = false;
	private ObjectInputStream ois;

	public ClientSocketThread(RemoteHost loader) {
		this.loader = loader;
		running = true;

		outboundPacketQueue = new LinkedBlockingQueue<String>();
		inboundPacketQueueObj = new LinkedBlockingQueue<Object>();

		server = getServerFromLoader();
		// synchronized (server) {
		// server.notify();
		// }

		if (server != null) {
			connectSocket();
			// System.out.println("## Ligado ##\n");
		}

		inThreadObj = new Thread(new inboundObject());
		inThreadObj.setName("inThreadObj");
		inThreadObj.setDaemon(true);

		outThread = new Thread(new outbound());
		outThread.setName("outThread");
		outThread.setDaemon(true);

		releaseLock = new Thread(new releaseLock());
		releaseLock.setName("releaseLock");
		releaseLock.setDaemon(true);
	}

	public void run() {
		inThreadObj.start();
		outThread.start();
		// releaseLock.start();

		lastHeartBeat = System.currentTimeMillis();

		while (running) {

			try {
				Object input = inboundPacketQueueObj.peek();
				if (input != null) {
					inboundPacketQueueObj.take();
					comandos(input);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			long time = System.currentTimeMillis();

			if (ligado && time - lastHeartBeat >= 3000) {
				heartbeat();
			} else if ((heartBeatEnviado && !heartBeatRecebido) && time - lastHeartBeat >= 500 && time - lastReconnect >= 2000)
				reconnect();
		}
	}

	public boolean getLogin() {
		return login;
	}

	private void comandos(Object input) {

		if (input instanceof String) {
			String input1 = (String) input;

			if (input1.contains("heartbeat")) {
				heartBeatRecebido = true;

			} else if (input1.contains("LOGIN")) {

				if (input1.contains("TRUE"))
					sincronizar(resposta, input1);

				else if (input1.contains("FALSE"))
					sincronizar(resposta, "FALSE");

			} else if (input1.contains("REGISTO")) {

				if (input1.contains("TRUE")) {
					sincronizar(resposta, "TRUE");
				}

			} else {
				sincronizar(resposta, input1);
			}
		} else
			sincronizarObjecto(obj, input);

	}

	private void sincronizarObjecto(Objecto obj2, Object input) {
		synchronized (obj2) {
			obj2.setObj(input);
			obj2.notify();
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
		// sincronizar(resposta, "");
		// sincronizarObjecto(obj, null);
		lastReconnect = System.currentTimeMillis();
		if (lastReconnect - lastHeartBeat >= 1000)
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
		return false;

	}

	private boolean connectSocket() {
		try {
			s = new Socket(server.getHostInet(), server.getPort());
			s.setSoTimeout(1000);
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());

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
			loaderStatus = true;
			if (data.startsWith("S")) {
				RemoteHost server;
				String[] split = data.split(";");
				if (!split[1].contains("null")) {
					server = new RemoteHost(split[1], split[2]);
					return server;

				} else {
					server = null;
					// delayConnect = System.currentTimeMillis()+3000;
				}
			}
		} catch (IOException e) {
			loaderStatus = false;
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
		ligado = false;
		try {
			Object o = ois.readObject();
			ligado = true;
			return o;

		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		ligado = true;
		return null;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Objecto obj) {
		this.obj = obj;
	}

	private class inboundObject implements Runnable {
		public void run() {

			while (!inThreadObj.isInterrupted()) {
				try {
					Object in = ois.readObject();

					inboundPacketQueueObj.add(in);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

		}
	}

	private class releaseLock implements Runnable {
		public void run() {

			while (!releaseLock.isInterrupted()) {
				try {
					Thread.sleep(5000);
					sincronizar(resposta, "");
					sincronizarObjecto(obj, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	private class outbound implements Runnable {
		public void run() {

			// int packetSize;
			while (!outThread.isInterrupted()) {
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

	public void registar(String username, String password) {
		adicionarPacote("REGISTAR|" + username + "|" + md5(password));

	}
}
