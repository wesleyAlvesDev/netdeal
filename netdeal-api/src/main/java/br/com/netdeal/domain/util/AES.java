package br.com.netdeal.domain.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

/**
 * Classe thread-safe com métodos para criptografia e decriptografia utilizando AES<br/><br/>
 * 
 * O encoding padrão dos textos de entrada e saída é o UTF-8.<br/>
 * 
 * O formato do conteúdo criptogrado é:
 * <ul>
 * 	<li>Vetor de inicialização (128 bits)</li>
 * 	<li>Mensagem</li>
 * </ul>
 * 
 * <br/>
 * A chave de criptografia pode ser um {@link UUID}, cuja representação interna possui 128 bits,
 * atendendo a especificação do algoritmo AES. Ver conversão no método {@link #asBytes(UUID)}<br/>
 * <br/>
 * O vetor de inicialização deve ser randômico, o que foi garantido no método {@link #generateIV()}<br/>
 * <br/>
 * A linha guia nesse código foi desenvolver algo capaz de ser facilmente portado entre várias
 * linguagens, como C# e PHP, apenas para citar as mais utilizadas pelo TCESP. Partiu-se da premissa
 * de fazer algo que exigisse implementação de métodos simples em cada linguagem para funcionar do 
 * que algo que estivesse pronto em Java mas fosse demasiadamente complexo para implementar em 
 * outras linguagens.<br/>
 * <br/>
 * Alguns recursos de segurança foram incorporados, como a limpeza do vetor de inicialização 
 * a cada operação realizada.<br/>
 * <br/>
 * @author dvivencio
 *
 */
public class AES {

	// Tamanhos em bytes do vetor de inicialização e da chave criptográfica
	private static final int 	IV_SIZE = 16;
	private static final int 	KEY_SIZE = 16;
	
	// Especificação do algoritmo
	private static final String ALGORITHM 	= "AES";
	private static final String BLOCK_MODE 	= "CBC";
	private static final String PADDING 	= "PKCS5PADDING";
	
	private static final String CIPHER = String.format("%s/%s/%s", ALGORITHM, BLOCK_MODE, PADDING);
	
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	
	/*
	 *  Chave de criptografia
	 *  Não deve ser acessada diretamente pelos métodos de negócio
	 */
	private final byte[] key;
	
	public AES(final byte[] key) {	
		if (key == null || key.length != KEY_SIZE)
			throw new IllegalArgumentException(String.format("Chave deve ter tamanho de %s bytes", KEY_SIZE));
		this.key = key;
	}
	
	public AES(final UUID chave) {
		this (UUIDUtil.asBytes(chave));
	}
		
	/**
	 * Método que encripta o conteúdo de uma string com a chave fornecida<br/>
	 * <br/>
	 * Fluxo básico:
	 * <ol>
	 * 	<li>Geração do Vetor de inicialização</li>
	 *  <li>Criptografia do conteúdo convertido para UTF-8</li>
	 *  <li>Geração da saída (IV + conteúdo cifrado)</li>
	 *  <li>Codificação da saída em Base64</li>
	 * </ol>
	 * 
	 * @param input
	 * @return
	 */
    public String encripta(final String input) {
    	checkNotNull(input);
        final byte[] output = encripta(input.getBytes(CHARSET));
        return new String(Base64.getEncoder().encode(output), CHARSET);
    }
            
    /**
	 * Método que encripta o conteúdo de um array de bytes com a chave fornecida<br/>
	 * <br/>
	 * Fluxo básico:
	 * <ol>
	 * 	<li>Geração do Vetor de inicialização</li>
	 *  <li>Criptografia do conteúdo</li>
	 *  <li>Geração da saída (IV + conteúdo cifrado)</li>
	 * </ol>
	 * 
	 * @param input
	 * @return
	 */
    public byte[] encripta(final byte[] input) {
    	checkNotNull(input);
        try {
            final byte[] iv 		= generateIV();

            final Cipher cipher 	= getCipher(ENCRYPT_MODE, iv);
            
            final byte[] encrypted 	= cipher.doFinal(input);
            
            final byte [] output 	= generateOutput(iv, encrypted);
            
            wipe(iv);
            
            return output;
        } catch (Exception ex) {
        	throw new IllegalStateException("Erro no processo de encriptação AES", ex);
        }
    }

	/**
     * Método que decripta o conteúdo de uma string com a chave fornecida<br/>
     * <br/>
     * Fluxo básico:
	 * <ol>
	 * 	<li>Decodificação da mensagem em Base 64</li>
	 * 	<li>Extração do vetor de inicialização</li>
	 *  <li>Extração da mensagem cifrada</li>
	 *  <li>Decriptação da mensagem cifrada</li>
	 *  <li>Geração da String de saída em UTF-8</li>
	 * </ol>
     * 
     * @param input
     * @return
     */
    public String decripta(final String input) {
    	checkNotNull(input);
    	final byte[] decoded 	= Base64.getMimeDecoder().decode(input.getBytes(CHARSET));
    	final byte[] original 	= decripta(decoded);
        return new String(original, CHARSET);
        
    }
    
