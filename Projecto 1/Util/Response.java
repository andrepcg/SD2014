package Util;
public class Response {

	private String resposta;

	public Response() {
		this.resposta = null;
	}

	public String getResposta() {
		return resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	public boolean isNull() {
		return (resposta == null) ? true : false;
	}
}
