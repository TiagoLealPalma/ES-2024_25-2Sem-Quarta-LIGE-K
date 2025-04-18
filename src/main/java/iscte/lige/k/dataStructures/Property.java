package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a land property with spatial, administrative and ownership data.
 * Each property can also maintain a list of its neighboring properties.
 */
public class Property {
    // Identifiers
    private final String parcelaId;
    private final String parcelaNum;

    // Geometric data
    private final Geometry geometry;
    private final double area;
    private final double perimeter;

    // Location data
    private final String parish;
    private final String county;
    private final String island;

    private final Owner owner;
    private final List<Property> neighbourProperties;

    /**
     * Constructs a new Property instance with all required information.
     * Automatically adds this property to the given owner's property list.
     *
     * @param parcelaId unique identifier of the land parcel
     * @param parcelaNum registry number
     * @param perimeter perimeter length in meters
     * @param area area in square meters
     * @param geometry geometric shape of the property
     * @param owner the owner of the property
     * @param parish the parish where the property is located
     * @param county the county where the property is located
     * @param island the island where the property is located
     */
    public Property(String parcelaId, String parcelaNum, double perimeter, double area,
                    Geometry geometry, Owner owner, String parish, String county, String island) {

        this.parcelaId = parcelaId;
        this.parcelaNum = parcelaNum;
        this.perimeter = perimeter;
        this.area = area;
        this.geometry = geometry;
        this.owner = owner;
        this.parish = parish;
        this.county = county;
        this.island = island;
        this.neighbourProperties = new ArrayList<>();

        // Automatically associate this property with its owner
        owner.addProperty(this);
    }

    /**
     * Adds a neighboring property to this property.
     * Duplicate or null values are ignored.
     *
     * @param property the neighboring property to add
     * @throws IllegalArgumentException if the property is null
     */
    public void addNeighbour(Property property) {
        if (property == null)
            throw new IllegalArgumentException("Property cannot be null");

        if (!neighbourProperties.contains(property))
            neighbourProperties.add(property);
    }

    /**
     * Returns a string representation with basic info.
     * @return summary string with owner and number of neighbors
     */
    @Override
    public String toString() {
        return "Property{" +
                "owner=" + owner +
                ", parcela=" + parcelaId +
                ", neighbourProperties=" + neighbourProperties.size() +
                '}';
    }

    // Getters

    /**
     * Gets the unique parcel identifier.
     * @return the parcela ID
     */
    public String getParcelaId() {
        return parcelaId;
    }

    /**
     * Gets the cadastral parcel number.
     * @return the parcela number
     */
    public String getParcelaNum() {
        return parcelaNum;
    }

    /**
     * Gets the geometric shape of the property.
     * @return geometry of the property
     */
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * Gets the area of the property in square meters.
     * @return property area
     */
    public double getArea() {
        return area;
    }

    /**
     * Gets the perimeter of the property in meters.
     * @return property perimeter
     */
    public double getPerimeter() {
        return perimeter;
    }

    /**
     * Gets the parish name where the property is located.
     * @return parish name
     */
    public String getParish() {
        return parish;
    }

    /**
     * Gets the county name where the property is located.
     * @return county name
     */
    public String getCounty() {
        return county;
    }

    /**
     * Gets the island name where the property is located.
     * @return island name
     */
    public String getIsland() {
        return island;
    }

    /**
     * Gets the owner of this property.
     * @return owner object
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Gets the list of neighboring properties.
     * @return list of neighbors
     */
    public List<Property> getNeighbourProperties() {
        return neighbourProperties;
    }
}
