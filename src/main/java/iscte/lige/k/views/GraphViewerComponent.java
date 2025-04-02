package iscte.lige.k.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import iscte.lige.k.service.PropertiesLoader;
import iscte.lige.k.dataStructures.Property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Tag("graph-viewer")
@JsModule("./graph-viewer.js")
public class GraphViewerComponent extends Div {
    Runnable onGraphLoadedCallback;

    public GraphViewerComponent() {
        // JS irá iniciar o carregamento chamando @ClientCallable
        System.err.println("GraphViewerComponent initialized. Awaiting JS request...");
    }


    @ClientCallable
    public void graphFinishedLoading() {
        System.err.println("Graph loaded on JS");
        if (onGraphLoadedCallback != null) {
            onGraphLoadedCallback.run();
        }
    }

    public void onGraphLoaded(Runnable callback) {
        this.onGraphLoadedCallback = callback;
    }

    // Method called from JS
    @ClientCallable
    public void startLoadingOnServer() {
        System.err.println("Data requested from client. Loading graph...");
        PropertiesLoader loader = PropertiesLoader.getInstance();
        JsonObject json = buildGraphData(loader.getPropertiesWithNeighbours());
        System.err.println("Sending graph to JS...");
        getElement().setAttribute("graphData", json.toString());

    }

    public JsonObject buildGraphData(List<Property> properties) {
        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();
        Set<String> addedNodes = new HashSet<>();

        System.err.println("\n Creating nodes... (" + properties.size() + " properties to evaluate, sorry for the delay :) )");
        // Insert all nodes before checking connections
        for (Property p : properties) {
            String id = p.getParcelaId();

            if (addedNodes.add(id)) {
                JsonObject node = new JsonObject();
                node.addProperty("id", id);
                node.addProperty("label", p.getOwner().getName());

                node.addProperty("value", (int) Math.log(p.getArea())); // este valor será usado para calcular o tamanho

                // Info that appears whenever the node is hovered
                node.addProperty("title", "Parcela: " + p.getParcelaId() + "\nÁrea: " + p.getArea() + " m²\nFreguesia: " + p.getFreguesia());
                nodes.add(node);
            } else
                System.err.println("DEBUG: Erro na construção de nodes (Propriedades duplicadas)");
        }

        System.err.println("Creating edges...");
        List<String> addedEdges = new ArrayList<>();
        for (Property p : properties) {
            String id = p.getParcelaId();
            for (Property neighbor : p.getNeighbourProperties()) {
                String nid = neighbor.getParcelaId();

                if (addedNodes.add(nid)) { // Se não foi inserida
                    System.err.println("Propriedade " + neighbor.getParcelaId() + " não adicionada nao primeira volta," +
                            " mas referenciada como vizinha (Verificar construção de vizinhos)");

                    // Still adds the node so it doesn't stop the graph execution
                    JsonObject neighborNode = new JsonObject();
                    neighborNode.addProperty("id", nid);
                    neighborNode.addProperty("label", neighbor.getOwner().getName());
                    nodes.add(neighborNode);
                }

                String edgeKey = id.compareTo(nid) < 0 ? id + "-" + nid : nid + "-" + id;

                // Verifies if it was already added, only adds if not
                if (!addedEdges.contains(edgeKey)) {
                    addedEdges.add(edgeKey);

                    JsonObject edge = new JsonObject();
                    edge.addProperty("from", id);
                    edge.addProperty("to", nid);
                    edges.add(edge);
                }
            }
        }

        JsonObject graph = new JsonObject();
        graph.add("nodes", nodes);
        graph.add("edges", edges);

        return graph;
    }
}
