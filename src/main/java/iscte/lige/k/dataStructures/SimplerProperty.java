package iscte.lige.k.dataStructures;


// For representation purposes
public class SimplerProperty {
    private String entryNumber;

    private double x;
    private double y;
    private double value;

    private String parish;
    private String county;

    public SimplerProperty(Property property) {
        this.entryNumber = property.getParcelaNum();
        this.x = property.getGeometry().getCentroid().getX();
        this.y = property.getGeometry().getCentroid().getY();
        this.parish = property.getParish();
        this.county = property.getCounty();
        this.value = Math.log(Math.max(property.getArea(), 1));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getValue() {
        return value;
    }

    public String getParish() {
        return parish;
    }

    public String getCounty() {
        return county;
    }

    public String getEntryNumber() {
        return entryNumber;
    }
}
