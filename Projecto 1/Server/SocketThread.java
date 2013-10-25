package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import RMI.RMI;
import Util.Clientes;
import Util.RemoteHost;

public class SocketThread extends Thread {

	private boolean ligado;
	private ServerSocket serverSocket;
	private int porta;
	private Clientes listaClientes;
	RMI rmi;
	RemoteHost rmihost;
	RMIThread rmiThread;

	public SocketThread(Server server, int porta, RMI rmi, RemoteHost rmihost) {
		ligado = true;
		this.rmi = rmi;
		this.rmihost = rmihost;
		listaClientes = new Clientes();
		rmiThread = new RMIThread(rmi, this);

		try {

			serverSocket = new ServerSocket(porta);

		} catch (IOException e) {
			System.out.println("Erro a criar socket");
			return;
		}
	}

	public void run() {
		HashMap<InetAddress, Integer> clientes = new HashMap<>();
		while (ligado) {

			try {
				Socket clientSocket = serverSocket.accept();

				System.out.println("\nLigado " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

				listaClientes.put(clientSocket.getInetAddress(), clientSocket.getPort());

				ClientThread theClient = new ClientThread(clientSocket, this);
				theClient.start();

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public String listarClientes() {
		return listaClientes.listarClientes();
	}

	public void startRMIfailover() {
		rmiThread = new RMIThread(rmi, this);
		rmiThread.start();
	}

	public class RMIThread extends Thread {

		RMI rmi;
		SocketThread socketThread;

		public RMIThread(RMI rmi, SocketThread socketThread) {
			this.rmi = rmi;
			this.socketThread = socketThread;
		}

		public void run() {
			boolean verf = false;
			while (!verf) {
				try {
					Thread.sleep(1500);
					rmi = (RMI) LocateRegistry.getRegistry(rmihost.getHost(), rmihost.getPort()).lookup("registry");
					verf = true;
					socketThread.rmi = rmi;
					System.out.println("Connected RMI");
				} catch (NotBoundException e) {
					System.out.println("Not bound");
				} catch (RemoteException e) {
					System.out.println("RMI failed");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
