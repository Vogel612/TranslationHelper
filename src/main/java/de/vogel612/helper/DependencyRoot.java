package de.vogel612.helper;

import de.vogel612.helper.data.OverviewModel;
import de.vogel612.helper.ui.OverviewPresenter;
import de.vogel612.helper.ui.OverviewView;
import de.vogel612.helper.ui.ResxChooser;
import de.vogel612.helper.ui.TranslationView;

/**
 * Created by vogel612 on 02.03.16.
 */
public final class DependencyRoot {

    public static void inject(OverviewModel model, OverviewView overview, OverviewPresenter presenter,
      TranslationView translations, ResxChooser resxChooser) {

        // FIXME unsaved changes checker??

        overview.addWindowClosingListener(presenter::onWindowCloseRequest);
        overview.addLanguageRequestListener(presenter::fileChoosing);
        overview.addSaveRequestListener(presenter::onSaveRequest);
        overview.addTranslationRequestListener(presenter::onTranslateRequest);
        model.addParseCompletionListener(presenter::onParseCompletion);
        translations.addTranslationAbortListener(presenter::onTranslationAbort);
        translations.addTranslationSubmitListener(presenter::onTranslationSubmit);
        resxChooser.addCompletionListener(presenter::fileChoiceCompletion);
    }
}
