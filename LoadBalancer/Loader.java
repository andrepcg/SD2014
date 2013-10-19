import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Loader {

	private ArrayList<RemoteHost> servidores;
	private DatagramSocket serverSocket;
	boolean ligado;
	RemoteHost primario;
	Ping threadPing;

	public static void main(String[] args) {

		new Loader(getPort(args));

	}

	public Loader(int porta) {

		servidores = new ArrayList<RemoteHost>();
		ligado = true;

		try {
			serverSocket = new DatagramSocket(porta);
			System.out.println("Socket UDP aberto na porta " + porta);

		} catch (SocketException e) {
			System.out.println("Erro criar socket UDP");
		}

		threadPing = new Ping(servidores);
		threadPing.start();

		while (ligado) {
			byte[] receive = new byte[256];
			byte[] send = new byte[256];

			DatagramPacket receivePacket = null;
			receivePacket = new DatagramPacket(receive, receive.length);

			try {
				serverSocket.receive(receivePacket);
				String connect = new String(receivePacket.getData());
				String ip = receivePacket.getAddress().toString().split("/")[1];

				if (connect.startsWith("S")) {
					int port = receivePacket.getPort();
					RemoteHost s = new RemoteHost(ip, port);
					servidores.add(s);
					if (connect.contains("primario") || servidores.size() == 0) {
						primario = s;
						threadPing.setPrimario(s);
					}
					System.out.println("Server " + ((connect.contains("primario") || servidores.size() == 1) ? "primario " : "") + "ligado " + ip + " " + port);

				} else if (connect.startsWith("C")) {
					System.out.println("Cliente ligado");
					sendClientPrimario(receivePacket);
				}

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

	private void sendClientPrimario(DatagramPacket receivePacket) throws IOException {
		byte[] enviar = new byte[200];
		RemoteHost p = threadPing.getPrimario();
		enviar = ("S;" + p.getHost() + ";" + p.getPort() + ";").getBytes();

		DatagramPacket sendPacket = new DatagramPacket(enviar, enviar.length, receivePacket.getAddress(), receivePacket.getPort());
		serverSocket.send(sendPacket);
	}

	// TODO vai dar bode quando servers tem mesmo ip
	private RemoteHost getServer(String ip) {
		for (RemoteHost server : servidores) {
			if (server.getHost().compareTo(ip) == 0)
				return server;
		}
		return null;
	}

	private static int getPort(String args[]) {
		int defPort = 9000; /* Default port number */
		int length = args.length;
		for (int i = 0; i < length; i++) {
			if (args[i].equals("-port") || args[i].equals("-p")) {
				if (++i < length) {
					return (Integer.parseInt(args[i]));
				}
			}
		}
		return (defPort);
	}

}
