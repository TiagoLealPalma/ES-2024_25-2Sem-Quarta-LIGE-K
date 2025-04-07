package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;

import java.util.*;

public class TradeService_MR {
    public PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private static int countNeighbouringRelations(Owner owner, Owner otherOwner) { // Quantas vezes são eles vizinhos
        int count = 0;
        for(Property property : owner.getProperties()) {
            for(Property otherProperty : otherOwner.getProperties()) {
                if (property.getNeighbourProperties().contains(otherProperty)) {
                    count++;
                }
            }
        }
        return count;
    }


    private static List<Property> getNeighbourProperties(Owner owner, Owner otherOwner) {
        List<Property> list = new ArrayList<>();
        for (Property property : owner.getProperties()) {
            for (Property otherProperty : otherOwner.getProperties()) {
                if (property.getNeighbourProperties().contains(otherProperty)) {
                    if(!list.contains(property)) list.add(property);
                    list.add(otherProperty);

                }
            }
        }
        return list;
    }





    public static List<Trade> getTradesList(List<Owner> owners) {
        List<Trade> trades = new ArrayList<>();
        Set<Trade> tradeSet = new HashSet<>(); // Used for duplicate avoidance

        for (Owner owner : owners) {
            for (Owner otherOwner : owners) {
                if (owner.getName().equals(otherOwner.getName())) // Stops self comparison
                    continue;

                if (countNeighbouringRelations(owner, otherOwner) < 2)  // Needs two relationships to be eligible for a trade
                    continue;

                // Look for the trade
                List<Property> neighbouringProperties = getNeighbourProperties(owner, otherOwner);
                for (Property property : neighbouringProperties) {
                    for (Property otherProperty : neighbouringProperties) {

                        if (property.getOwner().getName().equals(otherProperty.getOwner().getName()))
                            continue; // Cant trade properties that share the same owner
                        if (property.getNeighbourProperties().contains(otherProperty))
                            continue; // No point trading properties that are neighbours of eachother (Corner case 4 properties in a row alternating needs handling)

                        Trade newTrade = new Trade(property.getOwner(), otherProperty.getOwner(), property, otherProperty);
                        if (tradeSet.add(newTrade)) { // Adiciona ao HashSet e verifica duplicatas
                            trades.add(newTrade);
                        }

                    }
                }

            }
        }
        return trades;
    }

    public static void main(String[] args) {
        TradeService_MR m = new TradeService_MR();
        List<Trade> tradesList = m.getTradesList(m.propertiesLoader.owners.values().stream().toList());

        if (!tradesList.isEmpty()) {
            System.out.println(tradesList.get(0)); // Imprime apenas a primeira trade
        } else {
            System.out.println("Nenhuma trade encontrada.");
        }
    }

}



