package iscte.lige.k.dataStructures;

/**
 * A simplified representation of a Property, used for
 * data visualization, summaries, or lightweight data processing.
 *
 * Stores centroid coordinates, logarithmic area-based value,
 * administrative info and the parcel number.
 *
 * Currently, this class is being used to simplify the creation of the svgs,
 * but serves as a lightweight format of the {@link Property} class.
 */
public class SimplerProperty {
    private String entryNumber;

    // Geolocation
    private double x;
    private double y;
    private double value;

    // Location
    private String parish;
    private String county;

    /**
     * Constructs a simplified property from a full {@link Property} instance.
     * Extracts the centroid coordinates, computes a logarithmic value from area,
     * and stores minimal identifying and location data.
     *
     * @param property the original Property object
     */
    public SimplerProperty(Property property) {
        this.entryNumber = property.getParcelaNum();
        this.x = property.getGeometry().getCentroid().getX();
        this.y = property.getGeometry().getCentroid().getY();
        this.parish = property.getParish();
        this.county = property.getCounty();
        this.value = Math.log(Math.max(property.getArea(), 1)); // Log scale for normalized value
    }

    /**
     * Gets the X coordinate of the property's centroid.
     * @return x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the Y coordinate of the property's centroid.
     * @return y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the logarithmic area-based value used for simplified representation.
     * @return value (log(area))
     */
    public double getValue() {
        return value;
    }

    /**
     * Gets the parish name.
     * @return parish
     */
    public String getParish() {
        return parish;
    }

    /**
     * Gets the county name.
     * @return county
     */
    public String getCounty() {
        return county;
    }

    /**
     * Gets the registry entry number.
     * @return entry number
     */
    public String getEntryNumber() {
        return entryNumber;
    }
}
