package tr.com.realrioden.message_room_application.service;

import javax.crypto.SecretKey;

public interface EncryptionService {

	SecretKey generateKey(String method, int keyBitSize) throws Exception;
	byte[] createInitializationVector(int bitSize);
	String encryption(String algorithm, SecretKey key, String plainText, byte[] initializationVector) throws Exception;
	String decryption(String algorithm, SecretKey key, String cipherText, byte[] initializationVector) throws Exception;
}
