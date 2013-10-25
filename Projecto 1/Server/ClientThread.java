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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;

import RMI.RMI;
import Util.Ideia;
import Util.Mercado;
import Util.RemoteHost;
import Util.Share;
import Util.Topico;
import Util.Transaccao;
import Util.User;

class ClientThread extends Thread {
	private Socket clientSocket;
	private DataOutputStream os;
	private DataInputStream is;
	private RMI rmi;
	RemoteHost rmihost;
	SocketThread s;

	private ObjectOutputStream oos;
	private InputStream in;

	public ClientThread(Socket clientSocket, SocketThread s) {
		this.clientSocket = clientSocket;
		this.rmi = s.rmi;
		this.s = s;
		try {
			is = new DataInputStream(clientSocket.getInputStream());
			os = new DataOutputStream(clientSocket.getOutputStream());
			in = clientSocket.getInputStream();
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			// ois = new ObjectInputStream(clientSocket.getInputStream());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		String input = "";
		while (!clientSocket.isClosed()) {
			rmi = s.rmi;
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

			login2(dadosRegistoLogin(input));

		} else if (input.startsWith("LISTARTOPICOS")) {

			listarTopicos();

		} else if (input.startsWith("HISTORICO_TRANSACCOES")) {

			historicoTransaccoes(input);

		} else if (input.startsWith("TOPICO_IDEIAS|")) {
			listarIdeiasTopico(input);

		} else if (input.startsWith("USER_IDEIAS|")) {
			listarIdeiasUser(input);

		} else if (input.startsWith("CRIAR_IDEIA|")) {
			criarIdeia(input);

		} else if (input.startsWith("SHARES|")) {
			seleccionarShares(input);

		} else if (input.startsWith("IDEIA|")) {
			seleccionarIdeia(input);

		} else if (input.startsWith("MERCADO|")) {
			mercadoIdeia(input);

		} else if (input.startsWith("CRIAR_TOPICO|")) {
			criarTopico(input);

		} else if (input.startsWith("ORDEM_COMPRA|")) {
			criarOrdem(input);

		} else if (input.startsWith("ORDEM_VENDA|")) {
			criarOrdem(input);

		} else if (input.startsWith("LISTAR_ORDENS|")) {
			listarOrdens(input);

		} else if (input.startsWith("APAGAR_IDEIA|")) {
			apagarIdeia(input);

		} else if (input.startsWith("GET_UTILIZADOR|")) {
			getUser(input);

		} else if (input.startsWith("REMOVER_ORDEM|")) {
			removerOrdem(input);

		}

	}

	private void removerOrdem(String input) {
		try {
			String[] split = input.split("\\|");

			// REMOVER_ORDEM|idUser|tipo|idOrdem
			boolean r = rmi.removerOrdem(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
			// enviarObjecto(m);
		} catch (RemoteException e) {
			s.startRMIfailover();
		}

	}

	private void getUser(String input) {
		// TODO Auto-generated method stub
		try {
			String[] split = input.split("\\|");
			User m = rmi.getUser(Integer.parseInt(split[1]));
			enviarObjecto(m);
		} catch (RemoteException e) {
			s.startRMIfailover();
		}
	}

	private void apagarIdeia(String input) {
		try {
			String[] split = input.split("\\|");
			boolean m = rmi.apagarIdeia(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			enviarString("APAGAR_IDEIA|" + m);
		} catch (RemoteException e) {
			s.startRMIfailover();
		}

	}

	private void listarOrdens(String input) {
		String[] split = input.split("\\|");

		// LISTAR_ORDENS|idUser|idIdeia

		try {
			Mercado m = rmi.ordensUser(Integer.parseInt(split[1]), Integer.parseInt(split[2]));
			enviarObjecto(m);
		} catch (RemoteException e) {

			s.startRMIfailover();
		}

	}

	private void criarOrdem(String input) {
		// ORDEM_COMPRA|idIdeia;idUser;numShares;preco_por_share;precoTotal;timestamp
		try {
			String[] split = input.split("\\|");

			String[] ordem = split[1].split(";");
			int tipo = (split[0].compareTo("ORDEM_COMPRA") == 0) ? 0 : 1;

			int r = rmi.criarOrdem(tipo, Integer.parseInt(ordem[0]), Integer.parseInt(ordem[1]), Integer.parseInt(ordem[2]), Double.parseDouble(ordem[3]), Double.parseDouble(ordem[4]), ordem[5]);

		} catch (RemoteException e) {

			s.startRMIfailover();
		}

	}

	private void criarTopico(String input) {
		String[] split = input.split("\\|");
		try {
			rmi.criarTopico(split[1]);
		} catch (RemoteException e) {
			s.startRMIfailover();
		}

	}

	private void mercadoIdeia(String input) {
		String[] split = input.split("\\|");

		try {
			Mercado mercado = rmi.mercadoShares(Integer.parseInt(split[1]));

			String ordensCompra = "ORDENSCOMPRA|" + mercado.getOrdensCompraString();
			String ordensVenda = "ORDENSVENDA|" + mercado.getOrdensVendaString();

			String concat = ordensCompra + "<->" + ordensVenda;
			enviarString(concat);

		} catch (RemoteException e) {
			s.startRMIfailover();
		}

	}

	private void seleccionarIdeia(String input) {
		String[] split = input.split("\\|");

		try {
			ArrayList<Ideia> ideias = rmi.seleccionarIdeia(Integer.parseInt(split[1]));

			enviarObjecto(ideias);
		} catch (RemoteException e) {
			s.startRMIfailover();
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
					lista += s.getId() + ";" + s.getIdIdeia() + ";" + s.getNum_shares() + ";" + s.getIdeia() + "|";

				lista = lista.substring(0, lista.length() - 1);

				String d = "SHARES|" + lista;
				enviarString(d);
			} else {

				String d = "SHARES|0";
				enviarString(d);
			}

		} catch (RemoteException e) {
			s.startRMIfailover();
		}
	}

	private void criarIdeia(String input) {
		String[] split = input.split("\\|");
		// iduser|topico|texto|preco|time|idMae;null|file

		try {
			String[] resp = split[6].split(";");
			int id = rmi.criarIdeia(Integer.parseInt(split[1]), Integer.parseInt(split[2]), split[3], Double.parseDouble(split[4]), split[5], Integer.parseInt(resp[0]), Integer.parseInt(resp[1]), resp[2]);
			// System.out.println("Ideia inserida id: " + id);
			if (id > 0) {
				if (split[7].compareToIgnoreCase("true") == 0) {
					enviarString("RECEBER_FILE|CHECK");
					String path = receberFicheiro(input, Long.parseLong(split[8]), split[9]);
					if (path != null)
						rmi.inserirFicheiro(id, path);
				}
			}

			// sendCheck();

		} catch (RemoteException e) {
			s.startRMIfailover();
		}
	}

	private void listarTopicos() {

		try {
			ArrayList<Topico> topicos = rmi.mostraTopicos();

			String lista = "";

			for (Topico t : topicos)
				lista += t.getId() + ";" + t.getNome() + "|";

			lista = lista.substring(0, lista.length() - 1);

			String d = "TOPICOS|" + lista;
			enviarString(d);

		} catch (RemoteException e) {
			s.startRMIfailover();
		}

	}

	private void listarIdeiasUser(String input) {

		try {
			String[] split = input.split("\\|");
			ArrayList<Ideia> ideias = rmi.mostraIdeias(0, Integer.parseInt(split[1]));

			String d = ideiasFunc(ideias, "USER_IDEIAS");

			enviarString(d);

		} catch (RemoteException e) {
			s.startRMIfailover();
		}
	}

	private void listarIdeiasTopico(String input) {

		try {
			String[] split = input.split("\\|");
			ArrayList<Ideia> ideias = rmi.mostraIdeias(Integer.parseInt(split[1]), 0);

			String d = ideiasFunc(ideias, "TOPICO_IDEIAS");

			enviarString(d);

		} catch (RemoteException e) {
			s.startRMIfailover();
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
					lista += t.getIdIdeia() + ";" + t.getNumShares() + ";" + t.getPreco() + ";" + t.getPago() + ";" + t.getTipo() + ";" + t.getTimestamp() + "|";
				}

				lista = lista.substring(0, lista.length() - 1);

				String d = "HISTORICO_TRANSACCOES|" + lista;
				enviarString(d);
			} else {

				String d = "HISTORICO_TRANSACCOES|0";
				enviarString(d);
			}

		} catch (RemoteException e) {
			s.startRMIfailover();
		}
	}

