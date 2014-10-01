package Util;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map.Entry;

public class Clientes implements Serializable {
	/**
	 * @uml.property  name="clientes"
	 * @uml.associationEnd  qualifier="ip:java.net.InetAddress java.lang.Integer"
	 */
	private HashMap<InetAddress, Integer> clientes;

	public Clientes() {
		clientes = new HashMap<InetAddress, Integer>();
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
