package de.vogel612.helper.data;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import de.vogel612.helper.data.NotableData.Notability;
import de.vogel612.helper.ui.jfx.TranslationPair;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by vogel612 on 14.04.16.
 */
@RunWith(Parameterized.class)
public class NotableDataTest {

    @Parameter(0)
    public Translation leftSide;

    @Parameter(1)
    public Translation rightSide;

    @Parameter(2)
    public Notability expected;

    @Parameters
    public static Collection<Object[]> testData() {
        Object[][] data = new Object[][]{
          { new Translation("","", "asd"), new Translation("","","foo"), Notability.DEFAULT },
          { new Translation("", "", ""), new Translation("", "", "asd"), Notability.WARNING },
          { new Translation("", "", "asd"), new Translation("", "", "asd"), Notability.WARNING},
          { new Translation("", "", "asd"), new Translation("", "", "asd{0}"), Notability.ERROR},
          { new Translation("", "", "asd{0:d.5}"), new Translation("", "", "asd{0}"), Notability.ERROR},
          { new Translation("", "", "asd{1}"), new Translation("", "", "asd{0}"), Notability.ERROR},
          { new Translation("", "", "{0}as{1}d"), new Translation("", "", "asd{0}"), Notability.ERROR},
          };
        return Arrays.asList(data);
    }

    @Test
    public void checkBackgroundColors() {
        assertEquals(expected, NotableData.assessNotability(leftSide, rightSide));
        assertEquals(expected, NotableData.assessNotability(new TranslationPair(leftSide, rightSide)));
    }
}