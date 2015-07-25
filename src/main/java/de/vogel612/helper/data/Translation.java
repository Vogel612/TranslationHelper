package de.vogel612.helper.data;

/**
 * <p>
 * A simple data holder class to organize Translations. This class exposes an
 * immutable {@link #key}, an immutable {@link #value} and a
 * {@link #translation} for simplicity it allows changing the translation, which
 * may be removed later, depending on how it works out.
 * </p>
 *
 * @author vogel612<<a href="mailto:vogel612@gmx.de">vogel612@gmx.de</a>>
 */
public class Translation {
	/**
	 * The Key identifying the Translation in the resx file. Immutable for
	 * rather obvious purposes
	 */
	private final String key;
	/**
	 * The Value of a Translation.
	 */
	private String value;

	public static final String ELEMENT_NAME = "data";
	public static final String KEY_NAME = "name";
	public static final String VALUE_NAME = "value";

	/**
	 * Creates a new translation, where the translation is equal to the
	 * "root value"
	 *
	 * @param key
	 *            the key to use for the translation
	 * @param rootValue
	 *            the "root value", which is at the same time the preliminary
	 *            translation
	 */
	public Translation(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setValue(final String newValue) {
		value = newValue;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Translation [key=" + key + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Translation other = (Translation) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
