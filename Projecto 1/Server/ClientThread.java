package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import RMI.RMI;
import Util.Topico;
import Util.Transaccao;
import Util.User;

class ClientThread extends Thread {
	private Socket clientSocket;
	private DataOutputStream os;
	private DataInputStream is;
	private RMI rmi;

	private ObjectOutputStream oos;

	// private ObjectInputStream ois;

	public ClientThread(Socket clientSocket, RMI rmi) {
		this.clientSocket = clientSocket;
		this.rmi = rmi;
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

		// System.out.println(input);
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
		} else if (input.matches("\\bHISTORICOTRANSACCOES\\b")) {

			historicoTransaccoes(input);
		}
	}

	private void listarTopicos() {

		try {
			ArrayList<Topico> topicos = rmi.mostraTopicos();

			String lista = "";

			for (Topico t : topicos) {
				lista += t.getId() + ";" + t.getNome() + "\\|";
			}

			lista = lista.substring(0, lista.length() - 1);

			String d = "TOPICOS|" + lista;
			enviarString(d);

		} catch (RemoteException e) {

		}

	}

	private void historicoTransaccoes(String input) {
		String[] split = input.split("\\|");

		ArrayList<Transaccao> ts = new ArrayList<>();
		try {
			ts = rmi.historicoTransaccoes(Integer.parseInt(split[1]), Integer.parseInt(split[2]));

			String lista = "";

			// HISTORICOTRANSACCOES|idIdeia;numShares;preco;pago;tipo;timestamp
			for (Transaccao t : ts) {
				lista += t.getIdIdeia() + ";" + t.getNumShares() + ";" + t.getPreco() + ";" + t.getPago() + ";" + t.getTipo() + ";" + t.getTimestamp() + "\\|";
			}

			lista = lista.substring(0, lista.length() - 1);

			String d = "HISTORICOTRANSACCOES|" + lista;
			enviarString(d);

		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}
	}

	private boolean registar(String[] dadosRegisto) {
		String username = dadosRegisto[0];
		String password = dadosRegisto[1];

		// TODO acede RMI e regista user. registo com sucesso = true
		boolean verf = false;

		try {
			verf = rmi.registo(username, password);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			os.writeUTF("REGISTO|" + (verf ? "TRUE" : "FALSE"));
		} catch (IOException e) {

			e.printStackTrace();
		}

		return false;
	}

	private boolean login(String[] dadosLogin) {
		String username = dadosLogin[0];
		String password = dadosLogin[1];

		// TODO acede RMI e faz login ao user. login com sucesso = true

		User verf = null;

		try {
			verf = rmi.login(username, password);
			System.out.println("Login verf= " + verf);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			os.writeUTF("LOGIN|" + (verf != null ? ("TRUE" + "|" + verf.getId() + "|" + verf.getUsername() + "|" + verf.getDeicoins()) : "FALSE"));
		} catch (IOException e) {

			e.printStackTrace();
		}

		return false;
	}

	private String[] dadosRegistoLogin(String input) {
		String[] x = new String[2];
		String[] split = input.split("\\|");
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