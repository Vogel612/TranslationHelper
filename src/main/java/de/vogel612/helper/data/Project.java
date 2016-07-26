package de.vogel612.helper.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by vogel612 on 12.07.16.
 */
public class Project {

    private String name;
    private final List<ResourceSet> associatedResources = new ArrayList<>();

    public Project(String name) {
        this(name, Collections.emptySet());
    }

    public Project(String name, Collection<ResourceSet> associatedResources) {
        this.name = name;
        this.associatedResources.addAll(associatedResources);
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

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + associatedResources.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (!name.equals(project.name)) return false;
        return associatedResources.equals(project.associatedResources);


    }
}
