package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.SimplerProperty;
import iscte.lige.k.dataStructures.Trade;
import iscte.lige.k.util.SVGGenerator;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.index.strtree.STRtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Singleton class responsible for loading, parsing and managing all property-related data.
 * Parses input CSV, builds neighbour relations, creates simplified property versions,
 * generates trades, and provides filtered access based on administrative divisions.
 */
public class PropertiesLoader {

    // ---------------------------------------------------------------------------------------------
    // Singleton control
    // ---------------------------------------------------------------------------------------------
    private static PropertiesLoader instance = null;
    private static boolean loaded = false;

    // ---------------------------------------------------------------------------------------------
    // Core data structures
    // ---------------------------------------------------------------------------------------------
    private Map<Integer, Owner> owners                                      = new HashMap<>();
    private HashMap<Owner, Map<Owner, Integer>> ownersNeighbouringRelations = new HashMap<>();
    private List<Property> properties                                       = new ArrayList<>();
    private List<SimplerProperty> simplerProperties                         = new ArrayList<>();
    private List<Trade> trades                                              = new ArrayList<>();

    // ---------------------------------------------------------------------------------------------
    // Administrative groupings
    // ---------------------------------------------------------------------------------------------
    private List<String> parishes                                           = new ArrayList<>();
    private List<String> counties                                           = new ArrayList<>();
    private List<String> islands                                            = new ArrayList<>();
    private Map<String, List<Property>> propertiesByParish                  = new HashMap<>();
    private Map<String, List<Property>> propertiesByCounty                  = new HashMap<>();
    private Map<String, List<Property>> propertiesByIsland                  = new HashMap<>();
    private Map<String, List<String>> mapMunicipioToFreguesia               = new HashMap<>();

    // ---------------------------------------------------------------------------------------------
    // Current filtering options
    // ---------------------------------------------------------------------------------------------
    private String[] loadingOptions = {"null", "null"};  // [criteria, value], e.g. ["freguesia", "Fajã de Ovelha"]

    // ---------------------------------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------------------------------

