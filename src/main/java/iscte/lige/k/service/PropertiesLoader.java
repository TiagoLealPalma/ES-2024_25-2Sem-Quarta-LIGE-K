package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.SimplerProperty;
import iscte.lige.k.dataStructures.Trade;
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

    public Map<Integer, Owner> owners = new HashMap<Integer, Owner>();
    private List<Property> properties = new ArrayList<Property>();
    private List<SimplerProperty> simplerProperties = new ArrayList<>();
    private Map<String, List<Property>> propertyMapByFreguesia = new HashMap<>();
    private List<Trade> trades = new ArrayList<>();

    private List<String> freguesias = new ArrayList<>();
    private List<String> munincipios = new ArrayList<>();
    private List<String> ilhas = new ArrayList<>();
    private Map<String, List<String>> mapMunicipioToFreguesia = new HashMap<>();

    private String freguesia = null;

    // Context bundle needed to load Trades page
    public class TradesPageBundle {
        private String freguesia;
        private List<Property> properties;
        private List<Trade> trades;

        public TradesPageBundle(String freguesia, List<Property> properties, List<Trade> trades){
            this.freguesia = freguesia;
            this.properties = properties;
            this.trades = trades;
        }

        public String getFreguesia() { return freguesia; }

        public List<Property> getProperties() { return properties; }

        public List<Trade> getTrades() { return trades; }
    }

    // On initialization, parse and calculate the data structures with the csv file
    // on src/main/resources
    private PropertiesLoader(){
        File csv = new File("src/main/resources/Madeira-Moodle-1.1.csv");
        if (!csv.exists()) {
            System.err.println("Csv file does not exist, or path is incorrect");
            return;
        }

        try(Scanner s = new Scanner(csv)){
            WKTReader reader = new WKTReader();
            s.nextLine();

            // Loading bar in terminal
            System.err.println("\nParsing properties from csv...");
            int totalComparisons = 35000;
            int progress = 0;
            int barLength = 100;

            int DEVELPOMENT_LIMIT = 1000;

            while(s.hasNextLine() && DEVELPOMENT_LIMIT != 0){
                String line = s.nextLine();
                String[] data = line.split(";");

                // Convert to Geometry so the neighbours can be found
                Geometry geometry = reader.read(data[5]);

                // Check for new freguesia, munincipios e ilhas
                if(!freguesias.contains(data[7])) freguesias.add(data[7]);
                if(!munincipios.contains(data[8])) munincipios.add(data[8]);
                if(!ilhas.contains(data[9])) ilhas.add(data[9]);

                mapMunicipioToFreguesia.computeIfAbsent(data[8], k -> new ArrayList<>());
                if(!mapMunicipioToFreguesia.get(data[8]).contains(data[7]))
                    mapMunicipioToFreguesia.get(data[8]).add(data[7]);


                // Check if the Owner is already present in the list
                Owner owner = owners.get(Integer.parseInt(data[6]));
                if(owner == null) {
                    owner = new Owner(data[6]);
                    owners.put(Integer.parseInt(data[6]), owner);
                }

                try {
                    Double area = Double.parseDouble(data[3]);
                    Double price = Double.parseDouble(data[4]);

                    // Insert property data into struct
                    Property p = new Property(data[1], data[2], area, price, geometry,
                            owner, data[7], data[8], data[9]);

                    // Add property to properties list.
                    properties.add(p);

                    // Add do dictionary <Freguesia, List<Property>>
                    if (propertyMapByFreguesia.containsKey(data[7]))
                        propertyMapByFreguesia.get(data[7]).add(p); // Adicionar no mapa da freguesia
                    else {
                        List<Property> list = new ArrayList<>();
                        list.add(p);
                        propertyMapByFreguesia.put(data[7], list);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in CSV: " + Arrays.toString(data));
                }

                progress++;
                int percent = (int) ((progress / (double) totalComparisons) * 100);
                int filled = (int) ((progress / (double) totalComparisons) * barLength);
                String bar = "[" + "#".repeat(filled) + " ".repeat(barLength - filled) + "] " + percent + "%";

                //System.out.print("\r" + bar);

                //DEVELPOMENT_LIMIT--;
            }

            int totalProperties = properties.size();
            for(Owner o : owners.values()){
                for(Property p : o.getProperties()) totalProperties--;
            }
            assert totalProperties == 0; // Check consistency between properties in list and connected to owners

            // Insert data into a spatially driven tree so it drives the magnitude of connect method down
            STRtree index = new STRtree();
            for (Property p : properties) {
                index.insert(p.getGeometry().getEnvelopeInternal(), p);
            }

            // Run through the list and connect properties which geometry touches
            connectNeighbours(index);
            trades = TradeService_MR.getTradesList(owners.values().stream().toList());
            buildSimplerProperties();
           // calculateAllAvgAreas();
            System.err.print("Building SVGs");
            //buildSVG();

            // Inform that the instance is fully loaded and safe to use
            synchronized (this) {
                loaded = true;
                this.notifyAll();
                System.err.println("PropertiesLoader Instance has been fully loaded.");
            }

        } catch (ParseException | FileNotFoundException e) {
            System.err.println("Error parsing geometry: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
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


    private void calculateAllAvgAreas() {
        for (Owner o : owners.values()){
            o.calculateAvgArea();
        }
    }


    private void connectNeighbours(STRtree index) {
        System.err.println("\nCalculating property neighbours...");
        int totalComparisons = properties.size();
        int progress = 0;
        int barLength = 100; // Número de caracteres na barra

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
            progress++;
            int percent = (int) ((progress / (double) totalComparisons) * 100);
            int filled = (int) ((progress / (double) totalComparisons) * barLength);
            String bar = "[" + "#".repeat(filled) + " ".repeat(barLength - filled) + "] " + percent + "%";

            //System.out.print("\r" + bar);
        }
    }

    public List<Property> getPropertiesWithNeighbours () {
        checkLocked();

        System.err.println("getneighwithneigh: " + freguesia); // DEBUG
        List<Property> propertyList = new ArrayList<>();

        if(getFreguesias().contains(freguesia)) {
            // Get properties
            for (Property p : propertyMapByFreguesia.get(freguesia)) {
                if (!p.getNeighbourProperties().isEmpty())
                    propertyList.add(p);
            }
        } else {
            System.err.println("Freguesia nao foi reconhecida, ou outros ou todos: " + freguesia);
            return null;
        }

        return propertyList; // DEBUG, remover depois
    }

    public List<Trade> getTrades (String freguesia){
        checkLocked();

        List<Trade> filteredTrades = new ArrayList<>();

        if(getFreguesias().contains(freguesia)) {
            filteredTrades = trades.stream()
                    .filter(t -> t.getOwner1Property().getFreguesia().equals(freguesia) &&
                            t.getOwner2Property().getFreguesia().equals(freguesia))
                    .toList();
        } else {
            System.err.println("Freguesia nao foi reconhecida, ou outros ou todos: " + freguesia);
            return null;
        }

        return filteredTrades; // DEBUG, remover depois
    }




    public Map<String, List<Property>> getPropertyMapByFreguesia() { return propertyMapByFreguesia; }

    public List<String> getFreguesias(){
        checkLocked();

        List<String> result = new ArrayList<>(propertyMapByFreguesia.keySet().stream().sorted().toList());
        result.remove("NA");
        return result;
    }

    public void setLoadingOptions(String freguesia){
        checkLocked();
        this.freguesia = freguesia;
    }

    public void buildSimplerProperties(){
        System.err.println("Building simpler versions of properties");
        for (Property property : properties){
            if(!property.getGeometry().isValid() || property.getGeometry().isEmpty())
                continue;
            SimplerProperty p = new SimplerProperty(property);
            simplerProperties.add(p);
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

    public List<String> getMunincipios() {
        checkLocked();
        return munincipios;
    }

    public List<String> getIlhas() {
        checkLocked();
        return ilhas;
    }

    public List<String> getFreguesiasUnfiltered() {
        checkLocked();
        return freguesias;
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
