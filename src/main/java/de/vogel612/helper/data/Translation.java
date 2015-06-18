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

	public Translation(final String key, final String rootValue) {
		this(key, rootValue, "");
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
}
