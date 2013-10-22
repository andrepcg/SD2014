package Cliente;

import java.io.File;
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
	Response resposta;

	public static void main(String args[]) throws IOException {

		// String loader = getHost(args);
		String loader = "localhost:9000";

		// System.out.println("\t[ ORDENS DE COMPRA ]\t\t\t[ ORDENS DE VENDA ]");
		// System.out.println("Soma\tShares\tPreco\t\t\t\tPreco\tShares\tSoma");

		if (loader.length() > 1) {
			new ClientTCP(new RemoteHost(loader));
		} else
			System.out.println("-loader (-l) nao especificado");

	}

	public ClientTCP(RemoteHost remoteHost) {
		socketThread = new ClientSocketThread(remoteHost);
		socketThread.start();

		// synchronized (socketThread.getServer()) {
		// while (socketThread.getServer() == null)
		try {
			// socketThread.getServer().wait();
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		// }

		if (socketThread.getServer() != null) {

			resposta = null;
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
					System.out.println("\n[C]riar topico");

					v = false;
					int d = 0;
					while (!v) {
						System.out.print(">> ");
						String in = sc.nextLine();

						if (in.compareToIgnoreCase("C") == 0) {
							criarTopico(topicos);
						} else if (in.matches("\\b(\\d)+\\b")) {
							try {
								d = Integer.parseInt(in);
								if (d > 0 && d <= numTopicos) {
									v = true;
									topicoActual = d;
								}
							} catch (NumberFormatException n) {

							}
						} else
							v = true;
					}

					if (d != 0) {

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

		} else {
			socketThread.setRunning(false);
			System.out.println("nao ligado loader");
		}

	}

	private void criarTopico(String[] topicos) {
		boolean verf = false;

		String in = "";
		while (!verf) {
			System.out.print("\nNome >> ");
			in = sc.nextLine();
			verf = true;
			for (int i = 0; i < topicos.length; i++) {
				if (topicos[i] != null && topicos[i].compareToIgnoreCase(in) == 0)
					verf = false;
			}
		}

		socketThread.adicionarPacote("CRIAR_TOPICO|" + in);

		// TODO enviar topico tcp e rmi

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
					System.out.println(t[1] + ". [ " + t[2] + " shares ] [ Cotacao ] " + encurtarIdeia(t[3], 40));
				}

				System.out.print("\n>> ");
				String in = sc.nextLine();

				if (in.matches("\\b(\\d)+\\b")) {
					resposta.setResposta(null);
					socketThread.adicionarPacote("MERCADO|" + in);
					String x = dados(resposta);
					imprimirOrdens(x);
					// dentro de uma share
					//
					// [ORDENS COMPRA]
					// num_shares_somadas num_shares preco_por_share
					// ###################### ############## ###################
					// 1000 1000 120
					// 1500 500 119
					// 2500 1000 118

					// [ORDENS VENDA]
					// num_shares_somadas num_shares preco_por_share
					// ###################### ############## ###################
					// 500 500 121
					// 1000 500 130
					// 2000 1000 135
				}
			}
		}
	}

	private void imprimirOrdens(String input) {
		String[] split = input.split("<->");

		String[] ordensCompra = split[0].replace("ORDENSCOMPRA|", "").replace("null", "").split("\\|");
		String[] ordensVenda = split[1].replace("ORDENSVENDA|", "").replace("null", "").split("\\|");

		System.out.println("\t[ ORDENS DE COMPRA ]\t\t\t[ ORDENS DE VENDA ]");
		System.out.println("\tSoma\tShares\tPreco\t\t\tPreco\tShares\tSoma");

		// idideia;idUse;num_shares;preco_por_share;timestamp;

		int somaCompra = 0;
		int somaVenda = 0;

		for (int i = 0; i < (ordensCompra.length > ordensVenda.length ? ordensCompra.length : ordensVenda.length); i++) {
			String oCompra = "";
			if (i < ordensCompra.length && ordensCompra[i].length() > 1) {
				String[] s = ordensCompra[i].split(";");
				somaCompra += Integer.parseInt(s[2]);
				oCompra += somaCompra + "\t" + s[2] + "\t" + s[3] + "\t";
			}

			String oVenda = "";
			if (i < ordensVenda.length && ordensVenda[i].length() > 1) {
				String[] s = ordensVenda[i].split(";");
				somaVenda += Integer.parseInt(s[2]);
				oVenda += s[3] + "\t" + s[2] + "\t" + somaVenda;
			}

			if (oCompra.length() < 2)
				oCompra = "\t\t\t";

			System.out.println("\t" + oCompra + "\t\t" + oVenda);
		}

	}

	private void menuTopico(String comando, int topicoActual, User utilizador) {

		if (comando.equals("I") || comando.equals("i"))
			criarIdeia(topicoActual, utilizador);
		else if (comando.equals("C") || comando.equals("c"))
			comprarIdeia();
		else if (comando.matches("\\b\\(\\d)+\\b")) {
			menuIdeia(comando);
		}

	}

	private void menuIdeia(String comando) {
		socketThread.adicionarPacote("IDEIA|" + comando);
		resposta.setResposta(null);
		String dados = dados(resposta);
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

		System.out.print("\nAnexar ficheiro? [S]im ou [N]ao >> ");
		String files = sc.nextLine();
		boolean file = (files.compareToIgnoreCase("S") == 0) ? true : false;

		if (file) {
			socketThread.setLigado(false);
			System.out.print("\nPath >> ");
			String path = sc.nextLine();

			String ideia = "CRIAR_IDEIA|" + user.getId() + "|" + topico + "|" + texto + "|" + preco + "|" + getTime() + "|" + file + "|" + getFileSize(path) + "|" + getExtensao(path);
			socketThread.adicionarPacote(ideia);
			try {
				Thread.currentThread().sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			socketThread.enviarFicheiro(path);
			socketThread.setLigado(true);
		} else {
			String ideia = "CRIAR_IDEIA|" + user.getId() + "|" + topico + "|" + texto + "|" + preco + "|" + getTime() + "|" + file;
			socketThread.adicionarPacote(ideia);
		}

	}

	private String getExtensao(String file) {
		if (file.indexOf(".") > 0) {
			String[] split = file.split("\\.");
			return split[1];
		}
		return null;

	}

	private long getFileSize(String path) {
		File file = new File(path);
		return file.length();
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

				topicos[i - 1] = t[1];
				System.out.println(t[0] + ". " + t[1]);
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