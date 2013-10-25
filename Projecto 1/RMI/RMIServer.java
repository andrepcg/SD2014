package RMI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import SQL.OracleJDBC;
import Util.Ideia;
import Util.Mercado;
import Util.Share;
import Util.Topico;
import Util.Transaccao;
import Util.User;

public class RMIServer extends UnicastRemoteObject implements RMI {
	OracleJDBC oracle;

	public RMIServer() throws RemoteException {
		super();
		Registry r = LocateRegistry.createRegistry(6000);
		r.rebind("registry", this);

		System.out.println("Server online");
		oracle = connectDB();
	}

	public boolean removerOrdem(int idUser, int tipo, int idOrdem) throws RemoteException {
		return oracle.removerOrdem(idUser, tipo, idOrdem);
	}

	public boolean registo(String user, String password) throws RemoteException {

		return oracle.registo(user, password);

	}

	public User login(String user, String password) throws RemoteException {

		return oracle.login(user, password);

	}

	public boolean criarTopico(String nome) throws RemoteException {
		return oracle.criarTopico(nome);

	}

	public ArrayList<Topico> mostraTopicos() throws RemoteException {
		return oracle.mostraTopicos();

	}

	public ArrayList<Transaccao> historicoTransaccoes(int idUser, int limit) throws RemoteException {
		return oracle.transaccoes(idUser, limit);
	}

	public ArrayList<Ideia> mostraIdeias(int idTopico, int idUser) throws RemoteException {
		return oracle.mostraIdeias(idTopico, idUser);
	}

	public int criarIdeia(int idUser, int idTopico, String texto, double preco, String data, int idMae, int idIdeiaPrincipal, String sentimento) throws RemoteException {
		return oracle.criarIdeia(idUser, idTopico, texto, preco, data, idMae, idIdeiaPrincipal, sentimento);
	}

	public ArrayList<Share> seleccionarShares(int idUser) throws RemoteException {
		return oracle.seleccionarShares(idUser);
	}

	public boolean inserirFicheiro(int idIdeia, String path) throws RemoteException {
		return oracle.inserirFicheiro(idIdeia, path);
	}

	public Mercado mercadoShares(int idIdeia) throws RemoteException {
		return oracle.mercadoShares(idIdeia);
	}

	public int criarOrdem(int tipo, int idIdeia, int idUser, int numShares, double preco_por_share, double precoTotal, String timestamp) throws RemoteException {
		return oracle.criarOrdem(tipo, idIdeia, idUser, numShares, preco_por_share, precoTotal, timestamp);
	}

	public ArrayList<Ideia> ideiaRespostas(int idIdeia) throws RemoteException {
		return oracle.ideiaRespostas(idIdeia);
	}

	public ArrayList<Ideia> seleccionarIdeia(int idMae) throws RemoteException {
		return oracle.seleccionarIdeia(idMae);
	}

	public Mercado ordensUser(int idUser, int idIdeia) throws RemoteException {
		return oracle.ordensUser(idUser, idIdeia);
	}

	public boolean apagarIdeia(int idIdeia, int idUser) throws RemoteException {
		return oracle.eliminaIdeias(idIdeia, idUser);
	}

	public User getUser(int idUser) throws RemoteException {
		return oracle.getUser(idUser);
	}

	public OracleJDBC connectDB() {
		return new OracleJDBC();
	}

	public static void main(String args[]) {
		try {

			new RMIServer();

		} catch (RemoteException e) {
			System.out.println("Remote exception");
		} catch (NoSuchMethodError e) {

		}

	}
}
