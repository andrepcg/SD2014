package Cliente;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;

import Util.RemoteHost;
import Util.Response;
import Util.User;

public class ClientTCP {

	public RemoteHost server;
	private static ClientSocketThread socketThread;
	private static boolean running = true;
	Scanner sc;

	public static void main(String args[]) throws IOException {

		// String loader = getHost(args);
		String loader = "localhost:9000";

		if (loader.length() > 1) {
			new ClientTCP(new RemoteHost(loader));
		} else
			System.out.println("-loader (-l) nao especificado");

	}

	public ClientTCP(RemoteHost remoteHost) {
		socketThread = new ClientSocketThread(remoteHost);
		socketThread.start();

		Response resposta = null;
		sc = new Scanner(System.in);

		boolean logado = false;
		User utilizador = null;

		String input;

		while (!logado) {

			System.out.println("1. Login\n2. Registar");
			input = sc.nextLine();

			if (input.startsWith("2")) {
				System.out.print("Username: ");
				String username = sc.nextLine();

				while (!username.matches("[a-zA-Z0-9]{4}[a-zA-Z0-9]*")) {
					System.out.print("## minimo de 4 caracteres alfanumericos ##\nUsername: ");
					username = sc.nextLine();
				}

				System.out.print("Password: ");
				String password = sc.nextLine();

				resposta = new Response();
				socketThread.setResposta(resposta);
				// socketThread.registar(username, password);

				if (dados(resposta).contains("TRUE")) {

					System.out.println("## Registado com sucesso ##");
				} else if (resposta.getResposta().contains("FALSE")) {
					System.out.println("## Registado falhado ##");
				}

			} else if (input.startsWith("1")) {
				System.out.print("Username: ");
				String username = sc.nextLine();

				while (!username.matches("[a-zA-Z0-9]{4}[a-zA-Z0-9]*")) {
					System.out.print("## minimo de 4 caracteres alfanumericos ##\nUsername: ");
					username = sc.nextLine();
				}

				System.out.print("Password: ");
				String password = sc.nextLine();

				resposta = new Response();
				socketThread.setResposta(resposta);
				socketThread.login(username, password);

				if (dados(resposta).contains("TRUE")) {
					// utilizador = criarUser(resposta.getResposta());
					// System.out.println(resposta.getResposta());
					utilizador = new User(resposta.getResposta());
					logado = true;
					System.out.println("## Login com sucesso ##");
				} else if (resposta.getResposta().contains("FALSE")) {
					System.out.println("## Login falhado ##");
				}

			}

		}

		String[] topicos = new String[20];
		int topicoActual = 0;

		while (logado) {
			resposta = new Response();
			socketThread.setResposta(resposta);

			imprimirMenuPrincipal();

			boolean v = false;
			int num = 0;
			while (!v) {
				System.out.print(">> ");
				String in = sc.nextLine();
				System.out.print("\n");
				try {
					num = Integer.parseInt(in);
					v = true;
				} catch (NumberFormatException n) {

				}
			}

			if (num == 2) {

				int in = 0;
				boolean ver = false;
				while (!ver) {
					System.out.print("Quantidade: ");
					try {
						in = sc.nextInt();
						ver = true;
					} catch (InputMismatchException e) {

					}
				}

				socketThread.adicionarPacote("HISTORICO_TRANSACCOES|" + utilizador.getId() + "|" + in);
				imprimirTransaccoes(dados(resposta));

			} else if (num == 3) {

				socketThread.adicionarPacote("USER_IDEIAS|" + utilizador.getId());
				imprimirIdeias(dados(resposta));

			} else if (num == 4) {

				socketThread.adicionarPacote("SHARES|" + utilizador.getId());
				imprimirShares(dados(resposta));

			} else if (num == 1) {

				socketThread.adicionarPacote("LISTARTOPICOS");
				int numTopicos = imprimirTopicos(dados(resposta), topicos);
				v = false;
				int d = 0;
				while (!v) {
					System.out.print(">> ");
					String in = sc.nextLine();
					try {
						d = Integer.parseInt(in);
						if (d > 0 && d <= numTopicos) {
							v = true;
							topicoActual = d;
						}
					} catch (NumberFormatException n) {

					}
				}

				resposta.setResposta(null);

				socketThread.adicionarPacote("TOPICO_IDEIAS|" + d);
				System.out.println("\n[ TOPICO ] " + topicos[topicoActual - 1]);
				imprimirIdeias(dados(resposta));

				System.out.println("[I]nserir Ideia - [C]omprar Ideia");
				System.out.print(">> ");
				String in = sc.nextLine();
				menuTopico(in, topicoActual, utilizador);
			}
		}

	}

