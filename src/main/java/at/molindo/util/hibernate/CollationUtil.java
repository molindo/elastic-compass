package at.molindo.util.hibernate;

import java.io.IOException;
import java.text.CollationKey;

import org.xml.sax.SAXException;

import at.molindo.mysqlcollations.MySqlCollator;
import at.molindo.mysqlcollations.MySqlCollatorFactory;

public abstract class CollationUtil {
	// swedish locale as used by MySQL by default
	private static final MySqlCollator DEFAULT_COLLATOR;

	static {
		try {
			final MySqlCollatorFactory factory = MySqlCollatorFactory.parseDefaultDirectory();
			DEFAULT_COLLATOR = factory.getDefaultCollator();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		} catch (final SAXException e) {
			throw new RuntimeException(e);
		}
	}

	private CollationUtil() {

	}

	public static boolean equalsIgnoreAccents(final String s1, final String s2) {
		return equals(s1, s2);
	}

	public static boolean equalsIgnoreCase(final String s1, final String s2) {
		return equals(s1, s2);
	}

	public static CollationKey getKeyIgnoreAccents(final String string) {
		return getKey(string);
	}

	public static CollationKey getKeyIgnoreCase(final String string) {
		return getKey(string);
	}

	public static boolean equals(final String s1, final String s2) {
		return DEFAULT_COLLATOR.equals(s1, s2);
	}

	public static CollationKey getKey(final String string) {
		return DEFAULT_COLLATOR.getCollationKey(string);
	}

	public static String normalize(final String string) {
		return DEFAULT_COLLATOR.normalize(string);
	}
}
