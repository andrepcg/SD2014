import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Failover extends Thread {

	private DatagramSocket serverSocket;
	Server server;

	public Failover(Server server, int port) {
		this.server = server;
		try {
			serverSocket = new DatagramSocket(port);
			System.out.println("Socket UDP ping aberto na porta " + port);

		} catch (SocketException e) {
			System.out.println("Erro criar socket UDP");
		}
	}

	public void run() {

		while (true) {

			byte[] receiveData = new byte[512];
			byte[] sendData = new byte[512];

			DatagramPacket receivePacket = null;
			receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				serverSocket.receive(receivePacket);
				String data = new String(receivePacket.getData());

				if (data.contains("PING")) {
					// System.out.println("## PING RECEIVED ##");
					pong(receivePacket.getAddress(), receivePacket.getPort());
				}

				if (data.contains("SET PRIMARIO")) {
					if (data.contains("TRUE"))
						server.setPrimario(true);
					else if (data.contains("FALSE"))
						server.setPrimario(false);
				}

			} catch (IOException e) {

				e.printStackTrace();
			}

		}
	}

	private boolean pong(InetAddress inetAddress, int port) {
		byte[] sendData = new byte[512];
		sendData = "PONG".getBytes();
		try {

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, port);
			serverSocket.send(sendPacket);

			return true;

		} catch (IOException e) {

			e.printStackTrace();
		}

		return false;

	}
}
