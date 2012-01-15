/**
 * {iDart - Pharmacy dispensing tool for cronic diseases}
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 **/

package org.celllife.idart.misc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Map.Entry;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created on 31/07/2006
 * 
 * Class to encrypt and decrypt system properties from a file file format:
 * 
 * property_name_no_spaces property_no_spaces or
 * property_name_no_spaces=property_no_spaces
 * 
 * property_name should start with encrypted if the property is meant to be
 * encrypted / decrypted
 * 
 * Has a main method that encrypts the given file, used during installation by
 * izPack
 * 
 */

public class PropertiesEncrypter {

	private static String fubu = "[B@4aeb52";

	private static SecretKey blowfishKey;

	private static Cipher ecipher;

	private static Cipher dcipher;

	private static boolean debug;

	private SortedProperties props;

	public PropertiesEncrypter() {
		init();
	}

	public Properties getProperties() {
		return props;
	}

	/**
	 * Method main.
	 * 
	 * @param args
	 *            expecting the names of the input file and output file.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2)
			throw new IllegalArgumentException(
			"Expecting exactly two argument.");

		debug = true;
		PropertiesEncrypter pe = new PropertiesEncrypter();
		File input = pe.loadPropertiesFromFile(args[0]);
		pe.encryptProperties();
		pe.savePropertiesToFile(args[1]);
		input.delete();
	}

	/**
	 * Creates a new SystemPropertiesEncrypter, which can be used to encrypt or
	 * decrypt the given file
	 */
	private void init() {
		byte[] raw = fubu.getBytes();
		blowfishKey = new SecretKeySpec(raw, "Blowfish");
		try {
			ecipher = Cipher.getInstance("Blowfish");
			dcipher = Cipher.getInstance("Blowfish");
			ecipher.init(Cipher.ENCRYPT_MODE, blowfishKey);
			dcipher.init(Cipher.DECRYPT_MODE, blowfishKey);
		} catch (NoSuchPaddingException e) {
			System.out.println("EXCEPTION: NoSuchPaddingException");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("EXCEPTION: NoSuchAlgorithmException");
		} catch (InvalidKeyException e) {
			System.out.println("EXCEPTION: InvalidKeyException");
		}
	}

	public void decryptProperties() {
		processProperties(EncryptionMode.DECRYPT);
	}

	public void encryptProperties() {
		processProperties(EncryptionMode.ENCRYPT);
	}

	public File loadPropertiesFromFile(String fileName) throws IOException {
		File inFile = new File(fileName);

		InputStream in;
		try {
			in = new FileInputStream(inFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					"Input file does not exist or is a directory: "
					+ inFile.getAbsolutePath(), e);
		}
		props = loadProperties(in);
		return inFile;
	}

	public void loadPropertiesFromString(String properties) throws IOException {
		InputStream in = new ByteArrayInputStream(properties.getBytes());
		props = loadProperties(in);
	}

	/**
	 * Loads the properties from and InputStream
	 * 
	 * @param in
	 *            InputStream from which to load the properties
	 * @return the loaded properties
	 * @throws IOException
	 */
	private SortedProperties loadProperties(InputStream in) throws IOException {
		SortedProperties properties = new SortedProperties();
		properties.load(in);
		props = properties;
		return props;
	}

	private void processProperties(EncryptionMode mode) {
		for (Entry<Object, Object> e : props.entrySet()) {
			String key = (String) e.getKey();
			String value = (String) e.getValue();
			if (key.startsWith("encrypted")) {
				String processedValue = "";
				switch (mode) {
				case ENCRYPT:
					try {
						processedValue = encrypt(value);
					} catch (EncryptionException e1) {
						System.err.println("Failed to encrypt proptery: "
								+ value);
						e1.printStackTrace();
					}
					break;
				case DECRYPT:
					try {
						processedValue = decrypt(value);
					} catch (EncryptionException e1) {
						System.err.println("Failed to decrypt proptery: "
								+ value);
						e1.printStackTrace();
					}
					break;
				}
				if (debug) {
					System.out.println(key + "=" + processedValue);
				}
				props.setProperty(key, processedValue);
			} else {
				if (debug) {
					System.out.println(key + "=" + value);
				}
			}
		}
	}

	public void savePropertiesToFile(String fileName) throws IOException {
		File outFile = new File(fileName);
		OutputStream out;
		try {
			out = new FileOutputStream(outFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(
					"Output file does not exist or is a directory: "
					+ outFile.getAbsolutePath(), e);
		}
		props.store(out, "Encrypted properties generated by iDART.");
	}

	/**
	 * Takes a single String as an argument and returns an Encrypted version of
	 * that String.
	 * 
	 * @param str
	 *            String to be encrypted
	 * @return <code>String</code> Encrypted version of the provided String
	 * @throws EncryptionException
	 */
	private String encrypt(String str) throws EncryptionException {
		assert ecipher != null : "SystemPropertiesEncyrpter not initialised.";
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF8");

			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			String tmp = Base64.encodeBytes(enc);

			// Strip out all newline and linefeed characters
			return tmp.replaceAll("[\r,\n]", "");
		} catch (BadPaddingException e) {
			throw new EncryptionException("Error encrypting properties.", e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionException("Error encrypting properties.", e);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptionException("Error encrypting properties.", e);
		}
	}

	/**
	 * Takes a encrypted String as an argument, decrypts and returns the
	 * decrypted String.
	 * 
	 * @param str
	 *            Encrypted String to be decrypted
	 * @return <code>String</code> Decrypted version of the provided String
	 * @throws EncryptionException
	 */
	private String decrypt(String str) throws EncryptionException {
		assert dcipher != null : "SystemPropertiesEncyrpter not initialised.";
		try {
			// Decode base64 to get bytes
			byte[] dec = Base64.decode(str);

			// Decrypt
			byte[] utf8 = dcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF8");
		} catch (BadPaddingException e) {
			throw new EncryptionException("Error decrypting properties.", e);
		} catch (IllegalBlockSizeException e) {
			throw new EncryptionException("Error decrypting properties.", e);
		} catch (UnsupportedEncodingException e) {
			throw new EncryptionException("Error decrypting properties.", e);
		}
	}
}
