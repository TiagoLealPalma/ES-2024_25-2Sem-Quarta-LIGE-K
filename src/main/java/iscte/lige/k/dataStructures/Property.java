package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

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

    public Property(String parcelaId, String parcelaNum, double perimeter, double area,
                    Geometry geometry, Owner owner, String parish, String county, String island) {

        // Properties attributes
        this.parcelaId = parcelaId;
        this.parcelaNum = parcelaNum;
        this.perimeter = perimeter;
        this.area = area;
        this.geometry = geometry;
        this.owner = owner;
        this.parish = parish;
        this.county = county;
        this.island = island;
        neighbourProperties = new ArrayList<>();

        // Update Owners info
        owner.addProperty(this);
    }

    public void addNeighbour(Property property){
        if(property == null)
            throw new IllegalArgumentException("Property cannot be null");
        if(!neighbourProperties.contains(property))
            neighbourProperties.add(property);
    }

    // Getters
    @Override
    public String toString() {
        return "Property{" +
                "owner=" + owner +
                ", parcela=" + parcelaId +
                ", neighbourProperties=" + neighbourProperties.size() +
                '}';
    }

    public String getParcelaId() { return parcelaId; }

    public String getParcelaNum() { return parcelaNum; }

    public Geometry getGeometry() { return geometry; }

    public double getArea() { return area; }

    public double getPerimeter() { return perimeter; }

    public String getParish() { return parish; }

    public String getCounty() { return county; }

    public String getIsland() { return island; }

    public Owner getOwner() { return owner; }

    public List<Property> getNeighbourProperties() { return neighbourProperties; }

}