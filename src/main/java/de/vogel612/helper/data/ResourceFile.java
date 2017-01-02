package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;
import de.vogel612.helper.data.util.ResourceFileSerializer;
import de.vogel612.helper.data.util.Serialization;
import org.jdom2.Document;
import org.jdom2.JDOMException;

import java.io.IOException;
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
        } catch (IOException | JDOMException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
        entries.putAll(ResourceFileSerializer.deserializeToMap(associatedDocument));
    }

    public void updateTranslation(String key, String value) {
        if (!entries.containsKey(key)) {
            return;
        }
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
        entries.keySet().removeIf(key -> !keys.contains(key));
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

    /**
     * Crates a Stream of all ResourceFiles with a given name in a given folder. This assumes the filesystem is the
     * source of single truth, as such only the files present in the filesystem at the instant of invocation can be
     * returned.
     *
     * @param folder
     *         The "parent folder" of a ResourceSet. All files under the folder matching {@link
     *         DataUtilities#FILE_NAME_FORMAT} with any locale are returned.
     * @param name
     *         The "name" of the ResourceSet.
     *
     * @return A <tt>Stream&lt;ResourceFile&gt;</tt> containing all ResourceFiles of the given ResourceSet specification
     * on the filesystem.
     *
     * @throws IOException
     *         In case access to the filesystem results in an IOException, said exception is propagated
     */
    public static Stream<ResourceFile> getResourceFiles(Path folder, String name) throws IOException {
        return DataUtilities.streamFileset(folder, name).map(ResourceFile::new);
    }

    /**
     * Creates a Stream of all ResourceFiles belonging to a given ResourceSet. In case some files are missing on the
     * file system, they will be created as empty files.
     *
     * @param resourceSet
     *         The resource set to obtain the files of
     *
     * @return A <tt>Stream&lt;ResourceFile&gt;</tt> containing all ResourceFiles belonging to the given ResourceSet
     *
     * @throws IOException
     *         If any of the intermediate operations results in an IOException, said exception is propagated.
     */
    public static Stream<ResourceFile> getResourceFiles(ResourceSet resourceSet) throws IOException {
        resourceSet.files().filter(p -> !p.toFile().exists()).forEach(DataUtilities::createEmptyResourceFile);
        return resourceSet.files().map(ResourceFile::new);
    }
}
