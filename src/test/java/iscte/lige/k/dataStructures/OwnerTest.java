package iscte.lige.k.dataStructures;

import junit.framework.TestCase;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

public class OwnerTest extends TestCase {

    @Test
    public void testAddProperty() {
        fail("Not yet implemented");
    }

    private Geometry parseGeometry(String wkt) {
        try {
            return new WKTReader().read(wkt);
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testAverageAreaWithUniqueProperties() {
        Owner owner = new Owner("1");

        Property p1 = new Property("1", "A", 0.0, 100.0,  parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 0.0, 200.0,  parseGeometry("POLYGON((2 0, 2 1, 3 1, 3 0, 2 0))"), owner, "", "", "");

        double result = owner.calculateAvgArea();

        assertEquals(150.0, result);
    }

    @Test
    public void testAverageAreaWithTouchingProperties() {
        Owner owner = new Owner("2");

        Property p1 = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), owner, "", "", "");

        double result = owner.calculateAvgArea();
        assertEquals(200.0, result);
    }

    @Test
    public void testMixedProperties() {
        Owner owner = new Owner("3");

        Property p1 = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), owner, "", "", "");
        Property p3 = new Property("3", "A", 0.0, 300.0, parseGeometry("POLYGON((5 5, 5 6, 6 6, 6 5, 5 5))"), owner, "", "", "");

        double result = owner.calculateAvgArea();
        // Grupo de p1 + p2 = 200 (1 grupo)
        // p3 é único = 300
        // Média = (200 + 300) / 2 = 250
        assertEquals(250.0, result);
    }

    @Test
    public void testNoProperties() {
        Owner owner = new Owner("4");
        double result = owner.calculateAvgArea();
        assertEquals(0.0, result);
    }

    public void testTestGetName() {
        fail("Not yet implemented");
    }

    public void testGetProperties() {
        fail("Not yet implemented");
    }

    public void testGetNumOfProperties() {
        fail("Not yet implemented");
    }

    public void testTestToString() {
        fail("Not yet implemented");
    }
}