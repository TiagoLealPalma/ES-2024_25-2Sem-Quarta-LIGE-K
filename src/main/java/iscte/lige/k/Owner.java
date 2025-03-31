package iscte.lige.k;

import java.util.ArrayList;
import java.util.List;

public class Owner {
    private String name;
    private List<Property> properties;
    private int numOfProperties = 0;

    public Owner (String name) {
        this.name = name;
        this.properties = new ArrayList<>();
    }

    public Owner (String name, Property property) {
        this.name = name;
        this.properties = new ArrayList<>();
        this.properties.add(property);
        numOfProperties++;
    }

    public void addProperty(Property property) {
        if (property == null)
            throw new IllegalArgumentException("Property cannot be null");

        properties.add(property);
        numOfProperties++;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public int getNumOfProperties() {
        return numOfProperties;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", numOfProperties=" + numOfProperties +
                '}';
    }
}
