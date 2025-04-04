package iscte.lige.k.service;

import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.dataStructures.Property;
import iscte.lige.k.dataStructures.Trade;

import java.util.*;

public class TradeService_MR {
    public PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    private static int howMany (Owner owner, Owner otherOwner) { // Quantas vezes são eles vizinhos
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
                    list.add(property);
                }
            }
        }
        return list;
    }





    public static List<Trade> getTradesList(List<Owner> owners) {
        List<Trade> trades = new ArrayList<>();
        Set<Trade> tradeSet = new HashSet<>(); // Para evitar duplicatas
        int id = 0;

        for (Owner owner : owners) {
            for (Owner otherOwner : owners) {
                if (owner.getName().equals(otherOwner.getName())) continue; // Agora só compara donos diferentes

                if (howMany(owner, otherOwner) > 1) { // Só entra se houver mais de um ponto de contato
                    for (Property property : getNeighbourProperties(owner, otherOwner)) {
                        for (Property otherProperty : getNeighbourProperties(otherOwner, owner)) {

                            if (!property.getOwner().getName().equals(otherProperty.getOwner().getName())) {
                                Trade newTrade = new Trade(owner, otherOwner, property, otherProperty, id);
                                if (tradeSet.add(newTrade)) { // Adiciona ao HashSet e verifica duplicatas
                                    TradeEval.evaluateTrade(newTrade);
                                    trades.add(newTrade);
                                    id++;
                                }
                            }
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



