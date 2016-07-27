package de.vogel612.helper.data.util;

import de.vogel612.helper.data.Project;
import de.vogel612.helper.data.ResourceSet;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by vogel612 on 25.07.16.
 */
public class ProjectSerializerTests {

    private final Path canonicalFile = Paths.get(getClass().getResource("/ProjectSerialization.thp").getFile());
    private final Path tempFile = canonicalFile.resolveSibling("temp.thp");

    @Test
    public void testProjectDeserialization() throws IOException {
        Project result = ProjectSerializer.deserialize(canonicalFile);
        assertEquals("Testproject", result.getName());
        assertEquals(1, result.getAssociatedResources().size());
        ResourceSet check = new ResourceSet("Set1", Paths.get("path/to/somewhere"), new HashSet<>(Arrays.asList("de-DE", "fr-CA", "en-EN")));
        assertEquals(check, result.getAssociatedResources().get(0));
    }

    @Test
    public void testProjectSerialization() throws IOException {

        ResourceSet resourceSet = new ResourceSet("Set1", Paths.get("path/to/somewhere"), new HashSet<>(Arrays.asList("de-DE", "fr-CA", "en-EN")));
        Project toSerialize = new Project("Testproject", Collections.singletonList(resourceSet));

        ProjectSerializer.serialize(toSerialize, tempFile);
        assertEquals(toSerialize, ProjectSerializer.deserialize(tempFile));
    }
}
