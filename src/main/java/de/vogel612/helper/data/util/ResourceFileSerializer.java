package de.vogel612.helper.data.util;

import org.jdom2.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vogel612 on 20.07.16.
 */
public class ResourceFileSerializer {

    public static Map<String, String> deserializeToMap(Document associatedDocument) {
        final Map<String, String> result = new HashMap<>();
        associatedDocument.getRootElement().getChildren(Serialization.ELEMENT_NAME)
                .forEach(element -> {
                    final String key = element.getAttributeValue(Serialization.KEY_NAME);
                    final String value = element.getText();
                    result.put(key, value);
                });
        return result;
    }
}
