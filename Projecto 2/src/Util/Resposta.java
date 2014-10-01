package Util;
public class Resposta {

	/**
	 * @uml.property  name="resposta"
	 */
	private String resposta;

	public Resposta() {
		this.resposta = null;
	}

	/**
	 * @return
	 * @uml.property  name="resposta"
	 */
	public String getResposta() {
		return resposta;
	}

	/**
	 * @param resposta
	 * @uml.property  name="resposta"
	 */
	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public boolean isNull() {
		return (resposta == null) ? true : false;
	}
}
