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


// Singleton instance loaded whenever the program is booted
public class PropertiesLoader {

    private static PropertiesLoader instance = null;
    private static boolean loaded = false;

    private Map<Integer, Owner> owners = new HashMap<Integer, Owner>();
    private HashMap<Owner, Map<Owner, Integer>> ownersNeighbouringRelations = new HashMap<>();


    private List<Property> properties = new ArrayList<Property>();
    private List<SimplerProperty> simplerProperties = new ArrayList<>();


    private List<String> parishes = new ArrayList<>();
    private List<String> counties = new ArrayList<>();
    private List<String> islands = new ArrayList<>();
    private Map<String, List<Property>> propertiesByParish = new HashMap<>();
    private Map<String, List<Property>> propertiesByCounty = new HashMap<>();
    private Map<String, List<Property>> propertiesByIsland = new HashMap<>();

    private Map<String, List<String>> mapMunicipioToFreguesia = new HashMap<>();
    private List<Trade> trades = new ArrayList<>();

    // loadingOptions[0] = criteria  && loadingOptions[1] = value
    private String[] loadingOptions = {"null","null"};  // i.e loadingOptions[0] = "freguesia" && loadingOptions[1] = "Fajã de Ovelha"


    private PropertiesLoader(){
        // On initialization, parse and calculate the data structures with the csv file
        // on src/main/resources (generalized later)
        parseData("src/main/resources/Madeira-Moodle-1.1.csv");

        // Build needed structures
        connectNeighbours();
        generateSimplerProperties();
        buildOwnersNeighbouringRelations();
        calculateTrades();

        // Inform that the instance is fully loaded and safe to use
        synchronized (this) {
            loaded = true;
            this.notifyAll();
            System.err.println("PropertiesLoader Instance has been fully loaded.");
        }
    }

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

    public static synchronized PropertiesLoader getInstance(){
        if(instance == null){
            instance = new PropertiesLoader();
        }
        return instance;
    }

    private void parseData(String path){
        File csv = new File(path);

        if (!csv.exists()) {
            System.err.println("Csv file does not exist, or path is incorrect");
            return;
        }

        try(Scanner s = new Scanner(csv)){
            WKTReader reader = new WKTReader();
            s.nextLine();


            while(s.hasNextLine()) {
                String line = s.nextLine();
                String[] data = line.split(";");

                // Check if the Owner is already present in the list
                Owner owner = owners.computeIfAbsent(Integer.parseInt(data[6]), k -> new Owner(data[6]));

                // Convert to Geometry so the neighbours can be found
                Geometry geometry = reader.read(data[5]);

                double area = Double.parseDouble(data[3]);
                double price = Double.parseDouble(data[4]);

                // Insert property data into struct
                Property p = new Property(data[1], data[2], area, price, geometry,
                        owner, data[7], data[8], data[9]);

                // Add property to properties list.
                properties.add(p);



                // Check for new parish, county or island
                if (!parishes.contains(data[7])) parishes.add(data[7]);
                if (!counties.contains(data[8])) counties.add(data[8]);
                if (!islands.contains(data[9])) islands.add(data[9]);

                mapMunicipioToFreguesia.computeIfAbsent(data[8], k -> new ArrayList<>());
                if (!mapMunicipioToFreguesia.get(data[8]).contains(data[7]))
                    mapMunicipioToFreguesia.get(data[8]).add(data[7]);

                // Add do dictionary <Freguesia, List<Property>>
                propertiesByParish.computeIfAbsent(data[7], k -> new ArrayList<>()).add(p);
                propertiesByCounty.computeIfAbsent(data[8], k -> new ArrayList<>()).add(p);
                propertiesByIsland.computeIfAbsent(data[9], k -> new ArrayList<>()).add(p);

            }
        } catch (NumberFormatException | ParseException | FileNotFoundException e) {
            throw new IllegalStateException("Something went wrong while initializing PropertiesLoader: " + e);
        }
    }

    private void buildSVG() throws Exception {
        // Exemplo de acesso:
        // Para cada município, acede a todas as freguesias e vai buscar os dados no mapa principal
        List<SimplerProperty> properties = getSimplerProperties();
        SVGGenerator.exportPropertiesToSVG(properties,"null","null");
        for (String municipio : mapMunicipioToFreguesia.keySet()) {
            // Case municipio selection, freguesia null
            SVGGenerator.exportPropertiesToSVG(properties,municipio,"null");
            for (String freguesia : mapMunicipioToFreguesia.get(municipio)) {
                SVGGenerator.exportPropertiesToSVG(properties,municipio,freguesia); // LEMBRAR NO FINAL DE FAZER PARA FREGUESIA NULA
            }
        }
    }

