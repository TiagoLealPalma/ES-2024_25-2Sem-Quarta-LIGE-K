package iscte.lige.k.dataStructures;

import iscte.lige.k.service.TradeEval;

import java.util.Objects;

/**
 * Represents a land trade between two owners, each exchanging one property.
 * Evaluates the trade's score and determines the "main" property of each owner —
 * the one to which the acquired property will be merged.
 *
 * Trades can be compared, scored, and evaluated for validity (must have neighboring links).
 */
public class Trade implements Comparable<Trade> {
    private final Owner owner1;
    private final Owner owner2;
    private final Property owner1Property;
    private final Property owner2Property;
    private final Property owner1MainProperty; // Main property for owner1 after trade
    private final Property owner2MainProperty; // Main property for owner2 after trade

    private int score = -1;
    private final double totalAreaBeingTraded;

    /**
     * Creates a new Trade between two owners involving one property from each.
     * Automatically evaluates the trade and finds the main properties for merging.
     *
     * @param owner1 first owner
     * @param owner2 second owner
     * @param p1 property from owner1 to be traded
     * @param p2 property from owner2 to be traded
     * @throws IllegalStateException if no valid "main" properties can be found for merging
     */
    public Trade(Owner owner1, Owner owner2, Property p1, Property p2) {
        this.owner1 = owner1;
        this.owner2 = owner2;
        this.owner1Property = p1;
        this.owner2Property = p2;
        this.totalAreaBeingTraded = p1.getArea() + p2.getArea();

        // Score is computed externally via static evaluator
        TradeEval.evaluateTrade(this);

        // Find the property owned by owner1 that is adjacent to p2 (and vice-versa)
        this.owner1MainProperty = findMainProperty(owner1, p2);
        this.owner2MainProperty = findMainProperty(owner2, p1);

        // Check if the trade is valid (main properties must exist and be different from the traded ones)
        if (owner1MainProperty == null || owner2MainProperty == null ||
                owner1Property == owner2MainProperty || owner2Property == owner1MainProperty) {
            throw new IllegalStateException("No main property found: " +
                    "Owner1MainProperty = " + owner1MainProperty +
                    ", Owner2MainProperty = " + owner2MainProperty);
        }
    }

    /**
     * Finds a property owned by {@code ownerGettingTheProperty} that is a neighbor
     * of {@code propertyGettingMerged}.
     *
     * @param ownerGettingTheProperty owner who would receive the property
     * @param propertyGettingMerged property being merged into the owner's set
     * @return the neighboring property owned by the owner, or null if none found
     */
    private Property findMainProperty(Owner ownerGettingTheProperty, Property propertyGettingMerged) {
        for (Property property : propertyGettingMerged.getNeighbourProperties()) {
            if (property.getOwner().equals(ownerGettingTheProperty))
                return property;
        }
        return null;
    }

    /**
     * Sets the trade's evaluation score.
     * @param score integer score assigned to this trade
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Determines if two trades are equal, regardless of the order of participants.
     * A trade is equal if the same properties and owners are involved.
     *
     * @param obj the other trade
     * @return true if both trades are considered equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Trade other = (Trade) obj;

        boolean sameOrder = owner1.getName().equals(other.getOwner1().getName()) &&
                owner2.getName().equals(other.getOwner2().getName()) &&
                Objects.equals(owner1Property.getParcelaId(), other.getOwner1Property().getParcelaId()) &&
                Objects.equals(owner2Property.getParcelaId(), other.getOwner2Property().getParcelaId());

        boolean swappedOrder = owner1.getName().equals(other.getOwner2().getName()) &&
                owner2.getName().equals(other.getOwner1().getName()) &&
                Objects.equals(owner1Property.getParcelaId(), other.getOwner2Property().getParcelaId()) &&
                Objects.equals(owner2Property.getParcelaId(), other.getOwner1Property().getParcelaId());

        return sameOrder || swappedOrder;
    }

    /**
     * Computes a hash code for the trade using parcel IDs,
     * ensuring equal trades (regardless of order) get the same hash.
     *
     * @return consistent hash code
     */
    @Override
    public int hashCode() {
        int parcel1 = (int) Double.parseDouble(owner1Property.getParcelaId());
        int parcel2 = (int) Double.parseDouble(owner2Property.getParcelaId());

        return Objects.hash(
                Math.min(parcel1, parcel2),
                Math.max(parcel1, parcel2));
    }

    /**
     * Compares two trades based on score (higher is better), breaking ties
     * by favoring larger total traded area.
     *
     * @param otherTrade the trade to compare with
     * @return comparison result for sorting
     */
    @Override
    public int compareTo(Trade otherTrade) {
        if (this.score == otherTrade.getScore()) {
            return -Double.compare(this.totalAreaBeingTraded, otherTrade.getTotalArea());
        } else {
            return -Integer.compare(this.score, otherTrade.getScore());
        }
    }

    // Getters

    /**
     * Gets the total area being exchanged in this trade.
     * @return total area
     */
    public double getTotalArea() {
        return totalAreaBeingTraded;
    }

    /**
     * Gets the score assigned to this trade.
     * @return trade score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Gets the first owner in this trade.
     * @return owner1
     */
    public Owner getOwner1() {
        return owner1;
    }

    /**
     * Gets the second owner in this trade.
     * @return owner2
     */
    public Owner getOwner2() {
        return owner2;
    }

    /**
     * Gets the property owned by owner1 that is being traded.
     * @return property from owner1
     */
    public Property getOwner1Property() {
        return owner1Property;
    }

    /**
     * Gets the property owned by owner2 that is being traded.
     * @return property from owner2
     */
    public Property getOwner2Property() {
        return owner2Property;
    }

    /**
     * Gets the property from owner1 that will be merged with the incoming property.
     * @return owner1's main property
     */
    public Property getOwner1MainProperty() {
        return owner1MainProperty;
    }

    /**
     * Gets the property from owner2 that will be merged with the incoming property.
     * @return owner2's main property
     */
    public Property getOwner2MainProperty() {
        return owner2MainProperty;
    }

    /**
     * Gets the sum of the areas of both properties being traded.
     * @return sum of the areas
     */
    public double getTotalAreaBeingTraded() {
        return totalAreaBeingTraded;
    }

    /**
     * Returns a string summary of the trade, showing both owners and traded parcel IDs.
     * @return string representation of the trade
     */
    @Override
    public String toString() {
        return owner1.getName() + ": " + owner1Property.getParcelaId()
                + " <-> " +
                owner2.getName() + ": " + owner2Property.getParcelaId();
    }
}
