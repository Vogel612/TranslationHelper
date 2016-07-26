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

    /**
     * Deserializes the XML document at the given path into a Document
     *
     * @param file
     *         The Path where the Document is to be retrieved from.
     *
     * @return a document instance built from the file at the given path
     *
     * @throws IOException
     *         When reading the file failed
     * @throws IllegalStateException
     *         When the file could not be parsed into a document
     */
    public static Document parseFile(final Path file) throws IOException {
        final Path xmlFile = file.getFileName();
        final SAXBuilder documentBuilder = new SAXBuilder();

        try {
            return documentBuilder.build(file.toFile());
        } catch (JDOMException e) {
            throw new IllegalStateException("Unable to parse " + xmlFile, e);
        }
    }

    /**
     * Serializes the given JDOM Document to the given Path
     *
     * @param doc
     *         The document to serialize.
     * @param file
     *         The path to serialize the document to
     *
     * @throws IOException
     *         When serializing the Document failed because of an IOException
     */
    public static void serializeDocument(final Document doc, final Path file) throws IOException {
        try (OutputStream output = Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            Serialization.XML_PRETTY_PRINT.output(doc, output);
        }
    }
}