    private void connectNeighbours() {
        // Insert data into a spatially driven tree, so it drives the magnitude of connect method down (Only compares with nearby properties)
        STRtree index = new STRtree();
        for (Property p : properties) {
            index.insert(p.getGeometry().getEnvelopeInternal(), p);
        }

        for (Property p1 : properties) {
            Geometry g1 = p1.getGeometry().buffer(0); // Clean faulty geometry
            List<Property> candidates = index.query(p1.getGeometry().getEnvelopeInternal());
            for (Property p2: candidates) {
                if(p1.equals(p2)) continue;
                Geometry g2 = p2.getGeometry().buffer(0);
                if (g1.intersects(g2) || g1.touches(g2)) {
                    p1.addNeighbour(p2);
                    p2.addNeighbour(p1);
                }
            }
        }
    }

    // Return a properties list based on the current criteria and value on the loading options array
    public List<Property> getPropertiesWithNeighbours () {
        checkLocked();

        System.err.println("Getting properties for following criteria: " + loadingOptions[0] + ":" + loadingOptions[1]); // DEBUG
        List<Property> propertiesList = null;
        String criteria = loadingOptions[0];
        String value = loadingOptions[1];

        // According to the criteria, select the correct list of properties
        switch (criteria) {
            case "ilha":
                propertiesList = propertiesByIsland.get(value);
                break;
            case "concelho":
                propertiesList = propertiesByCounty.get(value);
                break;
            case "freguesia":
                propertiesList = propertiesByParish.get(value);
                break;
            default:
                throw new RuntimeException("Unknown property criteria: " + criteria);
        }

        if(propertiesList == null) {
            System.err.println("No properties found for following value: " + value);
            return null;
        }

        return propertiesList
                .stream()
                .filter((t)-> !t.getNeighbourProperties().isEmpty())
                .toList();
    }
    private void calculateTrades() {
        this.trades = TradeService.getTradesList(owners.values().stream().toList());
    }

    // Return a trades list based on the current criteria and value on the loading options array
    public List<Trade> getTrades (){
        checkLocked();

        String criteria = loadingOptions[0];
        String value = loadingOptions[1];


        List<Trade> filteredTrades = switch (criteria) {
            case "ilha" -> trades.stream()
                    .filter(t -> t.getOwner1Property().getIsland().equals(value) &&
                            t.getOwner2Property().getIsland().equals(value))
                    .toList();

            case "concelho" -> trades.stream()
                    .filter(t -> t.getOwner1Property().getCounty().equals(value) &&
                            t.getOwner2Property().getCounty().equals(value))
                    .toList();

            case "freguesia" -> trades.stream()
                    .filter(t -> t.getOwner1Property().getParish().equals(value) &&
                            t.getOwner2Property().getParish().equals(value))
                    .toList();

            default -> null;
        };

        if (filteredTrades == null || filteredTrades.isEmpty()) {
            System.err.println("No trades found for given value: " + value);
        }

        return filteredTrades;
    }



    public Map<String, List<Property>> getPropertiesByParish() { return propertiesByParish; }

    public HashMap<Owner, Map<Owner, Integer>> getOwnerRelationships() {
        checkLocked();
        return this.ownersNeighbouringRelations;
    }

    public void setLoadingOptions(String[] options){
        checkLocked();
        if(!"proprietariosfreguesiaconcelhoilha".toLowerCase().contains(options[0].toLowerCase())) // maybe change for a more predefined way
            throw new IllegalArgumentException("Criterio inserido não é valido.");

        this.loadingOptions = options;

    }

    public void generateSimplerProperties(){
        System.err.println("Building simpler versions of properties");
        for (Property property : properties){
            if(!property.getGeometry().isValid() || property.getGeometry().isEmpty())
                continue;
            SimplerProperty p = new SimplerProperty(property);
            simplerProperties.add(p);
        }
    }

    public void buildOwnersNeighbouringRelations(){
        HashMap<Owner, Map<Owner, Integer>> ownersNeighbouringRelations = new HashMap<>();
        for (Owner owner : owners.values()) {
            HashMap <Owner, Integer> relation = new HashMap<>();
            ownersNeighbouringRelations.put(owner, relation);
            for (Property property : owner.getProperties()) {
                for (Property neighbour : property.getNeighbourProperties()) {
                    if(!neighbour.getOwner().equals(owner))
                        relation.merge(neighbour.getOwner(), 1, Integer::sum);
                }
            }
        }

        this.ownersNeighbouringRelations = ownersNeighbouringRelations;
    }



    public Map<Integer, Owner> getOwners(){
        checkLocked();
        return this.owners;
    }

    public String[] getLoadingOptions(){
        return loadingOptions;
    }

    public List<String> getCounties() {
        checkLocked();
        return propertiesByCounty.keySet().stream().sorted().toList();
    }

    public List<String> getIslands() {
        checkLocked();
        return propertiesByIsland.keySet().stream().sorted().toList();
    }

    public List<String> getParishes() {
        checkLocked();
        return propertiesByParish.keySet().stream().sorted().toList();
    }

    public List<SimplerProperty> getSimplerProperties() {
        return simplerProperties.stream()
                .filter(t -> (!(Double.parseDouble(t.getEntryNumber().substring(0,5).replace(",", ".")) > 3.5)))
                .toList(); // Filters porto santo
    }

    public static void main(String[] args) {
        PropertiesLoader p = new PropertiesLoader();

    }
}
