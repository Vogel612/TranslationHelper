package de.vogel612.helper.data;

import de.vogel612.helper.data.util.ResourceFileSerializer;
import org.jdom2.Element;

import javafx.beans.value.ObservableValueBase;

/**
 * <p> A simple data holder class to organize Translations. This class exposes an immutable {@link #key},
 * an immutable {@link #locale} and  a
 * {@link #value}. For simplicity it allows changing the translation, instead of requiring the creation of a
 * new instance </p>
 * <p>To integrate better with JavaFX it also extends {@link ObservableValueBase ObservableValueBase&lt;String&gt; }</p>
 * @author vogel612
 */
public class Translation extends ObservableValueBase<String> {
    /**
     * The Key identifying the Translation in the resx file. Immutable for rather obvious purposes
     */
    private final String key;
    /**
     * The locale, which basically identifies the .resx file the Translation represents.
     */
    private final String locale;
    /**
     * The Value of a Translation.
     */
    private String value;

    /**
     * Creates a new translation from the necessary information
     *
     * @param value  The current value, which is at the same time the preliminary translation
     * @param locale The locale that this translation represents.
     * @param key    The key to use for the translation
     */
    public Translation(final String locale, final String key, final String value) {
        this.locale = locale;
        this.key = key;
        this.value = value;
    }

    /**
     * Creates a new translation from a DOM element in a resx file
     *
     * @param locale The locale that this translation represents
     * @param el     The DOM element representing a Translation
     */
    public Translation(final String locale, final Element el) {
        this.key = el.getAttribute(ResourceFileSerializer.KEY_NAME).getValue();
        this.value = el.getChildText(ResourceFileSerializer.VALUE_NAME);
        this.locale = locale;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(final String newValue) {
        value = newValue;
    }

    @Override
    public String toString() {
        return "Translation [key=" + key + ", value=" + value + ", locale=" + locale + "]";
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
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Translation other = (Translation) obj;
        if (key == null) {
            if (other.key != null) { return false; }
        } else if (!key.equals(other.key)) { return false; }
        if (value == null) {
            if (other.value != null) { return false; }
        } else if (!value.equals(other.value)) { return false; }
        return true;
    }

    public String getLocale() {
        return locale;
    }
}
