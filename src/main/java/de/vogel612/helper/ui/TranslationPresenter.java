package de.vogel612.helper.ui;

import de.vogel612.helper.data.Translation;

/**
 * <p>
 * Presenter for editing a single Translation. This part of the Program does not
 * require a model, since {@link Translation} <b>is</b> the model. The default
 * workflow is simple. From an OverviewPresenter call:
 * </p>
 *
 * <pre>
 * TranslationPresenter tp = new TranslationPresenterImpl();
 * tp.register(this);
 * tp.setRequestedTranslation(translation);
 * </pre>
 *
 * <p>
 * And handle the onTranslate-Event like the following
 * </p>
 *
 * <pre>
 *     onTranslationSubmit((t) -> tp.hide(); /* use translation *\/);
 * </pre>
 *
 * @implNote Implementation will not happen with an additional separated View
 *           interface, since there is in no way enough View-Logic to justify it
 *
 * @author vogel612<<a href="mailto:vogel612@gmx.de">vogel612@gmx.de</a>>
 */
public interface TranslationPresenter {

	public void register(OverviewPresenter p);

	public void show();

	public void hide();

	public void setRequestedTranslation(Translation t);
}
