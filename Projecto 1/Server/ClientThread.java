package Server;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import RMI.RMI;
import Util.Ideia;
import Util.Share;
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
	private InputStream in;

	public ClientThread(Socket clientSocket, RMI rmi) {
		this.clientSocket = clientSocket;
		this.rmi = rmi;
		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());
			in = clientSocket.getInputStream();
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

		} else if (input.startsWith("LISTARTOPICOS")) {

			listarTopicos();

		} else if (input.startsWith("HISTORICO_TRANSACCOES")) {

			historicoTransaccoes(input);

		} else if (input.startsWith("RECEBER_FICHEIRO|")) {
			String[] split = input.split("\\|");
			receberFicheiro(split[1]);

		} else if (input.startsWith("TOPICO_IDEIAS|")) {
			listarIdeiasTopico(input);

		} else if (input.startsWith("USER_IDEIAS|")) {
			listarIdeiasUser(input);

		} else if (input.startsWith("CRIAR_IDEIA|")) {
			criarIdeia(input);

		} else if (input.startsWith("SHARES|")) {
			seleccionarShares(input);
		}
	}

	private void seleccionarShares(String input) {
		String[] split = input.split("\\|");

		ArrayList<Share> shares = new ArrayList<>();
		try {
			shares = rmi.seleccionarShares(Integer.parseInt(split[1]));

			if (shares.size() > 0) {
				String lista = "";

				// SHARES|idShare;idIdeia;numShares;texto
				for (Share s : shares)
					lista += s.getId() + ";" + s.getIdeia() + ";" + s.getNum_shares() + ";" + s.getIdeia() + "\\|";

				lista = lista.substring(0, lista.length() - 1);

				String d = "SHARES|" + lista;
				enviarString(d);
			} else {

				String d = "SHARES|0";
				enviarString(d);
			}

		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}
	}

	private void criarIdeia(String input) {
		String[] split = input.split("\\|");
		// iduser;topico;texto;preco;time
		try {
			rmi.criarIdeia(Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3], Double.parseDouble(split[4]), split[5]);
		} catch (NumberFormatException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void listarTopicos() {

		try {
			ArrayList<Topico> topicos = rmi.mostraTopicos();

			String lista = "";

			for (Topico t : topicos)
				lista += t.getId() + ";" + t.getNome() + "\\|";

			lista = lista.substring(0, lista.length() - 1);

			String d = "TOPICOS|" + lista;
			enviarString(d);

		} catch (RemoteException e) {

		}

	}

	private void listarIdeiasUser(String input) {

		try {
			String[] split = input.split("\\|");
			ArrayList<Ideia> ideias = rmi.mostraIdeias(0, Integer.parseInt(split[1]));

			String d = ideiasFunc(ideias, "USER_IDEIAS");

			enviarString(d);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void listarIdeiasTopico(String input) {

		try {
			String[] split = input.split("\\|");
			ArrayList<Ideia> ideias = rmi.mostraIdeias(Integer.parseInt(split[1]), 0);

			String d = ideiasFunc(ideias, "TOPICOS_IDEIAS");

			enviarString(d);

		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void historicoTransaccoes(String input) {
		String[] split = input.split("\\|");

		ArrayList<Transaccao> ts = new ArrayList<>();
		try {
			ts = rmi.historicoTransaccoes(Integer.parseInt(split[1]), Integer.parseInt(split[2]));

			if (ts.size() > 0) {
				String lista = "";

				// HISTORICOTRANSACCOES|idIdeia;numShares;preco;pago;tipo;timestamp
				for (Transaccao t : ts) {
					lista += t.getIdIdeia() + ";" + t.getNumShares() + ";" + t.getPreco() + ";" + t.getPago() + ";" + t.getTipo() + ";" + t.getTimestamp() + "\\|";
				}

				lista = lista.substring(0, lista.length() - 1);

				String d = "HISTORICO_TRANSACCOES|" + lista;
				enviarString(d);
			} else {

				String d = "HISTORICO_TRANSACCOES|0";
				enviarString(d);
			}

		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}
	}

	private String ideiasFunc(ArrayList<Ideia> ideias, String comando) {
		if (ideias.size() > 0) {
			String lista = "";

			for (Ideia t : ideias)
				lista += t.getIdIdeia() + ";" + t.getUsername() + ";" + t.getTexto() + ";" + t.getData() + "\\|";

			if (lista.length() > 0)
				lista = lista.substring(0, lista.length() - 1);

			// USER_IDEIAS|id;username;texto;timestamp
			String d = comando + "|" + lista;
			return d;

		} else {

			String d = comando + "|0";
			return d;

		}
	}

	private void receberFicheiro(String nome) {
		byte[] mybytearray = new byte[1024];

		try {
			FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "\\ficheiros\\" + nome);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = in.read(mybytearray, 0, mybytearray.length);
			bos.write(mybytearray, 0, bytesRead);
			bos.close();
			System.out.println("Ficheiro recebido! " + System.getProperty("user.dir") + "\\ficheiros\\" + nome);
		} catch (IOException e) {
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