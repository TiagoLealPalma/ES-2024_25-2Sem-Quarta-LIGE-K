package iscte.lige.k.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import iscte.lige.k.service.PropertiesLoader;
import iscte.lige.k.dataStructures.Property;

import java.util.List;

@Tag("leaflet-map-viewer")
@JsModule("./leaflet-map-viewer.js")
public class MapViewerComponent extends Div {

    public MapViewerComponent() {
        sendGeoJsonToJs();
    }

    public void sendGeoJsonToJs() {
        PropertiesLoader loader = PropertiesLoader.getInstance();
        List<Property> properties = loader.getPropertiesWithNeighbours();

        JsonArray jsonFeatures = new JsonArray();

        for (Property p : properties) {
            JsonArray coordinates = new JsonArray();

            for (var coord : p.getGeometry().getCoordinates()) {
                JsonArray latlon = new JsonArray();
                latlon.add(coord.getY()); // latitude
                latlon.add(coord.getX()); // longitude
                coordinates.add(latlon);
            }

            JsonObject feature = new JsonObject();
            feature.add("coordinates", coordinates);
            feature.addProperty("owner", p.getOwner().getName());
            feature.addProperty("area", p.getArea());
            feature.addProperty("freguesia", p.getFreguesia());
            jsonFeatures.add(feature);
        }

        JsonObject geoData = new JsonObject();
        geoData.add("features", jsonFeatures);

        System.err.println("Enviando dados de mapa para JS...");
        getElement().setProperty("geoData", geoData.toString());
    }
}
