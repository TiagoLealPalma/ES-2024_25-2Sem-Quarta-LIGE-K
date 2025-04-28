package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;
import iscte.lige.k.dataStructures.SimplerProperty;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PropertiesLoaderTest {

    private static PropertiesLoader loader;

    @BeforeClass
    public static void setup() {
        loader = PropertiesLoader.getInstance();
    }

    @Test
    public void testGetInstance() {
        assertNotNull(loader);
        assertSame(loader, PropertiesLoader.getInstance());
    }

    @Test
    public void testSetAndGetLoadingOptions() {
        String[] options = {"freguesia", "Fajã da Ovelha"};
        loader.setLoadingOptions(options);
        assertArrayEquals(options, loader.getLoadingOptions());
    }

    @Test
    public void testGetPropertiesWithNeighbours() {
        loader.setLoadingOptions(new String[]{"freguesia", "Fajã da Ovelha"});
        List<Property> properties = loader.getPropertiesWithNeighbours();
        assertNotNull(properties);
        assertFalse(properties.isEmpty());

        loader.setLoadingOptions(new String[]{"concelho", "Santana"});
        properties = loader.getPropertiesWithNeighbours();
        assertNotNull(properties);
        assertFalse(properties.isEmpty());

        loader.setLoadingOptions(new String[]{"ilha", "NA"});
        properties = loader.getPropertiesWithNeighbours();
        assertNotNull(properties);
        assertFalse(properties.isEmpty());

        loader.setLoadingOptions(new String[]{"ilha", "asdasdasd"});
        properties = loader.getPropertiesWithNeighbours();
        assertNull(properties);
    }

    @Test
    public void testGetTrades() {
        // Teste com freguesia válida
        loader.setLoadingOptions(new String[]{"freguesia", "Fajã da Ovelha"});
        List<Trade> trades = loader.getTrades();
        assertNotNull(trades);

        // Teste com concelho válido
        loader.setLoadingOptions(new String[]{"concelho", "Santana"});
        trades = loader.getTrades();
        assertNotNull(trades);

        // Teste com ilha válida
        loader.setLoadingOptions(new String[]{"ilha", "Madeira"});
        trades = loader.getTrades();
        assertNotNull(trades);

        // Teste com valor inválido (nenhuma trade esperada)
        loader.setLoadingOptions(new String[]{"ilha", "inexistente"});
        trades = loader.getTrades();
        assertNotNull(trades);  // A função devolve sempre uma lista, mesmo que vazia
        assertTrue(trades.isEmpty());

    }

    @Test
    public void testGetPropertiesByParish() {
        Map<String, List<Property>> propertiesByParish = loader.getPropertiesByParish();
        assertNotNull(propertiesByParish);
        assertFalse(propertiesByParish.isEmpty());
    }

    @Test
    public void testGetOwnerRelationships() {
        Map<Owner, Map<Owner, Integer>> relationships = loader.getOwnerRelationships();
        assertNotNull(relationships);
    }

    @Test
    public void testGenerateSimplerProperties() {
        List<SimplerProperty> simplerProperties = loader.getSimplerProperties();
        assertNotNull(simplerProperties);
        assertFalse(simplerProperties.isEmpty());
    }

    @Test
    public void testBuildOwnersNeighbouringRelations() {
        Map<Owner, Map<Owner, Integer>> relations = loader.getOwnerRelationships();
        for (Map.Entry<Owner, Map<Owner, Integer>> entry : relations.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
        }
    }

    @Test
    public void testGetOwners() {
        Map<Integer, Owner> owners = loader.getOwners();
        assertNotNull(owners);
        assertFalse(owners.isEmpty());
    }

    @Test
    public void testGetCounties() {
        List<String> counties = loader.getCounties();
        assertNotNull(counties);
        assertFalse(counties.isEmpty());
    }

    @Test
    public void testGetIslands() {
        List<String> islands = loader.getIslands();
        assertNotNull(islands);
        assertFalse(islands.isEmpty());
    }

    @Test
    public void testGetParishes() {
        List<String> parishes = loader.getParishes();
        assertNotNull(parishes);
        assertFalse(parishes.isEmpty());
    }

    @Test
    public void testGetSimplerProperties() {
        List<SimplerProperty> simplerProps = loader.getSimplerProperties();
        assertNotNull(simplerProps);
        for (SimplerProperty sp : simplerProps) {
            assertTrue(sp.getEntryNumber().length() >= 5);
        }
    }
}
