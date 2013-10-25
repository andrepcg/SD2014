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

	public int criarIdeia(int idUser, int idTopico, String texto, double preco, String data, int idMae, int iIdeiaPrincipal, String sentimento) throws RemoteException;

	public ArrayList<Share> seleccionarShares(int idUser) throws RemoteException;

	public boolean inserirFicheiro(int idIdeia, String path) throws RemoteException;

	public Mercado mercadoShares(int idIdeia) throws RemoteException;

	public int criarOrdem(int tipo, int idIdeia, int idUser, int numShares, double preco_por_share, double precoTotal, String timestamp) throws RemoteException;

	public ArrayList<Ideia> ideiaRespostas(int idIdeia) throws RemoteException;

	public ArrayList<Ideia> seleccionarIdeia(int idMae) throws RemoteException;

	public Mercado ordensUser(int idUser, int idIdeia) throws RemoteException;

	public boolean apagarIdeia(int idIdeia, int idUser) throws RemoteException;

	public User getUser(int parseInt) throws RemoteException;

	public boolean removerOrdem(int idUser, int tipo, int idOrdem) throws RemoteException;
}