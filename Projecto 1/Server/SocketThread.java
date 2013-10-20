package Server;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import RMI.RMI;
import Util.Clientes;

public class SocketThread extends Thread {

	private boolean ligado;
	private ServerSocket serverSocket;
	private int porta;
	private Clientes listaClientes;
	private RMI rmi;

	public SocketThread(int porta, RMI rmi) {
		ligado = true;
		this.rmi = rmi;
		listaClientes = new Clientes();

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

				ClientThread theClient = new ClientThread(clientSocket, rmi);
				theClient.start();

			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public String listarClientes() {
		return listaClientes.listarClientes();
	}

}
