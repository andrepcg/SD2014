import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Server {

	ServerSocket serverSocket = null;
	Failover failover;
	String ip;
	static int porta;
	static boolean primario;
	boolean ligado = true;
	SocketThread socketThread;
	RMI rmi;
	boolean rmiON;

	public static void main(String[] args) {

		porta = getPort(args);
		String loader = getHost(args);
		primario = checkIfPrimary(args);
		String rmihost = getRMI(args);

		// porta = 5000;
		// String loader = "localhost:9000";
		// boolean primary = true;

		if (loader != null && porta > 0 && rmihost != null) {
			new Server(loader, rmihost);
		}

	}

	public Server(String loader, String rmi) {

		connect(loader);
		RemoteHost rmihost = new RemoteHost(rmi);

		try {
			this.rmi = (RMI) LocateRegistry.getRegistry(rmihost.getHost(), rmihost.getPort()).lookup("registry");
			rmiON = true;
		} catch (NotBoundException e) {
			System.out.println("Not bound");
		} catch (RemoteException e) {

		}

		if (rmiON) {
			failover = new Failover(this, porta);
			failover.start();

			socketThread = new SocketThread(porta, this.rmi);
			socketThread.start();
		}

		Scanner scanIn = new Scanner(System.in);
		while (ligado && rmiON) {

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

	private static String getRMI(String args[]) {
		int length = args.length;

		for (int i = 0; i < length; i++) {
			if (args[i].equals("-rmi")) {
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

	// private String getArgs(String ,String comando){
	// int length = args.length;
	//
	// for (int i = 0; i < length; i++) {
	// if (args[i].equals(comando) || args[i].equals("-l")) {
	// if (++i < length) {
	// return (args[i]);
	// }
	// }
	// }
	// }

}
