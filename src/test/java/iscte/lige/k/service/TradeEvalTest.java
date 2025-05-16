package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;
import iscte.lige.k.util.TradeEval;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import static org.junit.Assert.*;

public class TradeEvalTest {

    private Geometry parseGeometry(String wkt) {
        try {
            return new WKTReader().read(wkt);
        } catch (Exception e) {
            fail("Erro ao criar geometria: " + e.getMessage());
            return null;
        }
    }

    @Test
    public void testEvaluateTradeWithEqualAreas() throws ParseException {
        Geometry g1 = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");
        Geometry g2 = new WKTReader().read("POLYGON((2 0, 2 2, 4 2, 4 0, 2 0))");
        Geometry g3 = new WKTReader().read("POLYGON((1 1, 1 3, 3 3, 3 1, 1 1))");
        Geometry g4 = new WKTReader().read("POLYGON((3 1, 3 3, 5 3, 5 1, 3 1))");

        Owner owner1 = new Owner("Alice");
        Owner owner2 = new Owner("Bob");

        Property p1 = new Property("1", "001", 10.0, 100.0, g1, owner1, "P1", "C1", "I1");
        Property p2 = new Property("2", "002", 12.0, 100.0, g2, owner2, "P1", "C1", "I1");

        Property neighbour1 = new Property("3", "003", 14.0, 140.0, g3, owner1, "P1", "C1", "I1");
        Property neighbour2 = new Property("4", "004", 16.0, 160.0, g4, owner2, "P1", "C1", "I1");

        // Estabelecer vizinhança obrigatória para o trade ser válido
        p2.addNeighbour(neighbour1); // owner1 ganha p2
        p1.addNeighbour(neighbour2); // owner2 ganha p1

        Trade trade = new Trade(owner1, owner2, p1, p2);
        TradeEval.evaluateTrade(trade);

        assertEquals(100, trade.getScore());
    }

    @Test
    public void testEvaluateTradeWithDifferentAreas() throws ParseException {
        Geometry g1 = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");
        Geometry g2 = new WKTReader().read("POLYGON((2 0, 2 2, 4 2, 4 0, 2 0))");
        Geometry g3 = new WKTReader().read("POLYGON((1 1, 1 3, 3 3, 3 1, 1 1))");
        Geometry g4 = new WKTReader().read("POLYGON((3 1, 3 3, 5 3, 5 1, 3 1))");

        Owner owner1 = new Owner("Alice");
        Owner owner2 = new Owner("Bob");

        Property p1 = new Property("1", "001", 10.0, 100.0, g1, owner1, "P1", "C1", "I1");
        Property p2 = new Property("2", "002", 12.0, 50.0, g2, owner2, "P1", "C1", "I1");

        Property neighbour1 = new Property("3", "003", 14.0, 140.0, g3, owner1, "P1", "C1", "I1");
        Property neighbour2 = new Property("4", "004", 16.0, 160.0, g4, owner2, "P1", "C1", "I1");

        // Estabelecer vizinhança obrigatória para o trade ser válido
        p2.addNeighbour(neighbour1); // owner1 ganha p2
        p1.addNeighbour(neighbour2); // owner2 ganha p1

        Trade trade = new Trade(owner1, owner2, p1, p2);
        TradeEval.evaluateTrade(trade);

        // Score = (1 - |100-50| / 150) * 100 = (1 - 50/150) * 100 = (2/3)*100 = 66
        assertEquals(66, trade.getScore());
    }

    @Test
    public void testEvaluateTradeWithExtremeDifference() throws ParseException {
        Geometry g1 = new WKTReader().read("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))");
        Geometry g2 = new WKTReader().read("POLYGON((2 0, 2 2, 4 2, 4 0, 2 0))");
        Geometry g3 = new WKTReader().read("POLYGON((1 1, 1 3, 3 3, 3 1, 1 1))");
        Geometry g4 = new WKTReader().read("POLYGON((3 1, 3 3, 5 3, 5 1, 3 1))");

        Owner owner1 = new Owner("Alice");
        Owner owner2 = new Owner("Bob");

        Property p1 = new Property("1", "001", 10.0, 100.0, g1, owner1, "P1", "C1", "I1");
        Property p2 = new Property("2", "002", 12.0, 1200.0, g2, owner2, "P1", "C1", "I1");

        Property neighbour1 = new Property("3", "003", 14.0, 140.0, g3, owner1, "P1", "C1", "I1");
        Property neighbour2 = new Property("4", "004", 16.0, 160.0, g4, owner2, "P1", "C1", "I1");

        // Estabelecer vizinhança obrigatória para o trade ser válido
        p2.addNeighbour(neighbour1); // owner1 ganha p2
        p1.addNeighbour(neighbour2); // owner2 ganha p1

        Trade trade = new Trade(owner1, owner2, p1, p2);
        TradeEval.evaluateTrade(trade);

        assertEquals(15, trade.getScore());
    }


    @Test
    public void testIntToColorAtZero() {
        String color = TradeEval.IntToColor(0);
        assertEquals("rgba(255, 0, 0, 0.3)", color);
    }

    @Test
    public void testIntToColorAtFifty() {
        String color = TradeEval.IntToColor(50);
        assertEquals("rgba(255, 255, 0, 0.3)", color);
    }

    @Test
    public void testIntToColorAtHundred() {
        String color = TradeEval.IntToColor(100);
        assertEquals("rgba(0, 255, 0, 0.3)", color);
    }

    @Test
    public void testIntToColorWithNegativeValue() {
        String color = TradeEval.IntToColor(-10);
        assertEquals("rgba(255, 0, 0, 0.3)", color); // deve tratar como 0
    }

    @Test
    public void testIntToColorWithValueOver100() {
        String color = TradeEval.IntToColor(150);
        assertEquals("rgba(0, 255, 0, 0.3)", color); // deve tratar como 100
    }
}

