package de.vogel612.helper.data;

import de.vogel612.helper.data.util.Serialization;
import org.jdom2.Document;
import org.jdom2.Element;

import static de.vogel612.helper.data.util.DataUtilities.*;

import de.vogel612.helper.data.util.DataUtilities;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Overview model. Provides in-memory caching, parsing and writing of resx files
 */
public class FilesetOverviewModel {

    private final Set<Runnable> parseCompletionListeners = new HashSet<>();
    private final Map<String, Document> translations = new HashMap<>();

    private final AtomicBoolean saved = new AtomicBoolean(true);
    private Path currentPath;
    private String currentFileset;

    /**
     * Adds a listener that is notified upon the completion of a parsing process
     *
     * @param listener A {@link Runnable} that is executed when a parse of input files is completed
     */
    public void addParseCompletionListener(Runnable listener) {
        parseCompletionListeners.add(listener);
    }

    /**
     * Loads the fileset of the given file into memory. As Fileset are considered all files that have the same opening
     * name. The Filename for our purposes consists of the fileset, the optional locale and the extension <tt>resx</tt>
     *
     * @param file Any file of the fileset to load
     *
     * @throws IOException              in case the fileset cannot be read from disk.
     * @throws IllegalArgumentException In case the given path does not point to a file
     * @implNote IOException may also be thrown when the read-access to the path is denied
     */
    public void loadResxFileset(final Path file) throws IOException {
        if (file.toFile().isDirectory()) {
            throw new IllegalArgumentException("We need to work with an actual file");
        }

        this.currentPath = file.getParent();
        this.currentFileset = DataUtilities.getFilesetIdentifier(file);
        translations.clear();

        try (Stream<Path> resxFiles = DataUtilities.streamFileset(currentPath, currentFileset)) {
            translations.putAll(resxFiles.collect(Collectors.toMap(
                DataUtilities::parseLocale, Serialization::parseFile)
            ));
        }
        normalizeDocuments();
        parseCompletionListeners.forEach(Runnable::run);
    }

    private void normalizeDocuments() {
        final Set<String> singleTruth = translations
          .get(SINGLE_TRUTH_LOCALE)
          .getRootElement()
          .getChildren(Serialization.ELEMENT_NAME)
          .stream()
          .map(el -> el.getAttribute(Serialization.KEY_NAME).getValue())
          .collect(Collectors.toSet());

        translations.values().forEach(
          doc -> normalizeDocument(doc, singleTruth));
        saved.lazySet(false);
    }

    private void normalizeDocument(final Document doc, final Set<String> singleTruth) {
        final List<Element> localeElements = doc.getRootElement().getChildren(Serialization.ELEMENT_NAME);
        Set<String> localeKeys = new HashSet<>();

        // remove keys not present in the Single truth
        for (Iterator<Element> it = localeElements.iterator(); it.hasNext(); ) {
            final Element el = it.next();
            if (!singleTruth.contains(el.getAttribute(Serialization.KEY_NAME).getValue())) {
                it.remove();
                continue;
            }
            localeKeys.add(el.getAttribute(Serialization.KEY_NAME).getValue());
        }

        singleTruth.stream()
          .filter(key -> !localeKeys.contains(key))
          .map(key -> {
              final String val = getSingleTranslation(SINGLE_TRUTH_LOCALE, key).getValue();
              return Serialization.createNewElement(key, val);
          })
          .forEach(doc.getRootElement()::addContent);
    }

    /**
     * Returns an ordered list of the translations for a given Locale.
     *
     * @param locale The locale of the translations to return.
     *
     * @return A list of {@link Translation Translations} ordered alphabetically by their {@link Translation#key}
     */
    public List<Translation> getTranslations(final String locale) {
        Document document = translations.get(locale);
        final List<Element> translationElements = document.getRootElement()
          .getChildren(Serialization.ELEMENT_NAME);

        return translationElements.stream()
          .map(el -> new Translation(locale, el))
          .sorted(Comparator.comparing(Translation::getKey))
          .collect(Collectors.toList());
    }

    /**
     * Updates the Translation for a given locale at the given key to the given new translation.
     *
     * @param locale         The locale of the resx-file to modify
     * @param key            The key of the translation to update
     * @param newTranslation The new value for the translation
     */
    public void updateTranslation(final String locale, final String key,
      final String newTranslation) {
        Serialization.getValueElement(translations.get(locale), key).setText(newTranslation);
    }

    /**
     * Saves all changes from the in-memory-cache to disk
     *
     * @throws IOException In case saving the changes fails
     */
    public void saveAll() throws IOException {
        for (Map.Entry<String, Document> entry : translations.entrySet()) {
            final Path outFile = currentPath.resolve(fileNameString(currentFileset, entry
              .getKey()));
            try (OutputStream outStream = Files.newOutputStream(outFile,
              StandardOpenOption.TRUNCATE_EXISTING,
              StandardOpenOption.WRITE)) {
                Serialization.XML_PRETTY_PRINT.output(entry.getValue(), outStream);
                saved.lazySet(true);
            }
        }
    }

    /**
     * Gets a single translation for a given locale by it's key
     *
     * @param locale The locale the translation belongs to
     * @param key    The key uniquely identifying the Translation
     *
     * @return The translation for the given locale at the given key
     */
    public Translation getSingleTranslation(final String locale,
      final String key) {
        final String currentValue = Serialization.getValueElement(translations.get(locale), key).getText();
        return new Translation(locale, key, currentValue);
    }

    /**
     * Checks whether the current in-memory-cache has been saved to disk.<br />
     * <b>BEWARE:</b> This does not check whether the in-memory-cache is up to date with the content on disk
     *
     * @return True if the cache has been saved, false otherwise
     */
    public boolean isNotSaved() {
        return !saved.get();
    }
}
