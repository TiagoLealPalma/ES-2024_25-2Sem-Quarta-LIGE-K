package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
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
    private Map<String, List<Property>> propertyMapByFreguesia = new HashMap<>();

    private String freguesia = null;

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
           // calculateAllAvgAreas();

            // Inform that the instance is fully loaded and safe to use
            synchronized (this) {
                loaded = true;
                this.notifyAll();
                System.err.println("PropertiesLoader Instance has been fully loaded.");
            }

        } catch (ParseException | FileNotFoundException e) {
            System.err.println("Error parsing geometry: " + e.getMessage());
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

        System.err.println("getneighwithneigh: " + freguesia);
        List<Property> results = new ArrayList<>();

        if(getFreguesias().contains(freguesia)) {
            for (Property p : propertyMapByFreguesia.get(freguesia)) {
                if (!p.getNeighbourProperties().isEmpty())
                    results.add(p);
            }
        }else System.err.println("Freguesia nao foi reconhecida, ou outros ou todos: " + freguesia);
        return results; // DEBUG, remover depois
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


    public static void main(String[] args) {
        PropertiesLoader p = new PropertiesLoader();

        for (String s : p.getPropertyMapByFreguesia().keySet()) {
            System.err.println(s);
            System.err.println(p.getPropertyMapByFreguesia().get(s).size());
        }
    }
}
