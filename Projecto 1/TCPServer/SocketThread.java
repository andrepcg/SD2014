import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class SocketThread extends Thread {

	private boolean ligado;
	private ServerSocket serverSocket;
	private int porta;
	private Clientes listaClientes;

	public SocketThread(Server server, int porta) {
		ligado = true;
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

				ClientThread theClient = new ClientThread(clientSocket);
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
