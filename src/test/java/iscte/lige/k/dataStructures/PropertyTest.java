package iscte.lige.k.dataStructures;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.List;

import static org.junit.Assert.*;

public class PropertyTest {

    private Owner owner;
    private Property property;
    private Geometry geometry;

    @Before
    public void setup() {
        try {
            geometry = new WKTReader().read("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))");
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
        }
        owner = new Owner("TestOwner");
        property = new Property("123", "1", 10.0, 100.0, geometry, owner, "ParishX", "CountyY", "IslandZ");
    }

    @Test
    public void testAddNeighbour() {
        Property neighbour = new Property("456", "2", 12.0, 80.0, geometry, new Owner("NeighbourOwner"), "ParishX", "CountyY", "IslandZ");
        property.addNeighbour(neighbour);
        property.addNeighbour(neighbour);

        List<Property> neighbours = property.getNeighbourProperties();
        assertEquals(1, neighbours.size());
        assertEquals(neighbour, neighbours.get(0));

    }


    @Test(expected = IllegalArgumentException.class)
    public void testAddNullNeighbourThrowsException() {
        property.addNeighbour(null);
    }

    @Test
    public void testToString() {
        String result = property.toString();
        assertTrue(result.contains("name='TestOwner'"));
        assertTrue(result.contains("parcela=123"));
        assertTrue(result.contains("neighbourProperties=0"));
    }

    @Test
    public void getParcelaId() {
        assertEquals("123", property.getParcelaId());
    }

    @Test
    public void getParcelaNum() {
        assertEquals("1", property.getParcelaNum());
    }

    @Test
    public void getGeometry() {
        assertEquals(geometry, property.getGeometry());
    }

    @Test
    public void getArea() {
        assertEquals(100.0, property.getArea(), 0.001);
    }

    @Test
    public void getPerimeter() {
        assertEquals(10.0, property.getPerimeter(), 0.001);
    }

    @Test
    public void getParish() {
        assertEquals("ParishX", property.getParish());
    }

    @Test
    public void getCounty() {
        assertEquals("CountyY", property.getCounty());
    }

    @Test
    public void getIsland() {
        assertEquals("IslandZ", property.getIsland());
    }

    @Test
    public void getOwner() {
        assertEquals(owner, property.getOwner());
    }

    @Test
    public void getNeighbourProperties() {
        assertNotNull(property.getNeighbourProperties());
        assertTrue(property.getNeighbourProperties().isEmpty());
    }
}
