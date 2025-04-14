package iscte.lige.k.dataStructures;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;


// For representation purposes
public class SimplerProperty {
    private String entryNumber;
    private double x;
    private double y;
    private double value;
    private String freguesia;
    private String munincipio;
    private String ilha;

    public SimplerProperty(Property property) {
        this.entryNumber = property.getParcelaNum();
        this.x = property.getGeometry().getCentroid().getX();
        this.y = property.getGeometry().getCentroid().getY();
        this.freguesia = property.getFreguesia();
        this.munincipio = property.municipio;
        this.value = Math.log(Math.max(property.getArea(), 1));

        if(property.parcelaId.contains("7119097.0"))
            System.err.println(x);
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

    public String getFreguesia() {
        return freguesia;
    }

    public String getMunincipio() {
        return munincipio;
    }

    public String getIlha() {
        return ilha;
    }

    public String getEntryNumber() {
        return entryNumber;
    }
}
