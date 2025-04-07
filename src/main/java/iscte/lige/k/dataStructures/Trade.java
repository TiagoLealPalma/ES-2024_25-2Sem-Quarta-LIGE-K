package iscte.lige.k.dataStructures;

import iscte.lige.k.service.TradeEval;

import java.util.Objects;

public class Trade implements Comparable<Trade> {
    private Owner owner1;
    private Owner owner2;
    private Property owner1Property;
    private Property owner2Property;
    private Property owner1MainProperty; // Property that's being merged with the owner 2 property when traded
    private Property owner2MainProperty; // Property that's being merged with the owner 2 property when traded

    private int score = -1;

    private double totalAreaBeingTraded;

    public Trade(Owner owner1, Owner owner2, Property p1, Property p2) {
        this.owner1 = owner1;
        this.owner2 = owner2;
        this.owner1Property = p1;
        this.owner2Property = p2;
        this.totalAreaBeingTraded = p1.getArea() + p2.getArea();
        TradeEval.evaluateTrade(this);
        this.owner1MainProperty = findMainProperty(owner1, p2);
        this.owner2MainProperty = findMainProperty(owner2, p1);

        if(owner1MainProperty == null || owner2MainProperty == null || owner1Property == owner2MainProperty || owner2Property == owner1MainProperty) {
            throw new IllegalStateException("No main property found: " +
                    "Owner1MainProperty = " + owner1MainProperty +
                    ", Owner2MainProperty = " + owner2MainProperty);
        }
    }

    private Property findMainProperty(Owner ownerGettingTheProperty, Property propertyGettingMerged) {
        for (Property property : propertyGettingMerged.neighbourProperties){
            if (property.getOwner().equals(ownerGettingTheProperty))
                return property;
        }
        return null;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // Se for a mesma instância, retorna true
        if (obj == null || getClass() != obj.getClass()) return false; // Se for null ou de outra classe, retorna false

        Trade other = (Trade) obj;

        // Verifica se a troca é a mesma, independentemente da ordem dos Owners
        boolean sameOrder = owner1.getName().equals(other.getOwner1().getName()) &&
                owner2.getName().equals(other.getOwner2().getName()) &&
                owner1Property.getParcelaId() == other.getOwner1Property().getParcelaId() &&
                owner2Property.getParcelaId() == other.getOwner2Property().getParcelaId();

        boolean swappedOrder = owner1.getName().equals(other.getOwner2().getName()) &&
                owner2.getName().equals(other.getOwner1().getName()) &&
                owner1Property.getParcelaId() == other.getOwner2Property().getParcelaId() &&
                owner2Property.getParcelaId() == other.getOwner1Property().getParcelaId();

        return sameOrder || swappedOrder;
    }

    @Override
    public int hashCode() {
        int parcel1 = (int)Double.parseDouble(owner1Property.getParcelaId());
        int parcel2 = (int)Double.parseDouble(owner2Property.getParcelaId());

        return Objects.hash(
                owner1.getName(),
                owner2.getName(),
                Math.min(parcel1, parcel2),
                Math.max(parcel1, parcel2));
    }

    @Override
    public int compareTo(Trade otherTrade) {
        if (this.score == -1 || otherTrade.getScore() == -1)
            throw new IllegalStateException("Cannot compare trades that haven't been" +
                    " assigned a score");

        if (this.score == otherTrade.getScore()) { // Tiebreaker
            return -(Double.compare(this.totalAreaBeingTraded, otherTrade.getTotalArea()));
        } else { // Default comparing parameter
            return -(Integer.compare(this.score, otherTrade.getScore()));
        }
    }

    public double getTotalArea() {
        return totalAreaBeingTraded;
    }

    public int getScore() {
        return this.score;
    }

    public Owner getOwner1() {
        return owner1;
    }

    public Owner getOwner2() {
        return owner2;
    }

    public Property getOwner1Property() {
        return owner1Property;
    }

    public Property getOwner2Property() {
        return owner2Property;
    }

    public double getTotalAreaBeingTraded() {
        return totalAreaBeingTraded;
    }

    public Property getOwner1MainProperty() { return owner1MainProperty; }

    public Property getOwner2MainProperty() { return owner2MainProperty; }

    @Override
    public String toString() {
        return  owner1.getName() + ": " + owner1Property.getParcelaId()
                + " <-> " +
                owner2.getName() + ": " + owner2Property.getParcelaId();
    }


}
