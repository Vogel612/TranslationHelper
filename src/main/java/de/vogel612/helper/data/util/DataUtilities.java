package de.vogel612.helper.data.util;

import de.vogel612.helper.data.FilesetOverviewModel;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Centralizes access to data processing for {@link FilesetOverviewModel} and other classes that
 * benefit from the abstraction
 */
public class DataUtilities {

    private static final String EMPTY_RESOURCE_FILE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<root>\n" +
            "  <xsd:schema id=\"root\" xmlns=\"\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:msdata=\"urn:schemas-microsoft-com:xml-msdata\">\n" +
            "    <xsd:import namespace=\"http://www.w3.org/XML/1998/namespace\" />\n" +
            "    <xsd:element name=\"root\" msdata:IsDataSet=\"true\">\n" +
            "      <xsd:complexType>\n" +
            "        <xsd:choice maxOccurs=\"unbounded\">\n" +
            "          <xsd:element name=\"metadata\">\n" +
            "            <xsd:complexType>\n" +
            "              <xsd:sequence>\n" +
            "                <xsd:element name=\"value\" type=\"xsd:string\" minOccurs=\"0\" />\n" +
            "              </xsd:sequence>\n" +
            "              <xsd:attribute name=\"name\" use=\"required\" type=\"xsd:string\" />\n" +
            "              <xsd:attribute name=\"type\" type=\"xsd:string\" />\n" +
            "              <xsd:attribute name=\"mimetype\" type=\"xsd:string\" />\n" +
            "              <xsd:attribute ref=\"xml:space\" />\n" +
            "            </xsd:complexType>\n" +
            "          </xsd:element>\n" +
            "          <xsd:element name=\"assembly\">\n" +
            "            <xsd:complexType>\n" +
            "              <xsd:attribute name=\"alias\" type=\"xsd:string\" />\n" +
            "              <xsd:attribute name=\"name\" type=\"xsd:string\" />\n" +
            "            </xsd:complexType>\n" +
            "          </xsd:element>\n" +
            "          <xsd:element name=\"data\">\n" +
            "            <xsd:complexType>\n" +
            "              <xsd:sequence>\n" +
            "                <xsd:element name=\"value\" type=\"xsd:string\" minOccurs=\"0\" msdata:Ordinal=\"1\" />\n" +
            "                <xsd:element name=\"comment\" type=\"xsd:string\" minOccurs=\"0\" msdata:Ordinal=\"2\" />\n" +
            "              </xsd:sequence>\n" +
            "              <xsd:attribute name=\"name\" type=\"xsd:string\" use=\"required\" msdata:Ordinal=\"1\" />\n" +
            "              <xsd:attribute name=\"type\" type=\"xsd:string\" msdata:Ordinal=\"3\" />\n" +
            "              <xsd:attribute name=\"mimetype\" type=\"xsd:string\" msdata:Ordinal=\"4\" />\n" +
            "              <xsd:attribute ref=\"xml:space\" />\n" +
            "            </xsd:complexType>\n" +
            "          </xsd:element>\n" +
            "          <xsd:element name=\"resheader\">\n" +
            "            <xsd:complexType>\n" +
            "              <xsd:sequence>\n" +
            "                <xsd:element name=\"value\" type=\"xsd:string\" minOccurs=\"0\" msdata:Ordinal=\"1\" />\n" +
            "              </xsd:sequence>\n" +
            "              <xsd:attribute name=\"name\" type=\"xsd:string\" use=\"required\" />\n" +
            "            </xsd:complexType>\n" +
            "          </xsd:element>\n" +
            "        </xsd:choice>\n" +
            "      </xsd:complexType>\n" +
            "    </xsd:element>\n" +
            "  </xsd:schema>\n" +
            "  <resheader name=\"resmimetype\">\n" +
            "    <value>text/microsoft-resx</value>\n" +
            "  </resheader>\n" +
            "  <resheader name=\"version\">\n" +
            "    <value>2.0</value>\n" +
            "  </resheader>\n" +
            "  <resheader name=\"reader\">\n" +
            "    <value>System.Resources.ResXResourceReader, System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089</value>\n" +
            "  </resheader>\n" +
            "  <resheader name=\"writer\">\n" +
            "    <value>System.Resources.ResXResourceWriter, System.Windows.Forms, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089</value>\n" +
            "  </resheader>\n" +
            "</data>";

    public static final String FALLBACK_LOCALE = "";

    private static final String FILE_NAME_FORMAT = "%s%s.resx";
    private static final String FILENAME_REGEX = "^([^.]*)(\\.[a-z]{2}(?:-[a-z]{2})?)?\\.resx$";
    private static final Pattern FILENAME_PATTERN = Pattern.compile(FILENAME_REGEX,
            Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);
    private static final String FILESET_REGEX = "%s(\\.[a-zA-Z]{2}(?:-[a-zA-Z]{2})?)?\\.resx";

    /**
     * Parses the Language-Locale combination out of a given filename
     *
     * @param path
     *         The file to get the language-locale option out of
     *
     * @return The Language-Locale for the given file
     */
    public static String getFileLocale(final Path path) {
        final Matcher localeMatcher = FILENAME_PATTERN.matcher(path.getFileName().toString());
        if (localeMatcher.find()) { // should always be true, since we check beforehand
            return localeMatcher.group(2) == null
                    ? FALLBACK_LOCALE
                    : localeMatcher.group(2).substring(1);
        }
        throw new IllegalArgumentException("Argument was not a conform resx file");
    }

    /**
     * Build the filename from fileset and locale
     *
     * @param fileset
     *         The fileset the File belongs to
     * @param locale
     *         The locale that's saved into that file
     *
     * @return The built filename as String
     */
    public static String fileNameString(final String fileset, final String locale) {
        return String.format(FILE_NAME_FORMAT, fileset, locale.isEmpty() ? "" : "." + locale);
    }

    /**
     * Streams all files under a given directory that belong to a given fileset
     *
     * @param currentPath
     *         The Path to search the files under
     * @param currentFileset
     *         The fileset the searched files belong to
     *
     * @return A {@link Stream<Path>} consisting of all files in the fileset who are directly at the given path
     *
     * @throws IOException
     *         In case the directory is not accessible
     */
    public static Stream<Path> streamFileset(Path currentPath, String currentFileset) throws IOException {
        return Files.find(currentPath, 1,
                // build our own matcher for filenames in the set!
                (path, properties) -> path.getFileName().toString().matches(String.format(FILESET_REGEX, currentFileset)),
                FileVisitOption.FOLLOW_LINKS);
    }

    /**
     * Gets the fileset identifier from a given file. The fileset identifier is the part of a resx-file's filename that
     * comes before the first dot. We assume that the resx-file's filename follows the pattern: <tt>[fileset
     * identifier].{locale}.resx</tt>
     *
     * @param file
     *         The file we want to get the identifier to.
     *
     * @return The fileset identifier, if present. Otherwise <tt>IllegalArgumentException</tt> is thrown.
     */
    public static String getFileIdentifier(Path file) {
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(file.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            return filesetMatcher.group(1); // group is not optional
        } else {
            throw new IllegalArgumentException("The resx file does not match our permissive regex");
        }
    }

    /**
     * Creates an empty resource file at the given path, assuming the file does not exist already
     *
     * @param path
     *         The Path where the resource file needs to be created.
     */
    public static void createEmptyResourceFile(Path path) {
        if (path.toFile().exists()) {
            return;
        }
        try {
            Files.createFile(path);
            Files.write(path, EMPTY_RESOURCE_FILE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