	private String ideiasFunc(ArrayList<Ideia> ideias, String comando) {
		if (ideias.size() > 0) {
			String lista = "";

			for (Ideia t : ideias)
				lista += t.getIdIdeia() + ";" + t.getUsername() + ";" + t.getTexto() + ";" + t.getData() + "|";

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

	private void sendCheck() {
		enviarString("CHECK");
	}

	private String receberFicheiro(String input, long l, String extensao) {
		byte[] mybytearray = new byte[(int) l];

		long nome = System.currentTimeMillis();

		try {
			String dir = System.getProperty("user.dir") + "\\ficheiros\\" + nome + (extensao != null ? "." + extensao : "");
			FileOutputStream fos = new FileOutputStream(dir);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int bytesRead = in.read(mybytearray, 0, mybytearray.length);
			bos.write(mybytearray, 0, bytesRead);
			bos.close();
			System.out.println("Ficheiro recebido! " + dir);

			return dir;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	private boolean registar(String[] dadosRegisto) {
		String username = dadosRegisto[0];
		String password = dadosRegisto[1];

		// TODO acede RMI e regista user. registo com sucesso = true
		boolean verf = false;

		try {
			verf = rmi.registo(username, password);

		} catch (RemoteException e) {
			s.startRMIfailover();
		}

		enviarString("REGISTO|" + (verf ? "TRUE" : "FALSE"));

		return false;
	}

	private boolean login(String[] dadosLogin) {
		String username = dadosLogin[0];
		String password = dadosLogin[1];

		User verf = null;

		try {
			verf = rmi.login(username, password);
		} catch (RemoteException e) {
			s.startRMIfailover();
		}

		enviarString("LOGIN|" + (verf != null ? ("TRUE" + "|" + verf.getId() + "|" + verf.getUsername() + "|" + verf.getDeicoins()) : "FALSE"));

		return false;
	}

	private boolean login2(String[] dadosLogin) {
		String username = dadosLogin[0];
		String password = dadosLogin[1];

		User verf = null;

		try {
			verf = rmi.login(username, password);
			if (verf == null)
				verf = new User(false);
			enviarObjecto(verf);
		} catch (RemoteException e) {
			// e.printStackTrace();
			// ligarRMI();
			// e.printStackTrace();

			s.startRMIfailover();

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
		// os.writeUTF("heartbeat");
		enviarString("heartbeat");
	}

	private void enviarString(String dados) {
		try {
			oos.writeObject(dados);
		} catch (IOException r) {

			fecharSocket();
		}
	}

	private void enviarObjecto(Object o) {
		try {
			oos.writeObject(o);
		} catch (IOException e) {

			e.printStackTrace();
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

	public RMI ligarRMI() {
		try {
			this.rmi = (RMI) LocateRegistry.getRegistry(rmihost.getHost(), rmihost.getPort()).lookup("registry");

			return rmi;
		} catch (NotBoundException e) {
			System.out.println("Not bound");
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return null;
	}

}