	private void imprimirShares(String dados) {
		// SHARES|idShare;idIdeia;numShares;texto
		if (dados != null) {

			if (dados.startsWith("SHARES|0")) {
				System.out.println("Sem shares\n");
			} else {
				String[] split = dados.split("\\|");

				for (int i = 1; i < split.length; i++) {
					String[] t = split[i].split(";");
					System.out.println(t[0] + ". [ " + t[2] + " shares ] " + encurtarIdeia(t[3], 40));
				}
			}
		}
	}

	private void menuTopico(String comando, int topicoActual, User utilizador) {

		if (comando.equals("I") || comando.equals("i"))
			criarIdeia(topicoActual, utilizador);
		else if (comando.equals("C") || comando.equals("c"))
			comprarIdeia();

	}

	private void comprarIdeia() {
		boolean verf = false;
		int id = 0;
		while (!verf) {
			System.out.print("\nID Ideia >> ");
			try {
				id = Integer.parseInt(sc.nextLine());
				verf = true;
			} catch (Exception e) {

			}
		}

		String compra = "COMPRAR_IDEIA|" + id;
		socketThread.adicionarPacote(compra);
	}

	private void criarIdeia(int topico, User user) {
		System.out.print("\nIdeia >> ");
		String texto = sc.nextLine();

		boolean verf = false;
		double preco = 0;
		while (!verf) {
			System.out.print("\nPreco por share >> ");
			try {
				preco = Double.parseDouble(sc.nextLine());
				verf = true;
			} catch (Exception e) {

			}
		}

		String ideia = "CRIAR_IDEIA|" + user.getId() + "|" + topico + "|" + texto + "|" + preco + "|" + getTime();

		socketThread.adicionarPacote(ideia);
	}

	private void imprimirIdeias(String dados) {
		// TOPICOS_IDEIAS|id;username;texto;timestamp
		if (dados != null) {

			if (dados.startsWith("TOPICO_IDEIAS|0")) {
				System.out.println("Sem ideias\n");
			} else {

				String[] split = dados.split("\\|");

				for (int i = 1; i < split.length; i++) {
					String[] t = split[i].split(";");
					System.out.println(t[0] + ". [" + t[1] + "] " + encurtarIdeia(t[2], 40) + " (" + t[3] + ")");
				}
				System.out.print("\n");
			}
		}
	}

	private String encurtarIdeia(String ideia, int numCars) {
		return (String) (ideia.length() >= numCars ? ideia.subSequence(0, numCars) : ideia) + "...";
	}

	private void imprimirTransaccoes(String dados) {
		// HISTORICOTRANSACCOES|idIdeia;numShares;preco_por_share;pago;tipo;timestamp
		if (dados != null) {

			if (dados.startsWith("HISTORICO_TRANSACCOES|0")) {
				System.out.println("Sem transaccoes\n");
			} else {
				String[] split = dados.split("\\|");

				for (int i = 1; i < split.length; i++) {
					String[] t = split[i].split(";");
					System.out.println("[" + t[4] + "] Ideia: " + t[0] + " | " + t[1] + " shares - " + t[2] + " /share - Total pago: " + t[3] + " (" + t[5] + ")");
				}
			}
		}
	}

	private int imprimirTopicos(String dados, String[] topicos) {
		if (dados != null) {
			String[] split = dados.split("\\|");

			for (int i = 1; i < split.length; i++) {
				String[] t = split[i].split(";");
				String nome = t[1].replace("\\", "");
				topicos[i - 1] = nome;
				System.out.println(t[0] + ". " + nome);
			}
			return split.length;
		}
		return 0;

	}

	private void imprimirMenuPrincipal() {
		System.out.println("\n## MENU ##");
		System.out.println("1. Ver Topicos");
		// System.out.println("1. Ver Ideias");
		// System.out.println("2. Criar Ideia");
		// System.out.println("3. Criar Topico");
		System.out.println("2. Historico de Transaccoes");
		System.out.println("3. Minhas ideias");
		System.out.println("4. Carteira de Shares");
		// System.out.println("5. Listar Topicos");
		// System.out.println("5. ");
	}

	private User criarUser(String dados) {
		// LOGIN|TRUE|id|username|deicoins
		String[] split = dados.split("\\|");

		return new User(Integer.parseInt(split[2]), split[3], Double.parseDouble(split[4]));
	}

	private String dados(Response resposta) {
		synchronized (resposta) {
			while (resposta.isNull()) {
				try {
					resposta.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		return resposta.getResposta();
	}

	private void imprimirDados(String string, boolean indices) {

		if (string != null) {
			String[] split = string.split("\\|");

			for (int i = 1; i < split.length; i++)
				System.out.println((indices ? i + ". " : "") + split[i]);
		}
	}

	private String getTime() {
		SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		return date_format.format(cal.getTime());
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
}