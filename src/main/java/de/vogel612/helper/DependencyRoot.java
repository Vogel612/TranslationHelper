package de.vogel612.helper;

import de.vogel612.helper.data.FilesetOverviewModel;
import de.vogel612.helper.ui.*;

/**
 * Created by vogel612 on 02.03.16.
 */
public final class DependencyRoot {

    public static void inject(FilesetOverviewModel model, OverviewView overview, OverviewPresenter presenter,
                              TranslationView translations, LocaleChooser localeChooser) {
        overview.addWindowClosingListener(presenter::onWindowCloseRequest);
        overview.addLanguageRequestListener(presenter::fileChoosing);
        overview.addSaveRequestListener(presenter::onSaveRequest);
        overview.addTranslationRequestListener(presenter::onTranslateRequest);
        model.addParseCompletionListener(presenter::onParseCompletion);
        translations.addTranslationAbortListener(presenter::onTranslationAbort);
        translations.addTranslationSubmitListener(presenter::onTranslationSubmit);
        localeChooser.addCompletionListener(presenter::fileChoiceCompletion);
    }
}
