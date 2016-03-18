package de.vogel612.helper.ui.jfx;

import de.vogel612.helper.data.Translation;

import javafx.beans.value.ObservableValueBase;
import javafx.util.Pair;

/**
 * Created by vogel612 on 18.03.16.
 */
public class TranslationPair extends ObservableValueBase<Pair<Translation, Translation>> {

    private final Translation left;
    private final Translation right;

    public TranslationPair(Translation left, Translation right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Pair<Translation, Translation> getValue() {
        return new Pair<>(left, right);
    }

    public Translation getLeft() {
        return left;
    }

    public Translation getRight() {
        return right;
    }
}
