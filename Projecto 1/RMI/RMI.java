package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import SQL.OracleJDBC;
import Util.Ideia;
import Util.Mercado;
import Util.Share;
import Util.Topico;
import Util.Transaccao;
import Util.User;

public interface RMI extends Remote {

	public OracleJDBC connectDB() throws RemoteException;

	public boolean registo(String user, String pass) throws RemoteException;

	public User login(String user, String password) throws RemoteException;

	public boolean criarTopico(String nome) throws RemoteException;

	public ArrayList<Topico> mostraTopicos() throws RemoteException;

	public ArrayList<Transaccao> historicoTransaccoes(int idUser, int limit) throws RemoteException;

	public ArrayList<Ideia> mostraIdeias(int idTopico, int idUser) throws RemoteException;

	public int criarIdeia(int idUser, int idTopico, String texto, double preco, String data) throws RemoteException;

	public ArrayList<Share> seleccionarShares(int idUser) throws RemoteException;

	public boolean inserirFicheiro(int idIdeia, String path) throws RemoteException;

	public Mercado mercadoShares(int idIdeia) throws RemoteException;
}