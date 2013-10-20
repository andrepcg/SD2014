package Cliente;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import Util.RemoteHost;
import Util.Response;
import Util.User;

public class ClientTCP {

	public RemoteHost server;
	private static ClientSocketThread socketThread;
	private static boolean running = true;

	public static void main(String args[]) throws IOException {

		String loader = getHost(args);
		// String loader = "192.168.1.81:9000";

		if (loader.length() > 1) {
			new ClientTCP(new RemoteHost(loader));
		} else
			System.out.println("-loader (-l) nao especificado");

	}

	public ClientTCP(RemoteHost remoteHost) {
		socketThread = new ClientSocketThread(this, remoteHost);
		socketThread.start();

		Response resposta = null;
		Scanner sc = new Scanner(System.in);

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
				socketThread.registar(username, password);

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
					utilizador = criarUser(resposta.getResposta());
					logado = true;
					System.out.println("## Login com sucesso ##");
				} else if (resposta.getResposta().contains("FALSE")) {
					System.out.println("## Login falhado ##");
				}

			}

		}

		while (logado) {
			resposta = new Response();
			socketThread.setResposta(resposta);

			imprimirMenuPrincipal();
			input = sc.nextLine();

			if (input.startsWith("1")) {

				socketThread.adicionarPacote("IDEIAS|0");
				imprimirDados(dados(resposta), true);

			} else if (input.startsWith("2")) {

				// escrever ideia
				// menu seleccionar topico(s)

			} else if (input.startsWith("3")) {

				// mostrar topicos
				// escrever topico

			} else if (input.startsWith("4")) {

				int in = 0;
				boolean ver = false;
				while (!ver) {
					System.out.println("Quantidade: ");
					try {
						in = sc.nextInt();
						ver = true;
					} catch (InputMismatchException e) {

					}
				}

				socketThread.adicionarPacote("HISTORICOTRANSACCOES|" + utilizador.getId() + "|" + in);
				imprimirTransaccoes(dados(resposta));

			} else if (input.startsWith("5")) {

				socketThread.adicionarPacote("IDEIAS|" + utilizador.getId());
				imprimirDados(dados(resposta), true);

			} else if (input.startsWith("6")) {

				socketThread.adicionarPacote("SHARES|" + utilizador.getId());
				imprimirDados(dados(resposta), true);

			} else if (input.startsWith("7")) {

				socketThread.adicionarPacote("LISTARTOPICOS");
				imprimirTopicos(dados(resposta));

			}
		}

	}

	private void imprimirTransaccoes(String dados) {
		// HISTORICOTRANSACCOES|idIdeia;numShares;preco_por_share;pago;tipo;timestamp
		if (dados != null) {
			String[] split = dados.split("\\|");

			for (int i = 1; i < split.length; i++) {
				String[] t = split[i].split(";");
				System.out.println(t[0] + ". [" + t[4] + "] " + t[1] + " shares - " + t[2] + " /share - Total pago: " + t[3] + " (" + t[5] + ")");
			}
		}
	}

	private void imprimirTopicos(String dados) {
		if (dados != null) {
			String[] split = dados.split("\\|");

			for (int i = 1; i < split.length; i++) {
				String[] t = split[i].split(";");
				System.out.println(t[0] + ". " + t[1]);
			}
		}

	}

	private void imprimirMenuPrincipal() {
		System.out.println("\n## MENU ##");
		System.out.println("1. Ver Ideias");
		System.out.println("2. Criar Ideia");
		System.out.println("3. Criar Topico");
		System.out.println("4. Historico de Transaccoes");
		System.out.println("5. Minhas ideias");
		System.out.println("6. Carteira de Shares");
		System.out.println("7. Listar Topicos");
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