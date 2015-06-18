package de.vogel612.helper.data;

public class Translation {
	private final String key;
	private final String rootValue;
	private String translation;
	
	public Translation(final String key, final String rootValue) {
		this(key, rootValue, "");
	}
	
	public Translation(final String key, final String rootValue, final String currentTranslation) {
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
		// Notify people??
		this.translation = translation;
	}
}
