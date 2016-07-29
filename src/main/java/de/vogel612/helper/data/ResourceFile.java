package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;
import de.vogel612.helper.data.util.ResourceFileSerializer;
import de.vogel612.helper.data.util.Serialization;
import org.jdom2.Document;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vogel612 on 25.07.16.
 */
public class ResourceFile {

    private static final ExecutorService backgroundPropagator = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "BG-Propagator"));
    private final Document associatedDocument;

    private final String name;
    private final String locale;
    private final Path folder;
    private final Map<String, String> entries = new HashMap<>();

    public ResourceFile(final Path filePath) {
        Objects.requireNonNull(filePath, "filePath");

        folder = filePath.getParent();
        name = DataUtilities.getFileIdentifier(filePath);
        locale = DataUtilities.getFileLocale(filePath);
        try {
            associatedDocument = Serialization.parseFile(filePath);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new UncheckedIOException(e);
        }
        entries.putAll(ResourceFileSerializer.deserializeToMap(associatedDocument));
    }

    public void updateTranslation(String key, String value) {
        entries.put(key, value);
        backgroundPropagator.submit(() -> ResourceFileSerializer.getValueElement(associatedDocument, key).setText(value));
    }

    public List<Translation> orderedTranslations() {
        return entries.entrySet().stream()
                .map(entry -> new Translation(locale, entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(Translation::getKey))
                .collect(Collectors.toList());
    }

    public void normalize(Set<String> keys, ResourceFile canonical) {
        if (canonical == this) {
            return;
        }
        for (Iterator<String> it = entries.keySet().iterator(); it.hasNext(); ) {
            final String key = it.next();
            if (!keys.contains(key)) {
                it.remove();
            }
        }
        keys.stream().filter(key -> !entries.containsKey(key))
                .forEach(missing -> {
                    final String canonicalTranslation = canonical.getTranslation(missing);
                    associatedDocument.getRootElement().addContent(ResourceFileSerializer.createNewElement(missing, canonicalTranslation));
                    updateTranslation(missing, canonicalTranslation);
                });
    }

    public void save() throws IOException {
        Serialization.serializeDocument(associatedDocument, folder.resolve(DataUtilities.fileNameString(name, locale)));
    }

    public String getTranslation(String key) {
        return entries.get(key);
    }

    public String getName() {
        return name;
    }

    public String getLocale() {
        return locale;
    }

    public Path getFolder() {
        return folder;
    }

    public Set<String> getKeys() {
        return entries.keySet();
    }

    public static Stream<ResourceFile> getResourceFiles(Path folder, String name) throws IOException {
        return DataUtilities.streamFileset(folder, name).map(ResourceFile::new);
    }

    public static Stream<ResourceFile> getResourceFiles(ResourceSet resourceSet) throws IOException {
        return resourceSet.files().map(ResourceFile::new);
    }
}
