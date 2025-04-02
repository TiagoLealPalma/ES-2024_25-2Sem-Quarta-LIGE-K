package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

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

    public void calculateAvgArea(){
        List<Property> uniqueProperties = new ArrayList<>(); // Properties that are not neighbours of each other
        List<List<Property>> jointProperties = new ArrayList<>(); // Properties next to one another

        for (Property property : properties) {
            Geometry g1 = property.getGeometry().buffer(0);
            for (Property otherProperty : properties) {
                Geometry g2 = otherProperty.getGeometry().buffer(0);
                if(g1.touches(g2) || g1.intersects(g2)){

                }
            }
        }


    }

    // Getters

    public String getName() {
        return name;
    }

    public List<Property> getProperties() { return properties; }

    public int getNumOfProperties() {
        return numOfProperties;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", numOfProperties=" + numOfProperties +
                '}';
    }
}
