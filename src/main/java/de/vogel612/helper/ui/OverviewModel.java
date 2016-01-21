package de.vogel612.helper.ui;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import de.vogel612.helper.data.Translation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Overview model. Provides in-memory caching, parsing and writing of resx file data for Rubberduck files
 */
public class OverviewModel {

    public static final String VALUE_NAME = "value";
    public static final String KEY_NAME = "name";
    private static final String SINGLE_TRUTH_LOCALE = "";

    private static final String ELEMENT_NAME = "data";
    private static final XMLOutputter XML_PRETTY_PRINT = new XMLOutputter(Format.getPrettyFormat());
    private static final XPathFactory X_PATH_FACTORY = XPathFactory.instance();
    private static final XPathExpression<Element> VALUE_EXPRESSION = X_PATH_FACTORY.compile("/*/"
      + ELEMENT_NAME + "[@" + KEY_NAME + "=$key]/"
      + VALUE_NAME, Filters.element(), Collections.singletonMap("key", ""));

    private static final String FILE_NAME_FORMAT = "%s%s.resx";
    private static final String FILENAME_REGEX = "^.*?([a-z]*)\\.?([a-z]{2}(?:-[a-z]{2})?)?\\.resx$";
    private static final String FILESET_REGEX = "%s\\.?([a-zA-Z]{2}(?:-[a-zA-Z]{2})?)?\\.resx";
    private static final Pattern FILENAME_PATTERN = Pattern.compile(FILENAME_REGEX,
      Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);

    private final Set<Runnable> parseCompletionListeners = new HashSet<>();
    private final Map<String, Document> translations = new HashMap<>();

    private final AtomicBoolean saved = new AtomicBoolean(true);
    private Path currentPath;
    private String currentFileset;

    private static String parseFileName(final Path path) {
        final Matcher localeMatcher = FILENAME_PATTERN.matcher(path.getFileName().toString());
        if (localeMatcher.find()) { // should always be true, since we check beforehand
            return localeMatcher.group(2) == null
                   ? SINGLE_TRUTH_LOCALE
                   : localeMatcher.group(2);
        }
        throw new IllegalArgumentException("Argument was not a conform resx file");
    }

    public void addParseCompletionListener(Runnable listener) {
        parseCompletionListeners.add(listener);
    }

    //FIXME load files and not from a folder. We can't rely on FILENAME_REGEX
    public void loadResxFileset(final Path file) throws IOException {
        if (file.toFile().isDirectory()) {
            throw new IllegalArgumentException("We need to work with an actual file");
        }

        this.currentPath = file.getParent();
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(file.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            this.currentFileset = filesetMatcher.group(1); // group is not optional
        } else {
            throw new IllegalArgumentException("The resx file does not match our permissive regex");
        }


        translations.clear();
        try (Stream<Path> resxFiles = Files.find(currentPath,
          1,
          // build our own matcher for filenames in the set!
          (path, properties) -> path.getFileName().toString().matches(String.format(FILESET_REGEX, currentFileset)),
          FileVisitOption.FOLLOW_LINKS)) {
            translations.putAll(resxFiles.collect(Collectors.toMap(
                  OverviewModel::parseFileName, this::parseFile)
              ));
        }
        normalizeDocuments();
        parseCompletionListeners.forEach(Runnable::run);
    }

    private void normalizeDocuments() {
        final Set<String> singleTruth = translations
          .get(SINGLE_TRUTH_LOCALE)
          .getRootElement()
          .getChildren(ELEMENT_NAME)
          .stream()
          .map(el -> el.getAttribute(KEY_NAME).getValue())
          .collect(Collectors.toSet());

        translations.values().forEach(
          doc -> normalizeDocument(doc, singleTruth));
        saved.lazySet(false);
    }

    private void normalizeDocument(final Document doc, final Set<String> singleTruth) {
        final List<Element> localeElements = doc.getRootElement().getChildren(ELEMENT_NAME);
        Set<String> localeKeys = new HashSet<>();

        // remove keys not present in the Single truth
        for (Iterator<Element> it = localeElements.iterator(); it.hasNext(); ) {
            final Element el = it.next();
            if (!singleTruth.contains(el.getAttribute(KEY_NAME).getValue())) {
                it.remove();
                continue;
            }
            localeKeys.add(el.getAttribute(KEY_NAME).getValue());
        }

        singleTruth.stream()
          .filter(key -> !localeKeys.contains(key))
          .map(OverviewModel::createNewElement)
          .forEach(doc.getRootElement()::addContent);
    }

    private static Element createNewElement(String key) {
        Element newElement = new Element(ELEMENT_NAME);
        Element valueContainer = new Element(VALUE_NAME);
        valueContainer.setText("");

        newElement.setAttribute(KEY_NAME, key);
        newElement.addContent(valueContainer);
        return newElement;
    }

    private Document parseFile(final Path path) {
        final Path xmlFile = path.getFileName();
        final SAXBuilder documentBuilder = new SAXBuilder();

        final Document doc;
        try {
            doc = documentBuilder.build(path.toFile());
            return doc;
        } catch (JDOMException e) {
            // FIXME: Get the presenter out of error-handling!
            // presenter.onException(e, "Unspecified Parsing error");
            throw new IllegalStateException("Unable to parse " + xmlFile, e);
        } catch (IOException e) {
            // presenter.onException(e, "Unspecified I/O Error");
            throw new UncheckedIOException("Unable to read" + xmlFile, e);
        }
    }

    public List<Translation> getTranslations(final String locale) {
        Document document = translations.get(locale);
        final List<Element> translationElements = document.getRootElement()
          .getChildren(ELEMENT_NAME);

        return translationElements.stream()
          .map(el -> new Translation(locale, el))
          .sorted(Comparator.comparing(Translation::getKey))
          .collect(Collectors.toList());
    }

    public void updateTranslation(final String locale, final String key,
      final String newTranslation) {
        Element translationToUpdate = getValueElement(locale, key);
        translationToUpdate.setText(newTranslation);
    }

    private Element getValueElement(final String locale, final String key) {
        VALUE_EXPRESSION.setVariable("key", key);
        return VALUE_EXPRESSION.evaluateFirst(translations.get(locale));
    }

    public void saveAll() throws IOException {
        for (Map.Entry<String, Document> entry : translations.entrySet()) {
            final Path outFile = currentPath.resolve(fileNameString(entry
              .getKey()));
            try (OutputStream outStream = Files.newOutputStream(outFile,
              StandardOpenOption.TRUNCATE_EXISTING,
              StandardOpenOption.WRITE)) {
                XML_PRETTY_PRINT.output(entry.getValue(), outStream);
                saved.lazySet(true);
            }
        }
    }

    private String fileNameString(final String locale) {
        return String.format(FILE_NAME_FORMAT, currentFileset, locale.isEmpty() ? "" : "." + locale.toLowerCase());
    }

    public Translation getSingleTranslation(final String locale,
      final String key) {
        final String currentValue = getValueElement(locale, key).getText();
        return new Translation(locale, key, currentValue);
    }

    public List<String> getAvailableLocales() {
        return new ArrayList<>(translations.keySet());
    }

    public boolean isNotSaved() {
        return !saved.get();
    }
}