    /**
     * Método que decripta o conteúdo de uma string com a chave fornecida<br/>
     * <br/>
     * Fluxo básico:
	 * <ol>
	 * 	<li>Extração do vetor de inicialização</li>
	 *  <li>Extração da mensagem cifrada</li>
	 *  <li>Decriptação da mensagem cifrada</li>
	 * </ol>
     * 
     * @param input
     * @return
     */
    public byte[] decripta(final byte[] input) {
    	checkNotNull(input);
        try {
        	final ByteBuffer bb 	= ByteBuffer.wrap(input);
        	
        	final byte[] iv 		= getFromBuffer(bb, IV_SIZE);
        	
        	final byte[] encrypted 	= getRemainingFromBuffer(bb);
        	
        	final Cipher cipher 	= getCipher(DECRYPT_MODE, iv);
        	
			final byte[] original 	= cipher.doFinal(encrypted);
			
            wipe(iv);
			
            return original;
        } catch (Exception ex) {
            throw new IllegalStateException("Erro no processo de decriptação AES", ex);
        }
    }
    
    /**
     * Gera um vetor de inicialização de {@value #IV_SIZE} bytes para utilização na criptografia AES<br/>
     * <br/>
     * Referência.: <a href='http://www.cryptofails.com/post/70059609995/crypto-noobs-1-initialization-vectors'>Link</a><br/>
     * <br/>  
     * @return o vetor de incialização, com 16 bytes
     */
    private byte[] generateIV(){
    	return randomBytes(IV_SIZE);
    }

    /* 
     * Encapsulado para permitir possíveis mudanças
     * Ex.: Criptografar o conteúdo em memória
     */
    private byte [] getKey() {
		return key;
	}
        
    private void checkNotNull(final Object input) {
		if (input == null) {
    		throw new IllegalArgumentException("Entrada não deve ser nula");
    	}
	}

    /*
     * Método que limpa a conteúdo de um array da memória
     * Objetivo é evitar memory probing
     */
	private void wipe(final byte[] array) {
		Arrays.fill(array, (byte) 0);
	}
    
	private byte[] generateOutput(final byte[] iv, final byte[] encrypted) {
		final ByteBuffer bb = ByteBuffer.allocate(iv.length + encrypted.length);
		bb.put(iv);
		bb.put(encrypted);
		return bb.array();
	}
	
	private byte[] getFromBuffer(final ByteBuffer bb, final int length) {
		final byte[] aux = new byte[length];
		bb.get(aux);
		return aux;
	}
	
	private byte[] getRemainingFromBuffer(final ByteBuffer bb) {
		return getFromBuffer(bb, bb.remaining());
	}

    private Cipher getCipher(final int cipherMode, final byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
    	final Cipher cipher = Cipher.getInstance(CIPHER);
        final SecretKeySpec skeySpec = new SecretKeySpec(getKey(), ALGORITHM);
        final IvParameterSpec initVector = new IvParameterSpec(iv);
        cipher.init(cipherMode, skeySpec, initVector);
		return cipher;
	}
    
    private byte[] randomBytes(final int size){
    	//Necessário um gerador de números aleatórios criptograficamente forte
    	final Random random = new SecureRandom();
    	final byte[] bytes = new byte[size];
    	//Comente a linha abaixo para gerar uma saída apenas com zeros (útil para testes)
    	random.nextBytes(bytes);
		return bytes;
    }
    
    /*
     * Limpa o valor da chave da memória no momento da finalização 
     * 
     */
	@Override
    protected void finalize() throws Throwable {
    	super.finalize();
        wipe(key);
    }
    
    /**
     * Dois objetos AES são iguais se possuem a mesma chave
     */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AES other = (AES) obj;
		if (!Arrays.equals(getKey(), other.getKey()))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(getKey());
		return result;
	}
	
	@Override
	public String toString() {
		return CIPHER;
	}

	/**
     * Método para testes, gera um chave randômica a cada execução
     * @param args
     */
    public static void main(final String... args) {   	   	
    	
    	//Pode ser complementado pela alteração no geraIV para garantir reprodutibilidade em testes 
		final UUID chave = UUID.randomUUID();// UUID.fromString("00000000-0000-0000-0000-000000000000");
		System.out.println("Chave:      " + chave.toString());
		  
		final String entrada = "Teste 123";
		System.out.println("Entrada:    " + entrada);
				
		//AES x = new AES(chave);
		
		final String input = "Teste 123 456 789 012 345 678 901 234 567 890";
		//String y = x.encripta(input);

		
		for (int i = 0; i < 50; ++i) {
			final AES x = new AES(chave);
			final String y = x.encripta(input);
			System.out.format("Encriptado: %s\n", y);
			System.out.format("Decriptado: %s\n", x.decripta(y));
		}
		
		try {
			Thread.yield();
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

}
