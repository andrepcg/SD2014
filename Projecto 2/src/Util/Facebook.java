package Util;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Facebook {

	/**
	 * @uml.property  name="signed_request"
	 */
	private String signed_request;
	/**
	 * @uml.property  name="app_secret"
	 */
	private final String app_secret;

	/**
	 * @uml.property  name="hashCorrecta"
	 */
	private boolean hashCorrecta = false;
	/**
	 * @uml.property  name="code"
	 */
	private String code;
	/**
	 * @uml.property  name="issued_at"
	 */
	private long issued_at;
	/**
	 * @uml.property  name="user_id"
	 */
	private String user_id;

	/**
	 * @uml.property  name="access_token"
	 */
	public String access_token;

	public Facebook(String app_secret, String signed_request, String access_token) {
		this.signed_request = signed_request;
		this.app_secret = app_secret;
		this.access_token = access_token;

		// decodeSignedrequest();

		decodeSignedrequest();

	}

	private boolean decodeSignedrequest() {
		String[] split = signed_request.split("\\.", 2);
		byte[] sig = base64UrlDecode(split[0]);
		byte[] encrpt;

		try {
			encrpt = encode(app_secret, split[1]);
			if (Arrays.equals(sig, encrpt))
				hashCorrecta = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (hashCorrecta) {

			String data = new String(base64UrlDecode(split[1]));
			JSONObject obj = (JSONObject) JSONValue.parse(data);
			code = (String) obj.get("code");
			issued_at = (Long) obj.get("issued_at");
			user_id = (String) obj.get("user_id");

			return true;
		}
		return false;
	}

	public static byte[] base64UrlDecode(String input) {

		return Base64.decodeBase64(input.replace("-", "+").replace("_", "/").getBytes());
	}

	public static byte[] encode(String key, String data) throws Exception {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		sha256_HMAC.init(secret_key);

		// return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
		return sha256_HMAC.doFinal(data.getBytes());
	}

	/**
	 * @return
	 * @uml.property  name="hashCorrecta"
	 */
	public boolean isHashCorrecta() {
		return hashCorrecta;
	}

	/**
	 * @param hashCorrecta
	 * @uml.property  name="hashCorrecta"
	 */
	public void setHashCorrecta(boolean hashCorrecta) {
		this.hashCorrecta = hashCorrecta;
	}

	/**
	 * @return
	 * @uml.property  name="code"
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 * @uml.property  name="code"
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return
	 * @uml.property  name="issued_at"
	 */
	public long getIssued_at() {
		return issued_at;
	}

	/**
	 * @param issued_at
	 * @uml.property  name="issued_at"
	 */
	public void setIssued_at(long issued_at) {
		this.issued_at = issued_at;
	}

	/**
	 * @return
	 * @uml.property  name="user_id"
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id
	 * @uml.property  name="user_id"
	 */
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
