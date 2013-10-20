package RMI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import SQL.OracleJDBC;
import Util.Topico;
import Util.Transaccao;

public interface RMI extends Remote {

	public OracleJDBC connectDB() throws RemoteException;

	public boolean registo(String user, String pass) throws RemoteException;

	public int login(String user, String password) throws RemoteException;

	public boolean criarTopico(String nome) throws RemoteException;

	public ArrayList<Topico> mostraTopicos() throws RemoteException;

	public ArrayList<Transaccao> historicoTransaccoes(int idUser, int limit) throws RemoteException;
	// public ArrayList<Ideia> mostraIdeias(int idTopico) throws
	// RemoteException;
}