    /**
     * Private constructor: parses CSV, builds all data structures, and marks loader as ready.
     */
    private PropertiesLoader() {
        parseData("src/main/resources/Madeira-Moodle-1.1.csv");
        connectNeighbours();
        generateSimplerProperties();
        buildOwnersNeighbouringRelations();
        calculateTrades();

        synchronized (this) {
            loaded = true;
            this.notifyAll();
            System.err.println("PropertiesLoader instance fully loaded.");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Singleton Access
    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the singleton instance of PropertiesLoader, initializing it on first access.
     *
     * @return the shared PropertiesLoader instance
     */
    public static synchronized PropertiesLoader getInstance() {
        if (instance == null) {
            instance = new PropertiesLoader();
        }
        return instance;
    }

    // ---------------------------------------------------------------------------------------------
    // Public API – Configuration & Retrieval
    // ---------------------------------------------------------------------------------------------

    /**
     * Sets filtering criteria for subsequent data retrievals.
     *
     * @param options two-element array: [criteria, value]
     * @throws IllegalArgumentException if the criteria is not one of "proprietarios", "freguesia", "concelho", "ilha"
     */
    public void setLoadingOptions(String[] options) {
        checkLocked();
        String crit = options[0].toLowerCase();
        if (!Arrays.asList("proprietarios", "freguesia", "concelho", "ilha").contains(crit)) {
            throw new IllegalArgumentException("Invalid filtering criteria: " + options[0]);
        }

        List<String> valuesList = null;
        switch (options[1]){
            case "freguesia":
                valuesList = getParishes();
                break;
            case "concelho":
                valuesList = getCounties();
                break;
            case "ilha":
                valuesList = getIslands();
                break;
            default:
        }

        // If null -> owners else valid criteria, check if contains the value
        if (valuesList != null && !valuesList.contains(options[1])) {
            System.err.println("Invalid value for the given criteria: " + options[0] + " : " + options[1]);
            return;
        }
        this.loadingOptions = options;
    }

    /**
     * Returns the current filtering criteria and value.
     *
     * @return two-element array: [criteria, value]
     */
    public String[] getLoadingOptions() {
        return loadingOptions.clone();
    }

    /**
     * Retrieves all owners mapped by their ID.
     *
     * @return Map of owner ID to Owner
     */
    public Map<Integer, Owner> getOwners() {
        checkLocked();
        return Collections.unmodifiableMap(owners);
    }

    /**
     * Retrieves the neighbour‐relationship counts between owners.
     *
     * @return Map of Owner to map of neighbouring Owner counts
     */
    public Map<Owner, Map<Owner, Integer>> getOwnerRelationships() {
        checkLocked();
        return Collections.unmodifiableMap(ownersNeighbouringRelations);
    }

    /**
     * Retrieves all available islands.
     *
     * @return sorted List of island names
     */
    public List<String> getIslands() {
        checkLocked();
        return propertiesByIsland.keySet().stream().sorted().toList();
    }

    /**
     * Retrieves all available counties.
     *
     * @return sorted List of county names
     */
    public List<String> getCounties() {
        checkLocked();
        return propertiesByCounty.keySet().stream().sorted().toList();
    }

    /**
     * Retrieves all available parishes.
     *
     * @return sorted List of parish names
     */
    public List<String> getParishes() {
        checkLocked();
        return propertiesByParish.keySet().stream().sorted().toList();
    }

    /**
     * Retrieves the mapping of parish to its list of properties.
     *
     * @return Map of parish name to List of Property
     */
    public Map<String, List<Property>> getPropertiesByParish() {
        checkLocked();
        return Collections.unmodifiableMap(propertiesByParish);
    }

    /**
     * Retrieves the mapping of county to its list of properties.
     *
     * @return Map of county name to List of Property
     */
    public Map<String, List<Property>> getPropertiesByCounty() {
        checkLocked();
        return Collections.unmodifiableMap(propertiesByCounty);
    }

    /**
     * Retrieves the mapping of island to its list of properties.
     *
     * @return Map of island name to List of Property
     */
    public Map<String, List<Property>> getPropertiesByIsland() {
        checkLocked();
        return Collections.unmodifiableMap(propertiesByIsland);
    }

    /**
     * Retrieves a filtered list of properties that have at least one neighbour,
     * according to the current loadingOptions.
     *
     * @return List of Property with neighbours; or null if none found
     */
    public List<Property> getPropertiesWithNeighbours() {
        checkLocked();
        String criteria = loadingOptions[0];
        String value    = loadingOptions[1];
        List<Property> list;

        switch (criteria) {
            case "ilha"     -> list = propertiesByIsland.get(value);
            case "concelho" -> list = propertiesByCounty.get(value);
            case "freguesia"-> list = propertiesByParish.get(value);
            default         -> throw new RuntimeException("Unknown criteria: " + criteria);
        }

        if (list == null) {
            System.err.println("No properties for " + criteria + ": " + value);
            return null;
        }

        return list.stream()
                .filter(p -> !p.getNeighbourProperties().isEmpty())
                .toList();
    }

    /**
     * Retrieves a filtered list of trades between owners,
     * according to the current loadingOptions.
     *
     * @return List of Trade; may be empty
     */
    public List<Trade> getTrades() {
        checkLocked();
        String criteria = loadingOptions[0];
        String value    = loadingOptions[1];

        List<Trade> filtered = switch (criteria) {
            case "ilha"     -> trades.stream()
                    .filter(t -> t.getOwner1Property().getIsland().equals(value)
                            && t.getOwner2Property().getIsland().equals(value))
                    .toList();
            case "concelho" -> trades.stream()
                    .filter(t -> t.getOwner1Property().getCounty().equals(value)
                            && t.getOwner2Property().getCounty().equals(value))
                    .toList();
            case "freguesia"-> trades.stream()
                    .filter(t -> t.getOwner1Property().getParish().equals(value)
                            && t.getOwner2Property().getParish().equals(value))
                    .toList();
            default         -> Collections.emptyList();
        };

        if (filtered.isEmpty()) {
            System.err.println("No trades for " + criteria + ": " + value);
        }
        return filtered;
    }

    /**
     * Exports all properties to SVG grouped by municipality and parish.
     *
     * @throws Exception on any export failure
     */
    public void buildSVG() throws Exception {
        List<SimplerProperty> simp = getSimplerProperties();
        SVGGenerator.exportPropertiesToSVG(simp, "null", "null");
        for (String muni : mapMunicipioToFreguesia.keySet()) {
            SVGGenerator.exportPropertiesToSVG(simp, muni, "null");
            for (String freg : mapMunicipioToFreguesia.get(muni)) {
                SVGGenerator.exportPropertiesToSVG(simp, muni, freg);
            }
        }
    }

    /**
     * Returns the simplified property list for optimized processing.
     *
     * @return List of SimplerProperty
     */
    public List<SimplerProperty> getSimplerProperties() {
        checkLocked();
        return simplerProperties.stream()
                .filter(sp -> {
                    double entry = Double.parseDouble(
                            sp.getEntryNumber().substring(0, 5).replace(",", ".")
                    );
                    return entry <= 3.5;  // filter out Porto Santo by coords
                })
                .toList();
    }

    // ---------------------------------------------------------------------------------------------
    // Private helpers – data loading and structure building
    // ---------------------------------------------------------------------------------------------

    /**
     * Blocks until the instance is fully loaded.
     */
    private void checkLocked() {
        synchronized (this) {
            while (!loaded) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Parses the CSV file into owners, properties, and admin groupings.
     */
    private void parseData(String path) {
        File csv = new File(path);
        if (!csv.exists()) {
            System.err.println("CSV file not found or path incorrect.");
            return;
        }

        try (Scanner scanner = new Scanner(csv)) {
            WKTReader reader = new WKTReader();
            scanner.nextLine(); // skip header

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                Owner owner    = owners.computeIfAbsent(
                        Integer.parseInt(data[6]),
                        id -> new Owner(data[6])
                );
                Geometry geom  = reader.read(data[5]);
                double area    = Double.parseDouble(data[3]);
                double price   = Double.parseDouble(data[4]);

                Property prop = new Property(
                        data[1], data[2], area, price, geom,
                        owner, data[7], data[8], data[9]
                );
                properties.add(prop);

                // collect administrative names
                if (!parishes.contains(data[7]))  parishes.add(data[7]);
                if (!counties.contains(data[8]))  counties.add(data[8]);
                if (!islands.contains(data[9]))   islands.add(data[9]);

                mapMunicipioToFreguesia
                        .computeIfAbsent(data[8], k -> new ArrayList<>())
                        .add(data[7]);
                propertiesByParish
                        .computeIfAbsent(data[7], k -> new ArrayList<>())
                        .add(prop);
                propertiesByCounty
                        .computeIfAbsent(data[8], k -> new ArrayList<>())
                        .add(prop);
                propertiesByIsland
                        .computeIfAbsent(data[9], k -> new ArrayList<>())
                        .add(prop);
            }
        } catch (NumberFormatException | ParseException | FileNotFoundException e) {
            throw new IllegalStateException("Failed initializing PropertiesLoader: " + e, e);
        }
    }

    /**
     * Builds neighbour relationships by spatial indexing and geometry checks.
     */
    private void connectNeighbours() {
        STRtree index = new STRtree();
        for (Property p : properties) {
            index.insert(p.getGeometry().getEnvelopeInternal(), p);
        }
        for (Property p1 : properties) {
            Geometry g1 = p1.getGeometry().buffer(0);
            List<Property> candidates = index.query(p1.getGeometry().getEnvelopeInternal());
            for (Property p2 : candidates) {
                if (p1 == p2) continue;
                Geometry g2 = p2.getGeometry().buffer(0);
                if (g1.intersects(g2) || g1.touches(g2)) {
                    p1.addNeighbour(p2);
                    p2.addNeighbour(p1);
                }
            }
        }
    }

    /**
     * Generates simplified versions of properties for quicker rendering.
     */
    private void generateSimplerProperties() {
        System.err.println("Building simpler versions of properties...");
        for (Property p : properties) {
            if (p.getGeometry().isValid() && !p.getGeometry().isEmpty()) {
                simplerProperties.add(new SimplerProperty(p));
            }
        }
    }

    /**
     * Builds map of owner‐neighbour relationships (owner adjacency counts).
     */
    private void buildOwnersNeighbouringRelations() {
        HashMap<Owner, Map<Owner, Integer>> rels = new HashMap<>();
        for (Owner o : owners.values()) {
            Map<Owner, Integer> map = new HashMap<>();
            for (Property p : o.getProperties()) {
                for (Property n : p.getNeighbourProperties()) {
                    if (!n.getOwner().equals(o)) {
                        map.merge(n.getOwner(), 1, Integer::sum);
                    }
                }
            }
            rels.put(o, map);
        }
        this.ownersNeighbouringRelations = rels;
    }

    /**
     * Calculates potential trades based on owner property matches.
     */
    private void calculateTrades() {
        this.trades = TradeService.getTradesList(
                new ArrayList<>(owners.values())
        );
    }

    // ---------------------------------------------------------------------------------------------
    // Main (for quick debugging / testing)
    // ---------------------------------------------------------------------------------------------

    /**
     * Entry point to trigger SVG exports (debug / ad‐hoc usage).
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        PropertiesLoader loader = new PropertiesLoader();
        try {
            loader.buildSVG();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // By property
    public int getAvgArea() {
        List<Property> propertiesBeingLoaded = getPropertiesWithNeighbours();
        if (propertiesBeingLoaded.isEmpty()) return 0;

        int sum = 0;

        for (Property p : propertiesBeingLoaded) {
            sum += p.getArea();
        }
        System.err.println((int)(sum / propertiesBeingLoaded.size()));
        return (int)(sum / propertiesBeingLoaded.size());
    }

    public int getAvgAreaByOwner(){
        List<Property> propertiesBeingLoaded = getPropertiesWithNeighbours();
        if (propertiesBeingLoaded.isEmpty()) return 0;

        int sum = 0;
        int counter = 0;

        Set<Owner> uniqueOwners= new HashSet<>();

        for (Property p : propertiesBeingLoaded) {
            if(uniqueOwners.contains(p.getOwner()))
                continue;

            Owner owner = p.getOwner();
            uniqueOwners.add(owner);
            sum += owner.calculateAvgArea();
            counter++;

        }
        System.err.println((int)(sum / counter));
        return (int)(sum / counter);
    }
}
