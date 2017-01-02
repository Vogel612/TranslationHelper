package de.vogel612.helper.data;

import de.vogel612.helper.data.util.DataUtilities;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.vogel612.helper.data.util.DataUtilities.FALLBACK_LOCALE;

/**
 * Created by vogel612 on 12.07.16.
 */
public class ResourceSet {

    private final Path folder;
    private final String name;
    private final Set<String> locales = new HashSet<>();

    /**
     * Attempts to create a ResourceSet from the given path.
     * To that end the given Path is assumed to be a resx file. All similarly named resx files in the same folder are
     * assumed to belong to it's resource set.
     *
     * @param file
     *         The Path to a resx-file belonging to the resource set to be created. Passing in <tt>null</tt> results in
     *         a NullPointerException
     *
     * @return A ResourceSet instance encapsulating all relevant data that could be extracted from the given Path.
     * <tt>null</tt> if the given Path was a directory or anything at all went wrong during the object creation.
     */
    public static ResourceSet create(final Path file) {
        Objects.requireNonNull(file, "file");
        if (file.toFile().isDirectory()) {
            return null;
        }

        try {
            final Path folder = file.getParent();
            final String name = DataUtilities.getFileIdentifier(file);
            final Set<String> locales = DataUtilities.streamFileset(folder, name)
                    .map(DataUtilities::getFileLocale)
                    .filter(l -> !l.equals(FALLBACK_LOCALE))
                    .collect(Collectors.toSet());
            return new ResourceSet(name, folder, locales);
        } catch (Exception e) { // gotta catch em all!
            // FIXME shout at user?
            return null;
        }
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
        Set<String> result = new HashSet<>(locales);
        result.add(FALLBACK_LOCALE); // is this an evil assumption?
        return result;
    }

    public String getName() {
        return name;
    }

    public Stream<Path> files() throws IOException {
        return DataUtilities.streamFileset(folder, name)
                .filter(path -> getLocales().contains(DataUtilities.getFileLocale(path)));
    }

    @Override
    public String toString() {
        return "ResourceSet{" +
                "folder=" + folder +
                ", name='" + name + '\'' +
                ", locales=" + Arrays.toString(locales.toArray()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
