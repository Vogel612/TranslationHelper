package de.vogel612.helper.ui.javafx;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.hasText;

import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import de.vogel612.helper.data.Translation;
import de.vogel612.helper.ui.jfx.JFXOverviewView;
import de.vogel612.helper.ui.jfx.TranslationPair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class JavaFXOverviewViewTests extends ApplicationTest {

    private JFXOverviewView cut;

    @Override
    public void start (Stage stage) throws Exception {
        cut = new JFXOverviewView(stage, getClass().getResource("/OverviewView.fxml"));
        cut.show();
    }

    @Before
    public void before() {
        cut.rebuildWith(Collections.emptyList(), Collections.emptyList());
    }

    @Test
    public void hasAllUIElements() {
        sleep(700, TimeUnit.MILLISECONDS);

        verifyThat("#save", hasText("Save"));
        verifyThat("#chooseLang", hasText("Choose Languages"));
        verifyThat("#table", (Predicate<TableView<TranslationPair>>)tv -> tv.getItems().isEmpty());
    }

    @Test
    public void tableContainsItemsFromBuilding() {
        final List<Translation> left = Arrays.asList(new Translation("","key","value"), new Translation("", "key2", "value2"));
        final List<Translation> right = Arrays.asList(new Translation("de", "key", "wert"), new Translation("", "key2", "wert2"));

        cut.rebuildWith(left, right);

        TableView<TranslationPair> table = lookup("#table").queryFirst();
        ObservableList<TranslationPair> items = table.getItems();

        assertEquals("key", items.get(0).getLeft().getKey());
        assertEquals("value", items.get(0).getLeft().getValue());
        assertEquals("wert", items.get(0).getRight().getValue());

        assertEquals("key2", items.get(1).getLeft().getKey());
        assertEquals("value2", items.get(1).getLeft().getValue());
        assertEquals("wert2", items.get(1).getRight().getValue());

        //FIXME: verify display behavior!!
    }

    // FIXME implement and verify row-highlighting!
}
