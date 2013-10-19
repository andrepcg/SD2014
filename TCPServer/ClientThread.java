import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

class ClientThread extends Thread {
	private Socket clientSocket;
	private DataOutputStream os;
	private DataInputStream is;

	private ObjectOutputStream oos;

	// private ObjectInputStream ois;

	public ClientThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// ois = new ObjectInputStream(clientSocket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		String input = "";

		while (!clientSocket.isClosed()) {

			try {
				input = is.readUTF();

			} catch (SocketException e1) {
				fecharSocket();
			} catch (EOFException e2) {
				fecharSocket();
			} catch (IOException e1) {

				e1.printStackTrace();
			}

			comandos(input);

			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}

		System.out.println("Fechou");

	}

	private void comandos(String input) {

		System.out.println(input);
		if (input.contains("heartbeat"))
			heartbeat();

		else if (input.matches("\\bREGISTAR\\|((\\w{4})\\w*)\\|([a-zA-Z0-9]{32})\\b")) {

			registar(dadosRegistoLogin(input));
		} else if (input.matches("\\bLOGIN\\|((\\w{4})\\w*)\\|([a-zA-Z0-9]{32})\\b")) {

			login(dadosRegistoLogin(input));
		} else if (input.matches("\\bLOGIN\\|((\\w{4})\\w*)\\|([a-zA-Z0-9]{32})\\b")) {

			login(dadosRegistoLogin(input));
		} else if (input.matches("\\bLISTARTOPICOS\\b")) {

			listarTopicos();
		}
	}

	private void listarTopicos() {
		String d = "TOPICOS|politica|ciencia|medicina";
		enviarString(d);
	}

	private boolean registar(String[] dadosRegisto) {
		String username = dadosRegisto[0];
		String password = dadosRegisto[1];

		// TODO acede RMI e regista user. registo com sucesso = true

		try {
			Thread.sleep(150);
			os.writeUTF("REGISTO|true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean login(String[] dadosLogin) {
		String username = dadosLogin[0];
		String password = dadosLogin[1];

		// TODO acede RMI e faz login ao user. login com sucesso = true

		try {
			Thread.sleep(150);
			os.writeUTF("LOGIN|true");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return false;
	}

	private String[] dadosRegistoLogin(String input) {
		String[] x = new String[2];
		String[] split = input.split("|");
		x[0] = split[1];
		x[1] = split[2];
		return x;
	}

	private void heartbeat() {
		try {
			// Thread.sleep(150);
			os.writeUTF("heartbeat");
			// System.out.println("enviar heartbeat");
		} catch (IOException r) {

			fecharSocket();
		}
	}

	private void enviarString(String dados) {
		try {
			// Thread.sleep(150);
			os.writeUTF(dados);
		} catch (IOException r) {

			fecharSocket();
		}
	}

	private void fecharSocket() {
		try {
			os.close();
			is.close();
			clientSocket.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}