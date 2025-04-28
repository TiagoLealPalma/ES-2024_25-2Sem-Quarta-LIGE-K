package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a property owner, allowing the registration of properties
 * and the calculation of average land area, automatically grouping
 * adjacent properties.
 */
public class Owner {
    private String name;
    private List<Property> properties;

    /**
     * Helper inner class representing a group of adjacent properties,
     * combining their area and geometry.
     */
    private class Area {
        double area;
        Geometry geometry;
        List<String> parcelas = new ArrayList<>();

        /**
         * Constructs a new Area from a single property.
         * @param property the property used to initialize the area
         */
        Area(Property property) {
            area = property.getArea();
            geometry = property.getGeometry();
            parcelas.add(property.getParcelaId());
        }

        /**
         * Adds a property to the current area, updating the total area
         * and uniting the geometry.
         * @param property the property to add
         */
        public void addArea(Property property) {
            area += property.getArea();
            geometry.union(property.getGeometry());
            parcelas.add(property.getParcelaId());
        }
    }

    /**
     * Constructs an owner with no properties initially.
     * @param name the owner's name
     */
    public Owner(String name) {
        this.name = name;
        this.properties = new ArrayList<>();
    }

    /**
     * Constructs an owner with an initial property.
     * @param name the owner's name
     * @param property the initial property
     */
    public Owner(String name, Property property) {
        this.name = name;
        this.properties = new ArrayList<>();
        this.properties.add(property);
    }

    /**
     * Adds a property to the owner's property list.
     * @param property the property to add
     * @throws IllegalArgumentException if the property is null
     */
    public void addProperty(Property property) {
        if (property == null)
            throw new IllegalArgumentException("Property cannot be null");

        properties.add(property);
    }

    /**
     * Calculates the average area of the owner's properties. Adjacent properties
     * (touching or intersecting) are merged and treated as a single unit.
     * @return the average area (including merged groups)
     */
    public double calculateAvgArea() {
        if (properties.isEmpty()) return 0;

        List<Property> uniqueProperties = new ArrayList<>();
        List<Area> jointProperties = new ArrayList<>();
        List<Integer> notUnique = new ArrayList<>();

        // Iterate through all properties to group adjacent ones
        for (int i = 0; i < properties.size(); i++) {
            Area areaToEvaluate = null;

            // Check if this property is already part of an existing group
            for (Area area : jointProperties) {
                if (area.parcelas.contains(properties.get(i).getParcelaId())) {
                    areaToEvaluate = area;
                }
            }

            // If not yet grouped, start a new group with this property
            if (areaToEvaluate == null) {
                areaToEvaluate = new Area(properties.get(i));
            }

            // Save current area value to check later if it was changed
            double temp = areaToEvaluate.area;

            // Try to merge with other properties that are adjacent
            for (int j = 0; j < properties.size(); j++) {
                Property property = properties.get(j);

                // Skip if already included in this group
                if (areaToEvaluate.parcelas.contains(property.getParcelaId()))
                    continue;

                // Merge if properties are adjacent
                if (areaToEvaluate.geometry.touches(property.getGeometry()) ||
                        areaToEvaluate.geometry.intersects(property.getGeometry())) {

                    areaToEvaluate.addArea(property);
                    if (!notUnique.contains(i)) notUnique.add(i);
                    notUnique.add(j);
                }
            }

            // If the area has changed, add it to the list of joint groups
            if (temp != areaToEvaluate.area) {
                if (!jointProperties.contains(areaToEvaluate))
                    jointProperties.add(areaToEvaluate);
            }
        }

        // Properties not merged are treated as individual (unique) properties
        for (int i = 0; i < properties.size(); i++) {
            if (!notUnique.contains(i))
                uniqueProperties.add(properties.get(i));
        }

        // Sum the areas
        double sum = 0;
        for (Property property : uniqueProperties) {
            sum += property.getArea();
        }
        for (Area area : jointProperties) {
            sum += area.area;
        }

        // Compute the average
        return sum / (uniqueProperties.size() + jointProperties.size());
    }

    /**
     * Gets the owner's name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of properties.
     * @return list of properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Returns the number of registered properties.
     * @return property count
     */
    public int getNumOfProperties() {
        return properties.size();
    }

    @Override
    public String toString() {
        return "Owner{" +
                "name='" + name + '\'' +
                ", numOfProperties=" + properties.size() +
                '}';
    }
}
