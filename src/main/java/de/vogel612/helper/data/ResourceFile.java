package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;
import de.vogel612.helper.data.util.ResourceFileSerializer;
import de.vogel612.helper.data.util.Serialization;
import org.jdom2.Document;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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

    public ResourceFile(Path filepath) {
        folder = filepath.getParent();
        name = DataUtilities.getFileIdentifier(filepath);
        locale = DataUtilities.getFileLocale(filepath);
        associatedDocument = Serialization.parseFile(filepath);

        entries.putAll(ResourceFileSerializer.deserializeToMap(associatedDocument));
        // FIXME init entries
    }

    public String getTranslation(String key) {
        return entries.get(key);
    }

    public void updateTranslation(String key, String value) {
        entries.put(key, value);
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

    public static Stream<ResourceFile> getResourceFiles(ResourceSet resourceSet) throws IOException {
        return resourceSet.files().map(ResourceFile::new);
    }

    public static Stream<ResourceFile> getResourceFiles(Path folder, String name) throws IOException {
        return DataUtilities.streamFileset(folder, name).map(ResourceFile::new);
    }
}
