package de.vogel612.helper.data;

import static de.vogel612.helper.data.util.DataUtilities.*;

import de.vogel612.helper.data.util.DataUtilities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Overview model. Provides in-memory caching, parsing and writing of resx files
 */
public class FilesetOverviewModel {
    private final Set<Runnable> parseCompletionListeners = new HashSet<>();
    private final Map<String, ResourceFile> resources = new HashMap<>();

    private final AtomicBoolean saved = new AtomicBoolean(true);
    private Path currentPath;
    private String currentFileset;

    /**
     * Adds a listener that is notified upon the completion of a parsing process
     *
     * @param listener
     *         A {@link Runnable} that is executed when a parse of input files is completed
     */
    public void addParseCompletionListener(Runnable listener) {
        parseCompletionListeners.add(listener);
    }

    /**
     * Loads the fileset of the given file into memory. As Fileset are considered all files that have the same opening
     * name. The Filename for our purposes consists of the fileset, the optional locale and the extension <tt>resx</tt>
     *
     * @param file
     *         Any file of the fileset to load
     *
     * @throws IOException
     *         in case the fileset cannot be read from disk.
     * @throws IllegalArgumentException
     *         In case the given path does not point to a file
     * @implNote IOException may also be thrown when the read-access to the path is denied
     */
    public void loadResxFileset(final Path file) throws IOException {
        if (file.toFile().isDirectory()) {
            throw new IllegalArgumentException("We need to work with an actual file");
        }
        this.currentPath = file.getParent();
        this.currentFileset = DataUtilities.getFileIdentifier(file);
        loadResourceFiles(ResourceFile.getResourceFiles(currentPath, currentFileset));
    }

    public void loadResxFileset(final ResourceSet resourceSet) throws IOException {
        this.currentPath = resourceSet.getFolder();
        this.currentFileset = resourceSet.getName();
        loadResourceFiles(ResourceFile.getResourceFiles(resourceSet));
    }

    private void loadResourceFiles(Stream<ResourceFile> resourceFiles) {
        resources.clear();
        resources.putAll(resourceFiles.collect(Collectors.toMap(ResourceFile::getLocale, Function.identity())));
        normalizeResourceFiles();
        parseCompletionListeners.forEach(Runnable::run);
    }

    private void normalizeResourceFiles() {
        final Set<String> singleTruth = resources.get(FALLBACK_LOCALE).getKeys();
        resources.values().forEach(file -> file.normalize(singleTruth, resources.get(FALLBACK_LOCALE)));
        saved.lazySet(false);
    }

    /**
     * Returns a set of all available locales for translation
     *
     * @return an unordered Set of available locales
     */
    public Set<String> getAvailableLocales() {
        return resources.keySet();
    }

    /**
     * Returns an ordered list of the translations for a given Locale.
     *
     * @param locale
     *         The locale of the translations to return.
     *
     * @return A list of {@link Translation Translations} ordered alphabetically by their {@link Translation#key}
     */
    public List<Translation> getTranslations(final String locale) {
        return resources.get(locale).orderedTranslations();
    }

    /**
     * Updates the Translation for a given locale at the given key to the given new translation.
     *
     * @param locale
     *         The locale of the resx-file to modify
     * @param key
     *         The key of the translation to update
     * @param newTranslation
     *         The new value for the translation
     */
    public void updateTranslation(final String locale, final String key,
                                  final String newTranslation) {
        resources.get(locale).updateTranslation(key, newTranslation);
    }

    /**
     * Saves all changes from the in-memory-cache to disk
     *
     * @throws IOException
     *         In case saving the changes fails
     */
    public void saveAll() throws IOException {
        for (ResourceFile file : resources.values()) {
            file.save();
        }
        saved.lazySet(true);
    }

    /**
     * Gets a single translation for a given locale by it's key
     *
     * @param locale
     *         The locale the translation belongs to
     * @param key
     *         The key uniquely identifying the Translation
     *
     * @return The translation for the given locale at the given key
     */
    public Translation getSingleTranslation(final String locale, final String key) {
        final String currentValue = resources.get(locale).getTranslation(key);
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
