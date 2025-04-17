package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
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
    public void testEvaluateTradeWithEqualAreas() {
        Owner o1 = new Owner("Ana");
        Owner o2 = new Owner("Bruno");

        Property p1 = new Property("1", "A", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("2", "B", 100.0, 100.0, parseGeometry("POLYGON((2 0, 2 1, 3 1, 3 0, 2 0))"), o2, "", "", "");

        Trade trade = new Trade(o1, o2, p1, p2);
        TradeEval.evaluateTrade(trade);

        assertEquals(100, trade.getScore());
    }

    @Test
    public void testEvaluateTradeWithDifferentAreas() {
        Owner o1 = new Owner("Carlos");
        Owner o2 = new Owner("Diana");

        Property p1 = new Property("3", "C", 100.0, 100.0, parseGeometry("POLYGON((0 0, 0 2, 2 2, 2 0, 0 0))"), o1, "", "", "");
        Property p2 = new Property("4", "D", 50.0, 50.0, parseGeometry("POLYGON((3 0, 3 1, 4 1, 4 0, 3 0))"), o2, "", "", "");

        Trade trade = new Trade(o1, o2, p1, p2);
        TradeEval.evaluateTrade(trade);

        // Score = (1 - |100-50| / 150) * 100 = (1 - 50/150) * 100 = (2/3)*100 = 66
        assertEquals(66, trade.getScore());
    }

    @Test
    public void testEvaluateTradeWithExtremeDifference() {
        Owner o1 = new Owner("Eva");
        Owner o2 = new Owner("Filipe");

        // Propriedade com área 0 (muito pequena)
        Property p1 = new Property("5", "E", 0.0, 0.0, parseGeometry("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))"), o1, "", "", "");

        // Propriedade com área 100 (grande)
        Property p2 = new Property("6", "F", 100.0, 100.0, parseGeometry("POLYGON((1 0, 1 2, 3 2, 3 0, 1 0))"), o2, "", "", "");

        // Criar a troca entre as propriedades
        Trade trade = new Trade(o1, o2, p1, p2);
        TradeEval.evaluateTrade(trade);

        // Esperado: Diferença extrema (90% ou mais) -> Score = 0
        assertEquals(0, trade.getScore());
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

