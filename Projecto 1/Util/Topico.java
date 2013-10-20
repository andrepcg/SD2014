package Util;
import java.io.*;
public class Topico implements Serializable{
	
	private String nome;
	private int id;
	

	public Topico(int id,String nome){
		this.setId(id);
		this.setNome(nome);
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}

} 