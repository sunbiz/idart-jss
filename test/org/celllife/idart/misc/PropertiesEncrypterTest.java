package org.celllife.idart.misc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.celllife.idart.test.TestUtilities;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PropertiesEncrypterTest {

	@DataProvider(name = "stringProvider")
	public Object[][] stringProvider() {
		return new Object[][] {
				{ "test", "0NKSkvGY9DU=" },
				{ "jdbc:postgresql://nkosi.cell-life.org:5432/idart-3.1",
				"8JFcSjMJVY2gGLxUa/VcvkDeuiVPxcua6BB8S42zsWVYo9LfaA3RlPkieNoQRgEEFAQwVd/zzTI=" },
				{ "this is a long string with spaces",
				"OE/vdY/HfGJkJ28aqqDcstOUnMHwVrbLvJnIGgmT8H7ZFmSagiEjJg==" },
				{
					"this is an even longer     \n     string with     lots   \nof spaces and linebreaks",
				"OE/vdY/HfGL++XpQP05duUDCwxf7rOYEPsnLZEybAm7LZS5fZ1Ly8vfghEPD1Zg143Fh+JoAc7VMNrJvWipFmOc2okUWKnE3m2UcuKcNjKvZFmSagiEjJg==" },
				{ "this string @#/?$^^~\"'(0=90-(%64 has special characters",
				"Dln4uMtGfmVYe6tA8HKVOeIvAJa5IH1bEyITdtByuJdTc5HiAJ/ecHku0kJWOqUeCCly6zy9E0o=" }, };
	}

	@Test(dataProvider = "stringProvider")
	public void testEncrypt(String testString, String expected)
	throws InvocationTargetException {
		PropertiesEncrypter pe = new PropertiesEncrypter();

		String encrypted = (String) TestUtilities.invokeMethod(pe,
				PropertiesEncrypter.class, "encrypt",
				new Class[] { String.class }, new String[] { testString });
		Assert.assertEquals(encrypted, expected);
		Assert.assertFalse(encrypted.contains("\n"));

		String decrypted = (String) TestUtilities.invokeMethod(pe,
				PropertiesEncrypter.class, "decrypt",
				new Class[] { String.class }, new String[] { encrypted });

		Assert.assertEquals(decrypted, testString);
	}

	@DataProvider(name = "propertiesProvider")
	public Object[][] propertiesProvider() {
		String unencrypted = "a=1\n" + "a3=j\n" + "b=2\n"
		+ "encryptedkey1=test\n" + "encryptedkey2=23lkj23lkj234\n"
		+ "plainkey1=3.1.7.3633-\n" + "plainkey2=test\n"
		+ "plainkey3=long string   with  spaces\n";

		String encrypted = "a=1\n" + "a3=j\n" + "b=2\n"
		+ "encryptedkey1=0NKSkvGY9DU\\=\n"
		+ "encryptedkey2=c2LV2yYm2he0wR+B2q1sRA\\=\\=\n"
		+ "plainkey1=3.1.7.3633-\n" + "plainkey2=test\n"
		+ "plainkey3=long string   with  spaces\n";

		return new Object[][] {
				{ unencrypted, encrypted, EncryptionMode.ENCRYPT },
				{ encrypted, unencrypted, EncryptionMode.DECRYPT } };
	}

	@Test(dataProvider = "propertiesProvider")
	public void testEncryptSystemProperties(String inString,
			String outString,
			EncryptionMode mode) throws IOException {

		OutputStream out = new ByteArrayOutputStream();
		PropertiesEncrypter pe = new PropertiesEncrypter();
		pe.loadPropertiesFromString(inString);
		switch (mode) {
		case ENCRYPT:
			pe.encryptProperties();
			break;
		case DECRYPT:
			pe.decryptProperties();
			break;
		}
		Properties p = pe.getProperties();
		p.store(out, "");
		String actual = out.toString();

		// compare line by line
		String[] actualArray = actual.split("[\n\r]+");
		String[] expectedArray = outString.split("[\n\r]+");
		int j = 0;
		for (int i = 0; i < actualArray.length; i++) {
			if (actualArray[i].startsWith("#")) {
				continue;
			}
			Assert.assertEquals(actualArray[i], expectedArray[j]);
			j++;
		}

	}

	public static void main(String[] args) {
		String string = "#\r\n#Wed Mar 24 17:41:43 CAT 2010\r\na=1\r\na3=j\r\nb=2\r\nencryptedkey1=0NKSkvGY9DU\\=\r\nencryptedkey2=c2LV2yYm2he0wR+B2q1sRA\\=\\=\r\nplainkey1=3.1.7.3633-\r\nplainkey2=test\r\nplainkey3=long string   with  spaces\r\n";
		String[] split = string.split("[\n\r]+");
		for (String string1 : split) {
			System.out.println("--" + string1 + "--");
		}

		System.out.println("++" + string.replaceAll("#.*[\n\r]*", "") + "++");
	}
}
