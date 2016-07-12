package de.vogel612.helper.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vogel612 on 12.07.16.
 */
public class Project {

    private String name;
    private final List<ResourceSet> associatedResources = new ArrayList<ResourceSet>;

    public Project(Path saveLocation) {
        // parse given file into Project
    }

    public void associate(ResourceSet resource) {
        if (resource == null || associatedResources.contains(resource)) {
            return;
        }
        associatedResources.add(resource);
    }

    public void disassociate(ResourceSet resource) {
        associatedResources.remove(resource);
    }

    public String getName() {
        return name;
    }

    public List<ResourceSet> getAssociatedResources() {
        return new ArrayList<>(associatedResources);
    }
}
