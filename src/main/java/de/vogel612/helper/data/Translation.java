package de.vogel612.helper.data;

/**
 * <p>
 * A simple data holder class to organize Translations. This class exposes an
 * immutable {@link #key}, an immutable {@link #rootValue} and a
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
	 * The RootValue of a Translation, meaning the "original language".
	 * Immutable since we're not here to change the original UI
	 */
	private final String rootValue;
	/**
	 * The actual Translation "Value".
	 */
	private String translation;

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
	public Translation(final String key, final String rootValue) {
		this(key, rootValue, rootValue);
	}

	public Translation(final String key, final String rootValue,
			final String currentTranslation) {
		this.key = key;
		this.rootValue = rootValue;
		this.setTranslation(currentTranslation);
	}

	public String getKey() {
		return key;
	}

	public String getRootValue() {
		return rootValue;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	@Override
	public String toString() {
		return "Translation [key=" + key + ", rootValue=" + rootValue
				+ ", translation=" + translation + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((rootValue == null) ? 0 : rootValue.hashCode());
		result = prime * result
				+ ((translation == null) ? 0 : translation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
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
		if (rootValue == null) {
			if (other.rootValue != null)
				return false;
		} else if (!rootValue.equals(other.rootValue))
			return false;
		if (translation == null) {
			if (other.translation != null)
				return false;
		} else if (!translation.equals(other.translation))
			return false;
		return true;
	}

}
