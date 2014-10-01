package Util;

import java.io.Serializable;
import java.text.Normalizer;

public class Strings implements Serializable {

	public static String replaceSpecialChars(String s) {
		return Normalizer.normalize(s.replaceAll("[\\/',.]", ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String replaceSpecialChars(String s, String regex) {
		return Normalizer.normalize(s.replaceAll(regex, ""), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	public static String lowercase(String s) {
		String n = "";
		for (int index = 0; index < s.length(); index++) {
			char c = s.charAt(index);
			n += Character.toLowerCase(c);

		}
		return n;
	}

	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

}
