import java.net.InetAddress;
import java.net.UnknownHostException;

public class RemoteHost {

	private String host;
	private int port;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

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
		this.host = host.split(":")[0];
		this.port = new Integer(host.split(":")[1]).intValue();
	}

	public String toString() {
		return host + ":" + port;
	}

	public InetAddress getHostInet() throws UnknownHostException {
		return InetAddress.getByName(host);
	}
}
