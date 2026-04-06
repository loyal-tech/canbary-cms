package com.adopt.apigw.modules.Voucher.module;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class SimpleEncryptor {

	public String encrypt(final String plainKey) {
		return new String(Base64.getEncoder().encode(plainKey.getBytes()));
	}

	public String decrypt(final String encryptedKey) {
		return new String(Base64.getDecoder().decode(encryptedKey));
	}
}
