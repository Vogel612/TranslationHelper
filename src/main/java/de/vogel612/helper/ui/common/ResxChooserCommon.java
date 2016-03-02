package de.vogel612.helper.ui.common;

import static de.vogel612.helper.data.util.DataUtilities.FILENAME_PATTERN;
import static de.vogel612.helper.data.util.DataUtilities.streamFileset;

import de.vogel612.helper.data.util.DataUtilities;
import de.vogel612.helper.ui.ResxChooser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Encapsulates common functionality for {@link ResxChooser Resx Choosers} into an abstract class.<br/>
 * <p>
 * Encapsulated is the fields necessary to store data, namely {@link #left}, {@link #right} and {@link
 * #filesetBacking}.
 * Additionally there is an automatic reparsing functionality of the encapsulated {@link #localeOptionCache}, given
 * implementing classes call {@link #onFilesetChange()} when the Fileset changes. That triggers a recheck for the
 * locale
 * options. Additionally {@link #onFilesetChange()} will call {@link #onFilesetChangeInternal()} upon completion to
 * notify the implementing class.
 * </p>
 * <p>
 * There even is a method for internal use, that handles listener notification, namely {@link #completeChoice()}. It
 * will access the fields in the current class to build a {@link de.vogel612.helper.ui.common.ResxChooserCommon.ResxChooserEvent}
 * with the relevant information
 * </p>
 */
public abstract class ResxChooserCommon implements ResxChooser {
    protected final Set<String> localeOptionCache = new HashSet<>();
    private final Set<Consumer<ResxChooserEvent>> resxChoiceCompletionListener = new HashSet<>();
    protected String left;
    protected String right;
    protected Path filesetBacking; // could be done with Observable notifying onFilesetChange() ...

    @Override
    public final void setFileset(Path fileset) {
        if (fileset == null || !fileset.toFile().exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        this.filesetBacking = fileset;
        onFilesetChange();
    }

    /**
     * Should be called when changing the {@link #filesetBacking} to update the {@link #localeOptionCache available
     * locales}.
     * This also calls {@link #onFilesetChangeInternal()} to notify child classes of the refresh.
     */
    protected final void onFilesetChange() {
        final Matcher filesetMatcher = FILENAME_PATTERN.matcher(filesetBacking.getFileName().toString());
        if (filesetMatcher.matches()) { // should always be true
            // group is not optional, so we're good
            final String filesetName = filesetMatcher.group(1);
            try (final Stream<Path> filesetFiles = streamFileset(filesetBacking.getParent(), filesetName)) {
                localeOptionCache.clear();
                localeOptionCache.addAll(filesetFiles.map(
                  DataUtilities::parseLocale).collect(
                  Collectors.toSet()));
            } catch (IOException e1) {
                // FIXME handle
                e1.printStackTrace();
            }
        }
        onFilesetChangeInternal();
    }

    protected abstract void onFilesetChangeInternal();

    @Override
    public final void addCompletionListener(Consumer<ResxChooserEvent> listener) {
        resxChoiceCompletionListener.add(listener);
    }

    protected final void completeChoice() {
        if (left != null && right != null && filesetBacking != null) {
            final ResxChooserEvent evt = new ResxChooserEvent(this);
            resxChoiceCompletionListener.forEach(l -> l.accept(evt));
        }
    }

    /**
     * An event signaling the choice of fileset and locale is completed
     */
    public static class ResxChooserEvent {
        private final Path fileset;
        private final String rightLocale;
        private final String leftLocale;

        public ResxChooserEvent(ResxChooserCommon c) {
            this.leftLocale = c.left;
            this.rightLocale = c.right;
            this.fileset = c.filesetBacking;
        }

        public String getLeftLocale() {
            return leftLocale;
        }

        public String getRightLocale() {
            return rightLocale;
        }

        public Path getFileset() {
            return fileset;
        }
    }
}
