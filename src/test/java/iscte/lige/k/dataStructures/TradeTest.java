package iscte.lige.k.dataStructures;

import iscte.lige.k.service.TradeEval;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import static org.junit.Assert.*;

public class TradeTest {

    private Owner owner1;
    private Owner owner2;
    private Property p1;
    private Property p2;
    private Property neighbour1;
    private Property neighbour2;
    private Trade trade;

    @Before
    public void setUp() throws Exception {
        Geometry g1 = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");
        Geometry g2 = new WKTReader().read("POLYGON((2 0, 2 2, 4 2, 4 0, 2 0))");
        Geometry g3 = new WKTReader().read("POLYGON((1 1, 1 3, 3 3, 3 1, 1 1))");
        Geometry g4 = new WKTReader().read("POLYGON((3 1, 3 3, 5 3, 5 1, 3 1))");

        owner1 = new Owner("Alice");
        owner2 = new Owner("Bob");

        p1 = new Property("1", "001", 10.0, 100.0, g1, owner1, "P1", "C1", "I1");
        p2 = new Property("2", "002", 12.0, 120.0, g2, owner2, "P1", "C1", "I1");

        neighbour1 = new Property("3", "003", 14.0, 140.0, g3, owner1, "P1", "C1", "I1");
        neighbour2 = new Property("4", "004", 16.0, 160.0, g4, owner2, "P1", "C1", "I1");

        // Estabelecer vizinhança obrigatória para o trade ser válido
        p2.addNeighbour(neighbour1); // owner1 ganha p2
        p1.addNeighbour(neighbour2); // owner2 ganha p1

        trade = new Trade(owner1, owner2, p1, p2);
    }

    @Test(expected = IllegalStateException.class)
    public void testTradeWithoutValidMainPropertiesShouldThrow() throws Exception {
        // Geometrias afastadas — não são vizinhas
        Geometry g1 = new WKTReader().read("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))");
        Geometry g2 = new WKTReader().read("POLYGON((10 10, 10 11, 11 11, 11 10, 10 10))");

        Owner o1 = new Owner("Owner1");
        Owner o2 = new Owner("Owner2");

        Property p1 = new Property("1", "001", 10.0, 50.0, g1, o1, "P1", "C1", "I1");
        Property p2 = new Property("2", "002", 10.0, 60.0, g2, o2, "P1", "C1", "I1");

        // Aqui não criamos nenhuma relação de vizinhança — não são vizinhas!
        new Trade(o1, o2, p1, p2); // Deve lançar IllegalStateException
    }


    @Test
    public void setScore() {
        trade.setScore(80);
        assertEquals(80, trade.getScore());
    }

    @Test
    public void testEquals() {
        Trade same = new Trade(owner1, owner2, p1, p2);
        assertEquals(trade, same);

        Trade reversed = new Trade(owner2, owner1, p2, p1);
        assertEquals(trade, reversed);
    }

    @Test
    public void testHashCode() {
        Trade t1 = new Trade(owner1, owner2, p1, p2);
        Trade t2 = new Trade(owner2, owner1, p2, p1);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void compareTo() {
        Trade another = new Trade(owner1, owner2, p1, p2);
        trade.setScore(90);
        another.setScore(75);

        assertTrue(trade.compareTo(another) < 0); // because we negate the comparison
    }

    @Test
    public void getTotalArea() {
        assertEquals(220.0, trade.getTotalArea(), 0.001);
    }

    @Test
    public void getScore() {
        trade.setScore(50);
        assertEquals(50, trade.getScore());
    }

    @Test
    public void getOwner1() {
        assertEquals(owner1, trade.getOwner1());
    }

    @Test
    public void getOwner2() {
        assertEquals(owner2, trade.getOwner2());
    }

    @Test
    public void getOwner1Property() {
        assertEquals(p1, trade.getOwner1Property());
    }

    @Test
    public void getOwner2Property() {
        assertEquals(p2, trade.getOwner2Property());
    }

    @Test
    public void getTotalAreaBeingTraded() {
        assertEquals(220.0, trade.getTotalAreaBeingTraded(), 0.001);
    }

    @Test
    public void getOwner1MainProperty() {
        assertEquals(neighbour1, trade.getOwner1MainProperty());
    }

    @Test
    public void getOwner2MainProperty() {
        assertEquals(neighbour2, trade.getOwner2MainProperty());
    }

    @Test
    public void testToString() {
        String s = trade.toString();
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("Bob"));
        assertTrue(s.contains("1"));
        assertTrue(s.contains("2"));
    }
}
