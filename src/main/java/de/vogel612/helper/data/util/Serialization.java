package de.vogel612.helper.data.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Objects;

/**
 * Created by vogel612 on 14.07.16.
 */
public class Serialization {

    public static final XMLOutputter XML_PRETTY_PRINT = new XMLOutputter(Format.getPrettyFormat());
    private static final XPathFactory X_PATH_FACTORY = XPathFactory.instance();
    public static final String ELEMENT_NAME = "data";
    public static final String KEY_NAME = "name";
    public static final String VALUE_NAME = "value";
    private static final XPathExpression<Element> VALUE_EXPRESSION = X_PATH_FACTORY.compile("/*/"
            + ELEMENT_NAME + "[@" + KEY_NAME + "=$key]/"
            + VALUE_NAME, Filters.element(), Collections.singletonMap("key", ""));

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

    /**
     * Deserializes the XML document at the given path into a Document
     * @param path
     * @return
     */
    public static Document parseFile(final Path path) {
        final Path xmlFile = path.getFileName();
        final SAXBuilder documentBuilder = new SAXBuilder();

        final Document doc;
        try {
            doc = documentBuilder.build(path.toFile());
            return doc;
        } catch (JDOMException e) {
            throw new IllegalStateException("Unable to parse " + xmlFile, e);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to read" + xmlFile, e);
        }
    }

    public static void serializeDocument(final Document doc, final Path file) throws IOException {
        try (OutputStream output = Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            Serialization.XML_PRETTY_PRINT.output(doc, output);
        }
    }
}
