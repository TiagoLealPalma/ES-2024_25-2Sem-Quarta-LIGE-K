package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.List;

public class Property {
    // Identifiers
    String parcelaId;
    String parcelaNum;

    // Geometric data
    Geometry geometry;
    double area;
    double perimeter;

    // Location data
    String freguesia;
    String municipio;
    String ilha;

    Owner owner;
    List<Property> neighbourProperties;

    public Property(String parcelaId, String parcelaNum, double perimeter, double area,
                    Geometry geometry, Owner owner, String freguesia, String municipio, String ilha) {

        // Properties atributes
        this.parcelaId = parcelaId;
        this.parcelaNum = parcelaNum;
        this.perimeter = perimeter;
        this.area = area;
        this.geometry = geometry;
        this.owner = owner;
        this.freguesia = freguesia;
        this.municipio = municipio;
        this.ilha = ilha;
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

    public String getFreguesia() { return freguesia; }

    public String getMunicipio() { return municipio; }

    public String getIlha() { return ilha; }

    public Owner getOwner() { return owner; }

    public List<Property> getNeighbourProperties() { return neighbourProperties; }
}