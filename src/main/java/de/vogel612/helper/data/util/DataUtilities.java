package de.vogel612.helper.data.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Centralizes access to data processing for {@link de.vogel612.helper.data.OverviewModel} and other classes that
 * benefit from the abstraction
 */
// FIXME investigate moving this here
// FIXME write unit-tests
public class DataUtilities {
    public static final String VALUE_NAME = "value";
    public static final String KEY_NAME = "name";
    public static final String SINGLE_TRUTH_LOCALE = "";
    public static final String ELEMENT_NAME = "data";
    public static final XMLOutputter XML_PRETTY_PRINT = new XMLOutputter(Format.getPrettyFormat());
    private static final XPathFactory X_PATH_FACTORY = XPathFactory.instance();
    public static final XPathExpression<Element> VALUE_EXPRESSION = X_PATH_FACTORY.compile("/*/"
      + ELEMENT_NAME + "[@" + KEY_NAME + "=$key]/"
      + VALUE_NAME, Filters.element(), Collections.singletonMap("key", ""));
    public static final String FILE_NAME_FORMAT = "%s%s.resx";
    private static final String FILENAME_REGEX = "^.*?([a-z]*)\\.?([a-z]{2}(?:-[a-z]{2})?)?\\.resx$";
    public static final Pattern FILENAME_PATTERN = Pattern.compile(FILENAME_REGEX,
      Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
    public static final String FILESET_REGEX = "%s\\.?([a-zA-Z]{2}(?:-[a-zA-Z]{2})?)?\\.resx";

    /**
     * Parses the Language-Locale combination out of a given filename
     *
     * @param path The file to get the language-locale option out of
     *
     * @return The Language-Locale for the given file
     */
    public static String parseFileName(final Path path) {
        final Matcher localeMatcher = FILENAME_PATTERN.matcher(path.getFileName().toString());
        if (localeMatcher.find()) { // should always be true, since we check beforehand
            return localeMatcher.group(2) == null
                   ? SINGLE_TRUTH_LOCALE
                   : localeMatcher.group(2);
        }
        throw new IllegalArgumentException("Argument was not a conform resx file");
    }

    public static Element createNewElement(String key) {
        Element newElement = new Element(ELEMENT_NAME);
        Element valueContainer = new Element(VALUE_NAME);
        valueContainer.setText("");

        newElement.setAttribute(KEY_NAME, key);
        newElement.addContent(valueContainer);
        return newElement;
    }

    public static Element getValueElement(Document doc, final String key) {
        VALUE_EXPRESSION.setVariable("key", key);
        return VALUE_EXPRESSION.evaluateFirst(doc);
    }

    public static String fileNameString(final String fileset, final String locale) {
        return String.format(FILE_NAME_FORMAT, fileset, locale.isEmpty() ? "" : "." + locale.toLowerCase());
    }

    /**
     * Streams all files under a given directory that belong to a given fileset
     *
     * @param currentPath    The Path to search the files under
     * @param currentFileset The fileset the searched files belong to
     *
     * @return A {@link Stream<Path>} consisting of all files in the fileset who are directly at the given path
     *
     * @throws IOException In case the directory is not accessible
     */
    public static Stream<Path> streamFileset(Path currentPath, String currentFileset) throws IOException {
        return Files.find(currentPath, 1,
          // build our own matcher for filenames in the set!
          (path, properties) -> path.getFileName().toString().matches(String.format(FILESET_REGEX, currentFileset)),
          FileVisitOption.FOLLOW_LINKS);
    }
}
