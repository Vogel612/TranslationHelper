package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vogel612 on 12.07.16.
 */
public class ResourceSet {

    private final Path folder;
    private final Set<String> locales = new HashSet<>();
    private final String name;

    public ResourceSet(Path file) {
        folder = file.getParent();
        name = DataUtilities.getFilesetIdentifier(file);
    }

    public void addLocale(String locale) {
        locales.add(locale);
    }

    public void removeLocale(String locale) {
        locales.remove(locale);
    }

    public Path getFolder() {
        return folder;
    }

    public Set<String> getLocales() {
        return new HashSet<>(locales);
    }

    public String getName() {
        return name;
    }
}
