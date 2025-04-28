package iscte.lige.k.dataStructures;

import junit.framework.TestCase;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.List;

import static org.junit.Assert.assertThrows;

public class OwnerTest extends TestCase {

    private Geometry parseGeometry(String wkt) {
        try {
            return new WKTReader().read(wkt);
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testAddProperty() {
        Owner owner = new Owner("TestOwner");
        Property p = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");


        assertEquals(1, owner.getNumOfProperties());
        assertTrue(owner.getProperties().contains(p));
    }

    @Test
    public void testAddNullPropertyThrowsException() {
        Owner owner = new Owner("Joana");
        assertThrows(IllegalArgumentException.class, () -> {
            owner.addProperty(null);
        });
    }


    @Test
    public void testOwnerConstructorWithProperty() throws Exception {
        Geometry g = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");

        // Criar propriedade sem usar o construtor automático que adiciona ao owner,
        // para evitar adicionar duas vezes.
        Owner tempOwner = new Owner("Temp"); // usamos temporariamente para instanciar
        Property p = new Property("999", "123", 10.0, 100.0, g, tempOwner, "Parish", "County", "Island");

        // Agora criamos o owner usando o construtor que recebe a propriedade
        Owner owner = new Owner("João", p);

        assertEquals("João", owner.getName());
        assertEquals(1, owner.getNumOfProperties());
        assertEquals(1, owner.getProperties().size());
        assertEquals(p, owner.getProperties().get(0));
    }

    @Test
    public void testAverageAreaWithUniqueProperties() {
        Owner owner = new Owner("1");

        Property p1 = new Property("1", "A", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 200.0, 200.0, parseGeometry("POLYGON((2 0, 2 1, 3 1, 3 0, 2 0))"), owner, "", "", "");

        double result = owner.calculateAvgArea();
        assertEquals(150.0, result);
    }

    @Test
    public void testAverageAreaWithTouchingProperties() {
        Owner owner = new Owner("2");

        Property p1 = new Property("1", "A", 0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), owner, "", "", "");

        double result = owner.calculateAvgArea();
        assertEquals(200.0, result);

    }

    @Test
    public void testMixedProperties() {
        Owner owner = new Owner("3");

        Property p1 = new Property("1", "A", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 100.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), owner, "", "", "");
        Property p3 = new Property("3", "A", 300.0, 300.0, parseGeometry("POLYGON((5 5, 5 6, 6 6, 6 5, 5 5))"), owner, "", "", "");


        double result = owner.calculateAvgArea();
        assertEquals(250.0, result);
    }

    @Test
    public void testNoProperties() {
        Owner owner = new Owner("4");
        double result = owner.calculateAvgArea();
        assertEquals(0.0, result);
    }

    @Test
    public void testGetName() {
        Owner owner = new Owner("Maria");
        assertEquals("Maria", owner.getName());
    }

    @Test
    public void testGetProperties() {
        Owner owner = new Owner("TestOwner");
        Property p = new Property("1", "A", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");

        List<Property> properties = owner.getProperties();
        assertNotNull(properties);
        assertEquals(1, properties.size());
        assertEquals(p, properties.get(0));
    }

    @Test
    public void testGetNumOfProperties() {
        Owner owner = new Owner("OwnerWith2");
        Property p1 = new Property("1", "A", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");
        Property p2 = new Property("2", "A", 150.0, 150.0, parseGeometry("POLYGON((2 0, 2 1, 3 1, 3 0, 2 0))"), owner, "", "", "");


        assertEquals(2, owner.getNumOfProperties());
    }

    @Test
    public void testToStringFormat() {
        Owner owner = new Owner("ToStringTester");
        Property p = new Property("1", "A", 50.0, 50.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), owner, "", "", "");

        String result = owner.toString();
        assertTrue(result.contains("ToStringTester"));
        assertTrue(result.contains("numOfProperties=1"));
    }
}
