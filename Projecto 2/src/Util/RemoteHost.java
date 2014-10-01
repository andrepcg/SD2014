package Util;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class RemoteHost {

	/**
	 * @uml.property  name="host"
	 */
	private String host;
	/**
	 * @uml.property  name="port"
	 */
	private int port;

	/**
	 * @return
	 * @uml.property  name="host"
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 * @uml.property  name="host"
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return
	 * @uml.property  name="port"
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 * @uml.property  name="port"
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public RemoteHost(String ip, int port) {
		super();
		this.host = ip;
		this.port = port;
	}

	public RemoteHost(String ip, String porta) {
		host = ip;
		port = Integer.parseInt(porta);
	}

	public RemoteHost(String host) {
		if (host != null) {
			this.host = host.split(":")[0];
			this.port = new Integer(host.split(":")[1]).intValue();
		}
	}

	public String toString() {
		return host + ":" + port;
	}

	public InetAddress getHostInet() throws UnknownHostException {
		return InetAddress.getByName(host);
	}
}
