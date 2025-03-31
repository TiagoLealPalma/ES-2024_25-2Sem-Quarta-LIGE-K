package iscte.lige.k;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.index.strtree.STRtree;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class PropertiesLoader {

    Map<Integer, Owner> owners = new HashMap<Integer, Owner>();
    List<Property> properties = new ArrayList<Property>();


    // On initialization, parse and calculate the data structures with the csv file
    // on src/main/resources
    public PropertiesLoader(){
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

            while(s.hasNextLine()){
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

                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in CSV: " + Arrays.toString(data));
                }

                progress++;
                int percent = (int) ((progress / (double) totalComparisons) * 100);
                int filled = (int) ((progress / (double) totalComparisons) * barLength);
                String bar = "[" + "#".repeat(filled) + " ".repeat(barLength - filled) + "] " + percent + "%";

                System.out.print("\r" + bar);
            }

            int totalProperties = properties.size();
            for(Owner o : owners.values()){
                for(Property p : o.getProperties()) totalProperties--;
            }
            assert totalProperties == 0; // Check consistency between properties in list and connected to owners

            // Insert data into a spatially driven tree so it drives the magnitude of connect method down
            STRtree index = new STRtree();
            for (Property p : properties) {
                index.insert(p.geometry.getEnvelopeInternal(), p);
            }

            // Run through the list and connect properties which geometry touches
            connectNeighbours(index);

        } catch (ParseException | FileNotFoundException e) {
            System.err.println("Error parsing geometry: " + e.getMessage());
        }

    }

    private void connectNeighbours(STRtree index) {
        System.err.println("\nCalculating property neighbours...");
        int totalComparisons = properties.size();
        int progress = 0;
        int barLength = 100; // Número de caracteres na barra

        for (Property p1 : properties) {
            Geometry g1 = p1.geometry.buffer(0); // Clean faulty geometry
            List<Property> candidates = index.query(p1.geometry.getEnvelopeInternal());
            for (Property p2: candidates) {
                if(p1.equals(p2)) continue;
                Geometry g2 = p2.geometry.buffer(0);
                if (p1.geometry.intersects(p2.geometry) || p1.geometry.touches(p2.geometry)) {
                    p1.addNeighbour(p2);
                    p2.addNeighbour(p1);
                }
            }
            progress++;
            int percent = (int) ((progress / (double) totalComparisons) * 100);
            int filled = (int) ((progress / (double) totalComparisons) * barLength);
            String bar = "[" + "#".repeat(filled) + " ".repeat(barLength - filled) + "] " + percent + "%";

            System.out.print("\r" + bar);
        }
    }

    public List<Property> getPropertiesWithNeighbours () {
        List<Property> results = new ArrayList<>();
        for(Property p : properties){
            if(!p.neighbourProperties.isEmpty())
                results.add(p);
        }
        return results.stream().limit(1000).toList(); // DEBUG, remover depois
    }

    public static void main(String[] args) {
        PropertiesLoader loader = new PropertiesLoader();
    }


}
