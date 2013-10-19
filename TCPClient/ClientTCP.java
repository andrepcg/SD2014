import java.io.IOException;
import java.util.Scanner;

public class ClientTCP {

	public RemoteHost server;
	private static ClientSocketThread socketThread;
	private static boolean running = true;

	public static void main(String args[]) throws IOException {

		// String loader = getHost(args);
		String loader = "192.168.1.81:9000";

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

		System.out.println("1. Login\n2. Registar");
		String input = sc.nextLine();

		boolean logado = false;

		if (input.startsWith("2")) {
			System.out.print("Username: ");
			String username = sc.nextLine();

			while (!username.matches("[a-zA-Z0-9]{4}[a-zA-Z0-9]*")) {
				System.out.print("## minimo de 4 caracteres alfanumericos ##\nUsername: ");
				username = sc.nextLine();
			}

			System.out.print("Password: ");
			String password = sc.nextLine();

			if (socketThread.registar(username, password))
				System.out.println("## Registado com sucesso ##");
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

			if (dados(resposta).equals("TRUE")) {
				logado = true;
				System.out.println("## Login com sucesso ##");
			}

		}

		while (logado) {
			resposta = new Response();
			socketThread.setResposta(resposta);

			imprimirMenuPrincipal();
			input = sc.nextLine();

			if (input.startsWith("1")) {
				socketThread.adicionarPacote("LISTARTOPICOS");
				imprimirDados(dados(resposta), true);
				// imprimirDados(socketThread.listarTopicos(), true);
				// Thread.currentThread().wait();
			} else if (input.startsWith("2")) {

			} else if (input.startsWith("3")) {

			} else if (input.startsWith("4")) {

			}
		}

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

	private void imprimirMenuPrincipal() {
		System.out.println("\n## MENU ##");
		System.out.println("1. Listar Topicos");
		System.out.println("2. Historico de Transaccoes");
		System.out.println("3. Minhas ideias");
		System.out.println("4. Carteira de Shares");
		// System.out.println("5. ");
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