package de.vogel612.helper.data.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
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
    public static final String SINGLE_TRUTH_LOCALE = "";

    public static final String VALUE_NAME = "value";
    public static final String KEY_NAME = "name";
    public static final String ELEMENT_NAME = "data";
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

    /**
     * Creates a new <tt>data</tt>-entry for a given key with an empty value child attached
     *
     * @param key The key for the new element
     *
     * @return The element itself
     */
    public static Element createNewElement(String key) {
        Element newElement = new Element(ELEMENT_NAME);
        Element valueContainer = new Element(VALUE_NAME);
        valueContainer.setText("");

        newElement.setAttribute(KEY_NAME, key);
        newElement.addContent(valueContainer);
        return newElement;
    }

    /**
     * Grabs the <tt>value</tt> subelement of a resx <tt>data</tt>-entry with a specified key from a given {@link
     * Document}
     *
     * @param doc The document to search for the ELement
     * @param key The key of the associated <tt>data</tt>-entry
     *
     * @return The element, if it exists, null otherwise
     */
    public static Element getValueElement(Document doc, final String key) {
        VALUE_EXPRESSION.setVariable("key", key);
        return VALUE_EXPRESSION.evaluateFirst(doc);
    }

    /**
     * Build the filename from fileset and locale
     *
     * @param fileset The fileset the File belongs to
     * @param locale  The locale that's saved into that file
     *
     * @return The built filename as String
     */
    public static String fileNameString(final String fileset, final String locale) {
        return String.format(FILE_NAME_FORMAT, fileset, locale.isEmpty() ? "" : "." + locale);
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
