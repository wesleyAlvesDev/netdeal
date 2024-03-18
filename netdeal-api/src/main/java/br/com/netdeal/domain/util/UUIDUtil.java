package br.com.netdeal.domain.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

public class UUIDUtil {
	
	//Nil UUID (http://tools.ietf.org/html/rfc4122#section-4.1.7)
	private static final UUID NIL = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	
    /**
     * Converte um UUID para um vetor de bytes para uso como chave para criptografia<br/>
     * <br/>
     * A conversão utiliza um a representação textual (hexadecimal) que é consistente entre as plataformas (Java, PHP, C#)
     * Cada par de caracteres torna-se um byte. Ex: 78ca -> {0x78,0xca} -> {120,-54}<br/>
     * <br/>
     * @param uuid
     * @return sua representação em um vetor de bytes
     */
    public static byte[] asBytes(final UUID uuid){
		final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		// Remove os traços e quebra a representação em pares de caracteres (número hexadecimal) 
		Stream.of(uuid.toString().replaceAll("-", "").split("(?<=\\G.{2})"))
			.forEach(
				// Converte cada dígito hexadecimal em um byte
				// Necessário converter em int e fazer o cast explícito
				hex -> bb.put((byte) Integer.parseInt(hex, 16)
			)
		);
		return bb.array();
    }
    
    
    /**
     * Método que retorna um NIL UUID, conforme descrito na <a href='http://tools.ietf.org/html/rfc4122#section-4.1.7'>RFC 4122</a>
     * 
     * @return o NIL UUID conforme especificado na RFC
     */
    public static UUID nil() {
    	return NIL;
    }
    
    /**
	 * Retorna um UUID randômico
	 * 
	 * O encapsulamente visa incorporar alterações futuras no modo de criação
	 * 
	 */
	public static synchronized UUID random(){
		return UUID.randomUUID();
	}
	
	/**
	 * Retorna um UUID randômico codificado num formato Base64 URL-safe
	 * 
	 * 
	 */
	public static String randomBase64UUID(){
		return uuidToBase64(random());
	}

	/**
	 * Converte um UUID em sua representação Base64 URL-safe
	 * O padding é removido da String para redução do tamanho total 
	 * 
	 * @param uuid o UUID a ser convertido
	 * @return a representação Base64 do UUID
	 */
	public static String uuidToBase64(final UUID uuid) {
		final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    final byte[] base64 = Base64.getUrlEncoder().encode(bb.array());
		final String base64String = new String(base64, CHARSET);
		final String trimmedBase64String = base64String.substring(0, 22); //remove o '==' do final
		return trimmedBase64String;
	}
	
	/**
	 * Converte um String em Base64 URL-Safe para UUID
	 * 
	 * @param base64 - String comn 22 caracteres em base64
	 * @return
	 */
	public static UUID base64ToUUID (final String base64){
		// não é necessário, o Decoder do Java não precisa do padding
		// incluindo para garantir compatibilidade caso o Java mude o comportamento  
		final String paddedBase64 = base64 + "==";
		final byte[] decoded = Base64.getUrlDecoder().decode(paddedBase64.getBytes(CHARSET));
		final ByteBuffer bb = ByteBuffer.wrap(decoded);
		final UUID uuid = new UUID(bb.getLong(), bb.getLong());
		return uuid;
	}
    
    public static void main(String[] args) {
		byte[] bytes = asBytes(nil());
		for (byte b : bytes) {
			System.out.println(b);
		}
	}
    

}