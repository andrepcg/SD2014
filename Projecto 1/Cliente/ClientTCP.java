package Cliente;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import Util.Ideia;
import Util.Mercado;
import Util.Objecto;
import Util.OrdemCompra;
import Util.OrdemVenda;
import Util.RemoteHost;
import Util.Response;
import Util.Transaccao;
import Util.User;

public class ClientTCP {

	public RemoteHost server;
	private static ClientSocketThread socketThread;
	private static boolean running = true;
	Scanner sc;
	Response resposta;
	Objecto objecto;
	User utilizador;

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
			objecto = null;
			sc = new Scanner(System.in);

			boolean logado = false;
			utilizador = null;

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
					socketThread.setObj(objecto);
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

					User u = null;
					Objecto obj = new Objecto(u);
					socketThread.setObj(obj);
					u = (User) sincronizarObjecto(obj);

					// if (dados(resposta).contains("TRUE")) {
					// utilizador = new User(resposta.getResposta());
					// logado = true;
					// System.out.println("## Login com sucesso ##");
					// } else if (resposta.getResposta().contains("FALSE")) {
					// System.out.println("## Login falhado ##");
					// }

					if (u != null && !u.getUsername().contains("#failed#")) {
						utilizador = u;
						logado = true;
						System.out.println("## Login com sucesso ##");
					} else {
						System.out.println("## Login falhado ##");
					}

				}

			}

			String[] topicos = new String[20];
			int topicoActual = 0;

			while (logado) {

				resposta = new Response();
				socketThread.setResposta(resposta);

				if (utilizador.getHistorico() != null) {
					System.out.print("\n# Tem " + utilizador.getHistorico().size() + " novas transaccoes #\nVer? [S]im - [N]ao >> ");
					String in = sc.nextLine();
					System.out.println();
					if (in.compareToIgnoreCase("s") == 0)
						imprimirNotificacoes();
				}

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

					// int in = 0;
					// boolean ver = false;
					// while (!ver) {
					// System.out.print("Quantidade: ");
					// try {
					// in = sc.nextInt();
					// ver = true;
					// } catch (InputMismatchException e) {
					//
					// }
					// }

					socketThread.adicionarPacote("HISTORICO_TRANSACCOES|" + utilizador.getId() + "|0");
					imprimirTransaccoes(dados(resposta));

				} else if (num == 3) {

					socketThread.adicionarPacote("USER_IDEIAS|" + utilizador.getId());
					imprimirIdeias(dados(resposta));

					if (!resposta.getResposta().contains("|0")) {
						System.out.println("[A]pagar ideia");
						String in = sc.nextLine();

						if (in.compareToIgnoreCase("A") == 0) {
							boolean verf = false;
							while (!verf) {
								System.out.print("ID Ideia >> ");
								String x = sc.nextLine();
								if (x.matches("\\b(\\d)+\\b")) {
									socketThread.adicionarPacote("APAGAR_IDEIA|" + x + "|" + utilizador.getId());
									verf = true;
								} else
									verf = true;

								resposta.setResposta(null);
								String d = dados(resposta);
								if (d.contains("APAGAR_IDEIA")) {
									if (d.contains("true"))
										System.out.println("# Ideia apagada #");
									else
										System.out.println("# A ideia nao pode ser apagada #");
								}
							}
						}
					}

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

						System.out.print("[I]nserir Ideia");

						if (!resposta.getResposta().contains("|0")) {
							System.out.println(" - [C]omprar Ideia");
						}

						System.out.print("\n>> ");
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

	private void imprimirNotificacoes() {
		for (Transaccao t : utilizador.getHistorico()) {
			System.out.println("[ " + t.getTipo() + " ] Ideia " + t.getIdIdeia() + " : " + t.getNumShares() + " shares (" + t.getPreco() + "/share) - Total: " + (t.getTipo().equals("compra") ? "-" : "+") + t.getPago() + "DeiCoins");
		}

		utilizador.setHistorico(null);

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

	private void getUser() {
		socketThread.adicionarPacote("GET_UTILIZADOR|" + utilizador.getId());
		User u = null;
		Objecto obj = new Objecto(u);
		socketThread.setObj(obj);
		Object o = sincronizarObjecto(obj);
		utilizador = (User) o;
	}

	private void imprimirShares(String dados) {
		// SHARES|idShare;idIdeia;numShares;texto
		if (dados != null) {

			if (dados.startsWith("SHARES|0")) {
				System.out.println("Sem shares\n");
			} else {
				getUser();

				String[] split = dados.split("\\|");

				for (int i = 1; i < split.length; i++) {
					String[] t = split[i].split(";");
					System.out.println(t[1] + ". [ " + t[2] + " shares ] [ Cotacao ] " + encurtarIdeia(t[3], 40));
				}

				System.out.print("[L]istar todas as ordens\n>> ");
				String in = sc.nextLine();

				if (in.matches("\\b(\\d)+\\b")) {
					resposta.setResposta(null);
					socketThread.adicionarPacote("MERCADO|" + in);
					imprimirOrdens(dados(resposta));

					socketThread.adicionarPacote("LISTAR_ORDENS|" + utilizador.getId() + "|" + in);

					Mercado m = null;
					Objecto obj = new Objecto(m);
					socketThread.setObj(obj);
					Object o = sincronizarObjecto(obj);
					m = (Mercado) o;

					imprimirOrdens(m);

				} else if (in.compareToIgnoreCase("l") == 0) {
					socketThread.adicionarPacote("LISTAR_ORDENS|" + utilizador.getId() + "|0");
					Mercado m = null;
					Objecto obj = new Objecto(m);
					socketThread.setObj(obj);
					Object o = sincronizarObjecto(obj);
					m = (Mercado) o;
					imprimirOrdens(m);

				}

				if (in.matches("\\b(\\d)+\\b") || in.compareToIgnoreCase("l") == 0) {
					System.out.print("[R]emover ordem - [E]ditar ordem\n>> ");

					String x = sc.nextLine();
					if (x.compareToIgnoreCase("r") == 0) {

						System.out.print("Ordem de [C]ompra ou [V]enda >> ");
						String ocv = sc.nextLine();
						int tipo;
						if (ocv.compareToIgnoreCase("c") == 0) {
							tipo = 0;
						} else
							tipo = 1;

						String idordem;
						do {
							System.out.print("ID ordem >> ");
							idordem = sc.nextLine();
						} while (!idordem.matches("\\b(\\d)+\\b"));

						socketThread.adicionarPacote("REMOVER_ORDEM|" + utilizador.getId() + "|" + tipo + "|" + idordem);

					}
				}
			}
		}
	}

	private void imprimirOrdens(Mercado m) {
		System.out.println("\n[ Suas Ordens de Compra ]");
		// ir para ideia

		// alterar ordem
		if (m.getOrdensCompra().size() > 0)
			for (OrdemCompra oc : m.getOrdensCompra())
				System.out.println(oc.getId() + ".\t [Ideia " + oc.getIdideia() + "] " + oc.getNum_shares() + " shares\t" + oc.getPreco_por_share() + "/share");
		else
			System.out.println("-- Sem ordens --");

		System.out.println("\n[ Suas Ordens de Venda ]");

		if (m.getOrdensVenda().size() > 0)
			for (OrdemVenda oc : m.getOrdensVenda())
				System.out.println(oc.getId() + ".\t [Ideia " + oc.getIdideia() + "] " + oc.getNum_shares() + " shares\t" + oc.getPreco_por_share() + "/share");
		else
			System.out.println("-- Sem ordens --");
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
			criarIdeia(topicoActual, utilizador, 0, 0, "null");
		else if (comando.equals("C") || comando.equals("c"))
			comprarIdeia();
		else if (comando.matches("\\b(\\d)+\\b")) {
			menuIdeia(comando, topicoActual);
		}

		getUser();

	}

	private void menuIdeia(String ideiaActual, int topicoActual) {
		socketThread.adicionarPacote("IDEIA|" + ideiaActual);

		ArrayList<Ideia> ideias = null;
		Objecto obj = new Objecto(ideias);
		socketThread.setObj(obj);

		Object o = sincronizarObjecto(obj);
		ideias = (ArrayList<Ideia>) o;

		if (ideias.size() > 0) {
			System.out.println(ideias.get(0).getIdIdeia() + ". [ " + ideias.get(0).getUsername() + " ] " + ideias.get(0).getTexto() + " - " + ideias.get(0).getData());
			for (int i = 1; i < ideias.size(); i++) {
				Ideia id = ideias.get(i);
				System.out.println("\t> " + id.getIdIdeia() + ". [ " + id.getUsername() + " ] < " + id.getSentimento() + " > " + id.getTexto() + " - " + id.getData());
			}
		}
		// responder
		// comprar shares

		System.out.println("\n[R]esponder - [C]omprar ideia");

		String in = sc.nextLine();

		if (in.equals("R") || in.equals("r")) {
			// responderIdeia(ideiaActual, in);
			String idIdeia;
			do {
				System.out.print("ID >> ");
				idIdeia = sc.nextLine();
			} while (!idIdeia.matches("\\b(\\d)+\\b"));

			String sentimento = null;
			boolean v = false;
			while (!v) {
				System.out.print("[P]ositivo - [N]egativo - N[E]utro >> ");
				sentimento = sc.nextLine();
				if (sentimento.compareToIgnoreCase("p") == 0) {
					sentimento = "positivo";
					v = true;
				} else if (sentimento.compareToIgnoreCase("n") == 0) {
					sentimento = "negativo";
					v = true;
				} else if (sentimento.compareToIgnoreCase("e") == 0) {
					sentimento = "neutro";
					v = true;
				}
				System.out.println();
			}

			criarIdeia(topicoActual, utilizador, Integer.parseInt(ideiaActual), Integer.parseInt(idIdeia), sentimento);
		} else if (in.equals("C") || in.equals("c"))
			comprarIdeia();
		// else if (comando.matches("\\b(\\d)+\\b")) {
		// menuIdeia(comando);
		// }

	}

	private void imprime(Ideia ideia, ArrayList<Ideia> ideias, String barraT) {
		System.out.println(ideia.getIdIdeia() + " " + ideia.getTexto());

		for (Ideia i : ideias) {

			if (i.getIdIdeiaPrincipal() == ideia.getIdIdeia()) {

				imprime(i, ideias, "\t");
			}
			System.out.print(" ");
		}

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

		resposta.setResposta(null);
		socketThread.adicionarPacote("MERCADO|" + id);
		imprimirOrdens(dados(resposta));

		verf = false;
		while (!verf) {
			String in = "";
			do {
				System.out.print("\n<numShares> <preco por share (ate 2 casas decimais)>\nOrdem compra >> ");
				in = sc.nextLine();
			} while (!in.matches("\\b(\\d)+ [0-9]+(\\.?[0-9]{1,2})\\b"));

			String[] split = in.split(" ");
			int numShares = Integer.parseInt(split[0]);
			double preco = Double.parseDouble(split[1]);
			double precoTotal = numShares * preco;

			System.out.println("Preco total: " + precoTotal + " DeiCoins\nDisponivel em conta: " + utilizador.getDeicoins() + " DeiCoins");

			if (precoTotal > utilizador.getDeicoins()) {
				System.out.println("O preco total supera o montante em conta");
				System.out.println("Nova ordem? [S]im [N]ao\n>> ");
				in = sc.nextLine();
				if (in.compareToIgnoreCase("s") != 0)
					verf = true;
			} else {
				System.out.println("\nCompra de " + numShares + " shares no valor de " + precoTotal + " (" + preco + "/share) Ideia: " + id + "\n\nCONFIRMAR? [S]im [N]ao\n>> ");
				in = sc.nextLine();
				if (in.compareToIgnoreCase("s") == 0) {
					// ORDEM_COMPRA|idIdeia;idUser;numShares;preco_por_share;precoTotal
					String compra = "ORDEM_COMPRA|" + id + ";" + utilizador.getId() + ";" + numShares + ";" + preco + ";" + precoTotal + ";" + new Timestamp(new Date().getTime());
					socketThread.adicionarPacote(compra);
				}
				verf = true;
			}
		}

	}

	private void criarIdeia(int topico, User user, int idMae, int idIdeia, String sentimento) {
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

			String ideia = "CRIAR_IDEIA|" + user.getId() + "|" + topico + "|" + texto + "|" + preco + "|" + getTime() + "|" + idMae + ";" + idIdeia + ";" + sentimento + "|" + file + "|" + getFileSize(path) + "|" + getExtensao(path);
			socketThread.adicionarPacote(ideia);

			resposta.setResposta(null);
			String check = dados(resposta);
			if (check.contains("RECEBER_FILE|CHECK"))
				socketThread.enviarFicheiro(path);

			socketThread.setLigado(true);
		} else {
			String ideia = "CRIAR_IDEIA|" + user.getId() + "|" + topico + "|" + texto + "|" + preco + "|" + getTime() + "|" + idMae + ";" + idIdeia + ";" + sentimento + "|" + file;
			socketThread.adicionarPacote(ideia);
		}

		// if (receberCheck())
		// System.out.println("## ideia inserida");
		// else
		// System.out.println("## nope");

	}

	private boolean receberCheck() {
		resposta.setResposta(null);
		if (dados(resposta).contains("CHECK")) {
			return true;
		} else
			return false;

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

			if (dados.startsWith("TOPICO_IDEIAS|0") || dados.startsWith("USER_IDEIAS|0")) {
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
		System.out.println("# DeiCoins: " + utilizador.getDeicoins() + " #");
		// System.out.println("5. Listar Topicos");
		// System.out.println("5. ");
	}

	private User criarUser(String dados) {
		// LOGIN|TRUE|id|username|deicoins
		String[] split = dados.split("\\|");

		return new User(Integer.parseInt(split[2]), split[3], Double.parseDouble(split[4]), null);
	}

	private String dados(Response resposta) {
		synchronized (resposta) {
			while (resposta.isNull()) {
				try {
					resposta.wait(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		return resposta.getResposta();
	}

	private Object sincronizarObjecto(Objecto obj) {
		synchronized (obj) {

			try {
				obj.wait(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		return obj.getObj();
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