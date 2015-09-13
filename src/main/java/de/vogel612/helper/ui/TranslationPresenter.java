package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

/**
 * <p>
 * Presenter for editing a single Translation. This part of the Program does not
 * require a model, since {@link Translation} <b>is</b> the model. The default
 * workflow is simple. From an OverviewPresenter call:
 * </p>
 * <p>
 * <pre>
 * TranslationPresenter tp = new TranslationPresenterImpl();
 * tp.register(this);
 * tp.setRequestedTranslation(translation);
 * </pre>
 * <p>
 * <p>
 * And handle the onTranslate-Event like the following
 * </p>
 * <p>
 * <pre>
 *     onTranslationSubmit((t) -> tp.hide(); /* use translation *\/);
 * </pre>
 *
 * @author vogel612
 * @implNote Implementation will not happen with an additional separated View
 * interface, since there is in no way enough View-Logic to justify it
 */
public interface TranslationPresenter {

    void register(OverviewPresenter p);

    void show();

    void hide();

    void setRequestedTranslation(Translation t, String locale);
}
