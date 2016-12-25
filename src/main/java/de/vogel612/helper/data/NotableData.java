package de.vogel612.helper.data;

import de.vogel612.helper.ui.jfx.TranslationPair;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static class to help assess "Notability" of data-combinations.
 * Following are {@link Notability#ERROR ERROR}-Notabilities:
 * <ul><li>C# Format specifiers are mismatching for left and right</li></ul>
 * Following are {@link Notability#WARNING WARNING}-Notabilities:
 * <ul><li>Any value is an empty String</li><li>Values on both sides match</li></ul>
 * Following are {@link Notability#INFO INFO}-Notabilites:
 * <ul><li></li></ul>
 * Any other Data is considered {@link Notability#DEFAULT DEFAULT}
 */
public class NotableData {

    private static final String FORMAT_FINDER = "(?<!\\{)\\{(?:\\{\\{)*((?:\\d+)(?::.*?)?)}(?:}})*(?!})";
    private static final Pattern FORMAT_PATTERN = Pattern.compile(FORMAT_FINDER, Pattern.MULTILINE);

    public enum Notability {
        DEFAULT, INFO, WARNING, ERROR
    }

    public static Notability assessNotability(TranslationPair pair) {
        return assessNotability(pair.getLeft(), pair.getRight());
    }

    public static Notability assessNotability(Translation left, Translation right) {
        final Set<String> leftFormats = getFormatSpecifiers(left.toString());
        final Set<String> rightFormats = getFormatSpecifiers(right.toString());

        if (!(leftFormats.containsAll(rightFormats) && rightFormats.containsAll(leftFormats))) {
            return Notability.ERROR;
        }
        if (right.getValue().isEmpty() || left.getValue().isEmpty()) {
            return Notability.WARNING;
        }
        if (left.equals(right)) {
            return Notability.WARNING;
        }
        return Notability.DEFAULT;
    }

    private static Set<String> getFormatSpecifiers(String s) {
        final Matcher m = FORMAT_PATTERN.matcher(s);
        final Set<String> result = new HashSet<>();
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

}
