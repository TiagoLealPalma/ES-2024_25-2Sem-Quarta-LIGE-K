package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.*;

import static org.junit.Assert.*;

public class TradeServiceTest {

    private Geometry parseGeometry(String wkt) {
        try {
            return new WKTReader().read(wkt);
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testGetTradesListWithEligibleTrade() {
        Owner o1 = new Owner("Alice");
        Owner o2 = new Owner("Bob");

        Property p1 = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("2", "B", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), o2, "", "", "");
        Property p3 = new Property("3", "C", 0.0, 100.0, parseGeometry("POLYGON((0 1, 0 2, 1 2, 1 1, 0 1))"), o2, "", "", "");
        Property p4 = new Property("4", "D", 0.0, 100.0, parseGeometry("POLYGON((0 2, 0 3, 1 3, 1 2, 0 2))"), o1, "", "", "");

        p1.addNeighbour(p2);
        p1.addNeighbour(p3);
        p2.addNeighbour(p1);
        p3.addNeighbour(p1);
        p3.addNeighbour(p4);
        p4.addNeighbour(p3);

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1, o2));
        assertNotNull(trades);
        assertFalse(trades.isEmpty());

        Trade t = trades.get(0);
        assertNotNull(t.getOwner1());
        assertNotNull(t.getOwner2());
        assertNotNull(t.getOwner1Property());
        assertNotNull(t.getOwner2Property());
    }

    @Test
    public void testGetTradesListWithNoTrade() {
        Owner o1 = new Owner("Carlos");
        Owner o2 = new Owner("Diana");

        Property p1 = new Property("4", "D", 0.0, 100.0, parseGeometry("POLYGON((10 10, 10 11, 11 11, 11 10, 10 10))"), o1, "", "", "");
        Property p2 = new Property("5", "E", 0.0, 100.0, parseGeometry("POLYGON((20 20, 20 21, 21 21, 21 20, 20 20))"), o2, "", "", "");

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1, o2));
        assertNotNull(trades);
        assertTrue(trades.isEmpty());
    }

    @Test
    public void testSelfTradeIsIgnored() {
        Owner o1 = new Owner("Elena");

        Property p1 = new Property("6", "F", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("7", "G", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), o1, "", "", "");

        p1.addNeighbour(p2);
        p2.addNeighbour(p1);

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1));
        assertNotNull(trades);
        assertTrue(trades.isEmpty());
    }

    @Test
    public void testNoTradeForDirectNeighbours() {
        Owner o1 = new Owner("Ana");
        Owner o2 = new Owner("Bruno");

        Property p1 = new Property("1", "X", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("2", "Y", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), o2, "", "", "");

        p1.addNeighbour(p2);
        p2.addNeighbour(p1);

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1, o2));
        assertTrue(trades.isEmpty());
    }

    @Test
    public void testMultipleOwnersWithTrades() {
        Owner o1 = new Owner("Alice");
        Owner o2 = new Owner("Bob");
        Owner o3 = new Owner("Charlie");

        // Alice: p1, p4
        // Charlie: p3, p5
        Property p1 = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p4 = new Property("4", "D", 0.0, 100.0, parseGeometry("POLYGON((0 2, 0 3, 1 3, 1 2, 0 2))"), o1, "", "", "");

        // Bob: p2 (sem ligação útil aqui)
        Property p2 = new Property("2", "B", 0.0, 100.0, parseGeometry("POLYGON((10 0, 10 1, 11 1, 11 0, 10 0))"), o2, "", "", "");

        // Charlie: p3, p5
        Property p3 = new Property("3", "C", 0.0, 100.0, parseGeometry("POLYGON((0 1, 0 2, 1 2, 1 1, 0 1))"), o3, "", "", "");
        Property p5 = new Property("5", "E", 0.0, 100.0, parseGeometry("POLYGON((1 2, 1 3, 2 3, 2 2, 1 2))"), o3, "", "", "");

        // Definir vizinhanças entre Alice e Charlie
        p1.addNeighbour(p3); // Alice - Charlie
        p3.addNeighbour(p1);

        p4.addNeighbour(p5); // Alice - Charlie
        p5.addNeighbour(p4);

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1, o2, o3));
        assertNotNull(trades);
        assertFalse(trades.isEmpty()); // Agora deve haver pelo menos uma troca
    }


    @Test
    public void testAvoidDuplicateTrades() {
        Owner o1 = new Owner("Eva");
        Owner o2 = new Owner("Luis");

        Property p1 = new Property("1", "A", 0.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("2", "B", 0.0, 100.0, parseGeometry("POLYGON((1 0, 1 1, 2 1, 2 0, 1 0))"), o2, "", "", "");
        Property p3 = new Property("3", "C", 0.0, 100.0, parseGeometry("POLYGON((0 1, 0 2, 1 2, 1 1, 0 1))"), o2, "", "", "");

        p1.addNeighbour(p2);
        p1.addNeighbour(p3);
        p2.addNeighbour(p1);
        p3.addNeighbour(p1);

        List<Trade> trades = TradeService.getTradesList(Arrays.asList(o1, o2));
        Set<Trade> uniqueTrades = new HashSet<>(trades);

        assertEquals(uniqueTrades.size(), trades.size()); // Não deve haver duplicatas
    }
}
