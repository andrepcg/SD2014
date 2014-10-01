package RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import Util.Grupo;
import Util.Ideia;
import Util.Mercado;
import Util.Ordem;
import Util.Share;
import Util.Topico;
import Util.Transaccao;
import Util.Tx;
import Util.User;

public interface RMI extends Remote {

	public int criarGrupo(String nome, int idUser) throws RemoteException;

	public ArrayList<Transaccao> getHistorialPreco(int idIdeia) throws RemoteException;

	public String getFacebookPost(int idIdeia) throws RemoteException;

	public boolean registo(String user, String pass) throws RemoteException;

	public User login(String user, String pass) throws RemoteException;

	public boolean criarTopico(String nome) throws RemoteException;

	public ArrayList<Transaccao> transaccoes(int idUser, int limit) throws RemoteException;

	public ArrayList<Topico> mostraTopicos(boolean numIdeias) throws RemoteException;

	public ArrayList<Ideia> getIdeiasUser(int idUser, int pagina, int ideias_por_pagina) throws RemoteException;

	public ArrayList<Ideia> ideiasTopico(int idTopico, int pagina, int ideias_por_pagina) throws RemoteException;

	public ArrayList<Topico> getTopicosIdeia(int idIdeia) throws RemoteException;

	public int getNumIdeias() throws RemoteException;

	public ArrayList<Ideia> getIdeias(int pagina, int ideias_por_pagina) throws RemoteException;

	public ArrayList<Topico> pesquisaTopico(String pesquisa) throws RemoteException;

	public int numIdeiasTopico(int idTopico) throws RemoteException;

	public ArrayList<Ideia> pesquisarIdeias(String[] keywords) throws RemoteException;

	public ArrayList<Ideia> seleccionarIdeia(int idMae) throws RemoteException;

	public Ideia seleccionarIdeia2(int idMae) throws RemoteException;

	public ArrayList<Ideia> ideiaRespostas(int idIdeia) throws RemoteException;

	public Share getUserIdeiaShares(int idUser, int idIdeia) throws RemoteException;

	public ArrayList<Share> seleccionarShares(int idUser) throws RemoteException;

	public boolean removerUserGrupo(int grupoid, int uid) throws RemoteException;

	public boolean addUserGrupo(int grupoid, int uid, String uname) throws RemoteException;

	public Grupo getGroupDetails(int idGrupo) throws RemoteException;

	public ArrayList<User> getUsersFromGroup(int idGrupo) throws RemoteException;

	public ArrayList<Grupo> getUserGroups(int idUser) throws RemoteException;

	public int criarIdeia(int idUser, ArrayList<String> topicos, String titulo, String texto, double investir, int idGrupo, String ficheiro, String facebook_post) throws RemoteException;

	public Mercado ordensUser(int idUser, int idIdeia) throws RemoteException;

	public void comentarIdeia(int idUser, int idTopico, String texto, double preco, String data, int idIdeia) throws RemoteException;

	public Mercado mercadoShares(int idIdeia) throws RemoteException;

	public Mercado getMercado(int idIdeia) throws RemoteException;

	public boolean inserirFicheiro(int idIdeia, String path) throws RemoteException;

	public ArrayList<Tx> criarOrdem(int tipo, int idIdeia, int idUser, int numShares, double preco_por_share, double precoTotal, boolean limitOrder) throws RemoteException;

	public double ultimoPreco(int idIdeia) throws RemoteException;

	public Topico getTopico(int idTopico) throws RemoteException;

	public Ordem getOrdem(int tipo, int id) throws RemoteException;

	public boolean eliminaIdeias(int idIdeia, int idUser) throws RemoteException;

	public User getUser(int idUser) throws RemoteException;

	public User getUserFacebook(String facebookID) throws RemoteException;

	public boolean setUserFacebook(int idUser, String facebookID) throws RemoteException;

	public boolean removerOrdem(int idUser, int tipo, int idOrdem) throws RemoteException;

	public ArrayList<Tx> editarOrdem(int tipo, int idordem, int idUser, int numShares, double preco_por_share, boolean limit) throws RemoteException;

}