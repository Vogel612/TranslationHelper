package de.vogel612.helper.data.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.*;

/**
 * Created by vogel612 on 20.07.16.
 */
public class ResourceFileSerializer {

    private static final String ELEMENT_NAME = "data";
    private static final String KEY_NAME = "name";
    private static final String VALUE_NAME = "value";

    private static final XPathFactory X_PATH_FACTORY = XPathFactory.instance();
    private static final XPathExpression<Element> VALUE_EXPRESSION = X_PATH_FACTORY.compile("/*/"
            + ELEMENT_NAME + "[@" + KEY_NAME + "=$key]/"
            + VALUE_NAME, Filters.element(), Collections.singletonMap("key", ""));

    /**
     * Deserializes the given Document as resource-file into a Map of keys and values
     *
     * @param document
     *         The given document to deserialize
     *
     * @return A Map containing all keys and values from the given Document when processed as .resx file
     */
    public static Map<String, String> deserializeToMap(Document document) {
        final Map<String, String> result = new HashMap<>();
        document.getRootElement().getChildren(ELEMENT_NAME)
                .forEach(el -> {
                    final String key = el.getAttribute(KEY_NAME).getValue();
                    final String value = el.getChildText(VALUE_NAME);
                    result.put(key, value);
                });
        return result;
    }

    /**
     * Creates a new <tt>data</tt>-entry for a given key and a given value. Neither may be null
     *
     * @param key
     *         The key for the new element
     * @param value
     *         The value for the new element
     *
     * @return The element itself
     */
    public static Element createNewElement(final String key, final String value) {
        Objects.requireNonNull(key, "Key");
        Objects.requireNonNull(value, "value");

        Element newElement = new Element(ELEMENT_NAME);
        Element valueContainer = new Element(VALUE_NAME);
        valueContainer.addContent(value);

        newElement.setAttribute(KEY_NAME, key);
        newElement.addContent(valueContainer);
        return newElement;
    }

    /**
     * Grabs the <tt>value</tt> subelement of a resx <tt>data</tt>-entry with a specified key from a given {@link
     * Document}
     *
     * @param doc
     *         The document to search for the ELement
     * @param key
     *         The key of the associated <tt>data</tt>-entry
     *
     * @return The element, if it exists, null otherwise
     */
    public static Element getValueElement(final Document doc, final String key) {
        VALUE_EXPRESSION.setVariable("key", key);
        return VALUE_EXPRESSION.evaluateFirst(doc);
    }
}
