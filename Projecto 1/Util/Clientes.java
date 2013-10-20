package Util;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map.Entry;

public class Clientes {
	private HashMap<InetAddress, Integer> clientes;

	public Clientes() {
		clientes = new HashMap<>();
	}

	public synchronized void put(InetAddress ip, int port) {
		clientes.put(ip, port);
	}

	public synchronized void remove(InetAddress ip) {
		clientes.remove(ip);
	}

	public String listarClientes() {
		String s = "";
		for (Entry<InetAddress, Integer> cliente : clientes.entrySet()) {
			s += cliente.getKey() + ":" + cliente.getValue() + "\n";
		}
		return s;
	}
}
