package com.jiguro.bettervia;

import android.content.Context;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class SecurePasswordHelper {

	private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int HASH_ITERATIONS = 10000;
	private static final int HASH_KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 16;

	private final Context context;

	public SecurePasswordHelper(Context context) {
		this.context = context;
	}

	private byte[] generateSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		new SecureRandom().nextBytes(salt);
		return salt;
	}

	private byte[] hashPassword(String password, byte[] salt) throws Exception {
		javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance(HASH_ALGORITHM);
		javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt,
				HASH_ITERATIONS, HASH_KEY_LENGTH);
		return factory.generateSecret(spec).getEncoded();
	}

	public String hash(String password) throws Exception {
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("密码不能为空");
		}

		byte[] salt = generateSalt();

		byte[] hash = hashPassword(password, salt);

		ByteBuffer buffer = ByteBuffer.allocate(salt.length + hash.length);
		buffer.put(salt);
		buffer.put(hash);

		return Base64.encodeToString(buffer.array(), Base64.NO_WRAP);
	}

	public boolean verify(String password, String storedHash) {
		if (password == null || password.isEmpty() || storedHash == null || storedHash.isEmpty()) {
			return false;
		}

		try {
		
			byte[] decoded = Base64.decode(storedHash, Base64.NO_WRAP);

			if (decoded.length != SALT_LENGTH + (HASH_KEY_LENGTH / 8)) {
				return false;
			}

			ByteBuffer buffer = ByteBuffer.wrap(decoded);
			byte[] salt = new byte[SALT_LENGTH];
			buffer.get(salt);

			byte[] storedHashBytes = new byte[HASH_KEY_LENGTH / 8];
			buffer.get(storedHashBytes);

			byte[] inputHash = hashPassword(password, salt);

			return slowEquals(inputHash, storedHashBytes);

		} catch (Exception e) {
			return false;
		}
	}

	private boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++) {
			diff |= a[i] ^ b[i];
		}
		return diff == 0;
	}
}
