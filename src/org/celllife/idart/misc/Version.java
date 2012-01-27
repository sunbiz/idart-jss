package org.celllife.idart.misc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * This class represents a plug-in version identifier.
 * <br>
 * @version $Id: Version.java,v 1.3 2007/01/20 15:01:54 ddimon Exp $
 */
public final class Version implements Serializable, Comparable<Version> {
	private static final long serialVersionUID = -3054349171116917643L;

	/**
	 * Version identifier parts separator.
	 */
	public static final char SEPARATOR = '.';

	/**
	 * Parses given string as version identifier. All missing parts will be
	 * initialized to 0 or empty string. Parsing starts from left side of the
	 * string.
	 * @param str version identifier as string
	 * @return version identifier object
	 */
	public static Version parse(final String str) {
		Version result = new Version();
		result.parseString(str);
		return result;
	}

	private transient int major;
	private transient int minor;
	private transient int revision;
	private transient String name;
	private transient String asString;

	private Version() {
		// no-op
	}

	private void parseString(final String str) {
		major = 0;
		minor = 0;
		revision = 0;
		name = ""; //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(str, "" + SEPARATOR, false); //$NON-NLS-1$
		// major segment
		if (!st.hasMoreTokens())
			return;
		String token = st.nextToken();
		try {
			major = Integer.parseInt(token, 10);
		} catch (NumberFormatException nfe) {
			name = token;
			while (st.hasMoreTokens()) {
				name += st.nextToken();
			}
			return;
		}
		// minor segment
		if (!st.hasMoreTokens())
			return;
		token = st.nextToken();
		try {
			minor = Integer.parseInt(token, 10);
		} catch (NumberFormatException nfe) {
			name = token;
			while (st.hasMoreTokens()) {
				name += st.nextToken();
			}
			return;
		}
		// revision segment
		if (!st.hasMoreTokens())
			return;
		token = st.nextToken();
		try {
			revision = Integer.parseInt(token, 10);
		} catch (NumberFormatException nfe) {
			name = token;
			while (st.hasMoreTokens()) {
				name += st.nextToken();
			}
			return;
		}
		// name segment
		if (st.hasMoreTokens()) {
			name = st.nextToken();
			while (st.hasMoreTokens()) {
				name += st.nextToken();
			}
		}
	}

	/**
	 * Creates version identifier object from given parts. No validation
	 * performed during object instantiation, all values become parts of version
	 * identifier as they are.
	 * 
	 * @param aMajor
	 *            major version number
	 * @param aMinor
	 *            minor version number
	 * @param aRevision
	 *            revision number
	 * @param aName
	 *            revision name, <code>null</code> value becomes empty string
	 */
	public Version(final int aMajor, final int aMinor, final int aRevision,
			final String aName) {
		major = aMajor;
		minor = aMinor;
		revision = aRevision;
		name = (aName == null) ? "" : aName; //$NON-NLS-1$
	}

	/**
	 * @return revision number
	 */
	public int getRevision() {
		return revision;
	}

	/**
	 * @return major version number
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return minor version number
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @return revision name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Compares two version identifiers to see if this one is greater than or
	 * equal to the argument.
	 * <p>
	 * A version identifier is considered to be greater than or equal if its
	 * major component is greater than the argument major component, or the
	 * major components are equal and its minor component is greater than the
	 * argument minor component, or the major and minor components are equal and
	 * its revision component is greater than the argument revision component,
	 * or all components are equal.
	 * </p>
	 * 
	 * @param other
	 *            the other version identifier
	 * @return <code>true</code> if this version identifier is compatible with
	 *         the given version identifier, and <code>false</code> otherwise
	 */
	public boolean isGreaterOrEqualTo(final Version other) {
		if (other == null)
			return false;
		if (major > other.major)
			return true;
		if ((major == other.major) && (minor > other.minor))
			return true;
		if ((major == other.major) && (minor == other.minor)
				&& (revision > other.revision))
			return true;
		if ((major == other.major) && (minor == other.minor)
				&& (revision == other.revision)
				&& name.equalsIgnoreCase(other.name))
			return true;
		return false;
	}

	/**
	 * Compares two version identifiers for compatibility.
	 * <p>
	 * A version identifier is considered to be compatible if its major
	 * component equals to the argument major component, and its minor component
	 * is greater than or equal to the argument minor component. If the minor
	 * components are equal, than the revision component of the version
	 * identifier must be greater than or equal to the revision component of the
	 * argument identifier.
	 * </p>
	 * 
	 * @param other
	 *            the other version identifier
	 * @return <code>true</code> if this version identifier is compatible with
	 *         the given version identifier, and <code>false</code> otherwise
	 */
	public boolean isCompatibleWith(final Version other) {
		if (other == null)
			return false;
		if (major != other.major)
			return false;
		if (minor > other.minor)
			return true;
		if (minor < other.minor)
			return false;
		if (revision >= other.revision)
			return true;
		return false;
	}

	/**
	 * Compares two version identifiers for equivalency.
	 * <p>
	 * Two version identifiers are considered to be equivalent if their major
	 * and minor components equal and are at least at the same revision level as
	 * the argument.
	 * </p>
	 * 
	 * @param other
	 *            the other version identifier
	 * @return <code>true</code> if this version identifier is equivalent to the
	 *         given version identifier, and <code>false</code> otherwise
	 */
	public boolean isEquivalentTo(final Version other) {
		if (other == null)
			return false;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		if (revision >= other.revision)
			return true;
		return false;
	}

	/**
	 * Compares two version identifiers for order using multi-decimal
	 * comparison.
	 *
	 * @param other the other version identifier
	 * @return <code>true</code> if this version identifier
	 *         is greater than the given version identifier, and
	 *         <code>false</code> otherwise
	 */
	public boolean isGreaterThan(final Version other) {
		if (other == null)
			return false;
		if (major > other.major)
			return true;
		if (major < other.major)
			return false;
		if (minor > other.minor)
			return true;
		if (minor < other.minor)
			return false;
		if (revision > other.revision)
			return true;
		return false;

	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Version))
			return false;
		Version other = (Version) obj;
		if ((major != other.major) || (minor != other.minor)
				|| (revision != other.revision)
				|| !name.equalsIgnoreCase(other.name))
			return false;
		return true;
	}

	/**
	 * Returns the string representation of this version identifier.
	 * The result satisfies
	 * <code>version.equals(new Version(version.toString()))</code>.
	 * @return the string representation of this version identifier
	 */
	@Override
	public String toString() {
		if (asString == null) {
			asString = "" + major + SEPARATOR + minor + SEPARATOR + revision //$NON-NLS-1$
			+ (name.length() == 0 ? "" : SEPARATOR + name); //$NON-NLS-1$
		}
		return asString;
	}

	/**
	 * @param obj version to compare this instance with
	 * @return comparison result
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final Version obj) {
		if (equals(obj))
			return 0;
		if (major != obj.major)
			return major - obj.major;
		if (minor != obj.minor)
			return minor - obj.minor;
		if (revision != obj.revision)
			return revision - obj.revision;
		return name.toLowerCase(Locale.ENGLISH).compareTo(
				obj.name.toLowerCase(Locale.ENGLISH));
	}

	// Serialization related stuff.

	private void writeObject(final ObjectOutputStream out) throws IOException {
		out.writeUTF(toString());
	}

	private void readObject(final ObjectInputStream in) throws IOException {
		parseString(in.readUTF());
	}
}
