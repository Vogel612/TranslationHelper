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

public class OverviewModel {

    public static final String VALUE_NAME = "value";
    public static final String KEY_NAME = "name";
    public static final String SINGLE_TRUTH_LOCALE = "";

    private static final String ELEMENT_NAME = "data";
    private static final String FILE_NAME_FORMAT = "RubberduckUI%s.resx";
    private static final String FILENAME_REGEX = "^.*RubberduckUI\\.?([a-z]{2}(?:-[a-z]{2})?)?\\.resx$";

    private static final Pattern localeFinder = Pattern.compile(FILENAME_REGEX);
    private final Set<Runnable> parseCompletionListeners = new HashSet<>();

    private final Map<String, Document> translations = new HashMap<>();
    private final XPathFactory xPathFactory = XPathFactory.instance();
    private final XPathExpression<Element> valueExpression = xPathFactory.compile("/*/"
      + ELEMENT_NAME + "[@" + KEY_NAME + "=$key]/"
      + VALUE_NAME, Filters.element(), Collections.singletonMap("key", ""));

    private Path currentPath;
    private final AtomicBoolean saved = new AtomicBoolean(true);
    public static final XMLOutputter XML_PRETTY_PRINT = new XMLOutputter(Format.getPrettyFormat());

    private static String parseFileName(final Path path) {
        final Matcher localeMatcher = localeFinder.matcher(path.getFileName().toString());
        if (localeMatcher.find()) { // should always be true, since we check beforehand
            final String locale = localeMatcher.group(1) == null
                                  ? SINGLE_TRUTH_LOCALE
                                  : localeMatcher.group(1);
            return locale;
        }
        throw new IllegalArgumentException("Argument was not a conform resx file");
    }

    public void addParseCompletionListener(Runnable listener) {
        parseCompletionListeners.add(listener);
    }

    public void loadFromDirectory(final Path resxFolder) throws IOException {
        this.currentPath = resxFolder;
        translations.clear();
        try (Stream<Path> resxFiles = Files.find(resxFolder, 1, (path,
            properties) -> path.toString().matches(FILENAME_REGEX),
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
        valueExpression.setVariable("key", key);
        return valueExpression.evaluateFirst(translations.get(locale));
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
        return String.format(FILE_NAME_FORMAT, locale.isEmpty() ? "" : "." + locale.toLowerCase());
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
