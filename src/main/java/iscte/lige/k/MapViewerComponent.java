package iscte.lige.k;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

import java.util.List;

@Tag("leaflet-map-viewer")
@JsModule("./leaflet-map-viewer.js")
public class MapViewerComponent extends Div {

    public MapViewerComponent() {
        sendGeoJsonToJs();
    }

    public void sendGeoJsonToJs() {
        PropertiesLoader loader = new PropertiesLoader();
        List<Property> properties = loader.getPropertiesWithNeighbours();

        JsonArray jsonFeatures = new JsonArray();

        for (Property p : properties) {
            JsonArray coordinates = new JsonArray();

            for (var coord : p.geometry.getCoordinates()) {
                JsonArray latlon = new JsonArray();
                latlon.add(coord.getY()); // latitude
                latlon.add(coord.getX()); // longitude
                coordinates.add(latlon);
            }

            JsonObject feature = new JsonObject();
            feature.add("coordinates", coordinates);
            feature.addProperty("owner", p.owner.getName());
            feature.addProperty("area", p.area);
            feature.addProperty("freguesia", p.freguesia);
            jsonFeatures.add(feature);
        }

        JsonObject geoData = new JsonObject();
        geoData.add("features", jsonFeatures);

        System.err.println("Enviando dados de mapa para JS...");
        getElement().setProperty("geoData", geoData.toString());
    }
}
