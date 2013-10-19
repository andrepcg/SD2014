import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Scanner;

public class Server {

	ServerSocket serverSocket = null;
	Failover failover;
	String ip;
	static int porta;
	private static boolean primario;
	boolean ligado = true;
	SocketThread socketThread;

	public static void main(String[] args) {

		porta = getPort(args);
		String loader = getHost(args);
		primario = checkIfPrimary(args);

		// porta = 5000;
		// String loader = "localhost:9000";
		// boolean primary = true;

		if (loader != null && porta > 0) {
			new Server(loader);
		}

	}

	public Server(String loader) {

		connect(loader);

		failover = new Failover(this, porta);
		failover.start();

		socketThread = new SocketThread(this, porta);
		socketThread.start();

		Scanner scanIn = new Scanner(System.in);
		while (ligado) {

			System.out.print(">> ");
			String s = scanIn.nextLine();

			if (s.contentEquals("clientes")) {
				System.out.println(socketThread.listarClientes());
			}

		}
	}

	public void setPrimario(boolean b) {
		primario = b;
	}

	public boolean connect(String loader) {

		try {
			InetAddress ip = InetAddress.getByName(loader.split(":")[0]);
			int port = new Integer(loader.split(":")[1]).intValue();

			DatagramSocket socket = new DatagramSocket(porta);
			byte[] send = ("S " + (primario ? "primario " : "") + porta).getBytes();
			DatagramPacket sendPacket = new DatagramPacket(send, send.length, ip, port);
			socket.send(sendPacket);
			socket.close();

			return true;
		} catch (SocketException e1) {

			e1.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;

	}

	private static int getPort(String args[]) {

		int length = args.length;
		for (int i = 0; i < length; i++) {
			if (args[i].equals("-port") || args[i].equals("-p")) {
				if (++i < length) {
					return (Integer.parseInt(args[i]));
				}
			}
		}
		return 0;
	}

	private static String getHost(String args[]) {
		int length = args.length;

		for (int i = 0; i < length; i++) {
			if (args[i].equals("-loader") || args[i].equals("-l")) {
				if (++i < length) {
					return (args[i]);
				}
			}
		}
		return null;
	}

	private static boolean checkIfPrimary(String args[]) {
		int length = args.length;
		for (int i = 0; i < length; i++) {
			if (args[i].equals("-primary") || args[i].equals("-P")) {
				return (true);
			}
		}
		return (false);
	}

}
