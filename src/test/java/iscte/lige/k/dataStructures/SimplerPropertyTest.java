package iscte.lige.k.dataStructures;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import static org.junit.Assert.*;

public class SimplerPropertyTest {

    private SimplerProperty simplerProperty;
    private Property baseProperty;
    private Owner owner;
    private Geometry geometry;

    @Before
    public void setUp() {
        try {
            geometry = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
        }

        owner = new Owner("TestOwner");
        baseProperty = new Property("123", "567", 0.0, 4.0, geometry, owner, "ParishX", "CountyY", "IslandZ");
        simplerProperty = new SimplerProperty(baseProperty);
    }

    @Test
    public void getX() {
        assertEquals(1.0, simplerProperty.getX(), 0.001);
    }

    @Test
    public void getY() {
        assertEquals(1.0, simplerProperty.getY(), 0.001);
    }

    @Test
    public void getValue() {
        double expectedLog = Math.log(4.0);
        assertEquals(expectedLog, simplerProperty.getValue(), 0.001);
    }

    @Test
    public void getParish() {
        assertEquals("ParishX", simplerProperty.getParish());
    }

    @Test
    public void getCounty() {
        assertEquals("CountyY", simplerProperty.getCounty());
    }

    @Test
    public void getEntryNumber() {
        assertEquals("567", simplerProperty.getEntryNumber());
    }
}
