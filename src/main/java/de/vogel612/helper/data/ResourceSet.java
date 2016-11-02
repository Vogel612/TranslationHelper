package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by vogel612 on 12.07.16.
 */
public class ResourceSet {

    private final Path folder;
    private final String name;
    private final Set<String> locales = new HashSet<>();

    public static ResourceSet create(final Path file) throws IOException {
        Objects.requireNonNull(file, "file");
        final Path folder = file.getParent();
        final String name = DataUtilities.getFileIdentifier(file);
        final Set<String> locales = DataUtilities.streamFileset(folder, name)
                .map(DataUtilities::getFileLocale).collect(Collectors.toSet());
        return new ResourceSet(name, folder, locales);
    }

    public ResourceSet(String name, Path folder, Set<String> locales) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(folder, "folder");
        Objects.requireNonNull(locales, "locales");
        this.folder = folder;
        this.name = name;
        this.locales.addAll(locales);
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

    public Stream<Path> files() throws IOException {
        return DataUtilities.streamFileset(folder, name)
                .filter(path -> locales.contains(DataUtilities.getFileLocale(path)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResourceSet that = (ResourceSet) o;

        return folder.equals(that.folder)
                && locales.equals(that.locales)
                && name.equals(that.name);

    }

    @Override
    public int hashCode() {
        int result = folder.hashCode();
        result = 31 * result + locales.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
