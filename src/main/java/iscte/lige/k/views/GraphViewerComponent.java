package iscte.lige.k.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import iscte.lige.k.dataStructures.Owner;
import iscte.lige.k.service.PropertiesLoader;
import iscte.lige.k.dataStructures.Property;

import java.util.*;

@Tag("graph-viewer")
@JsModule("./graph-viewer.js")
public class GraphViewerComponent extends Div {
    Runnable onGraphLoadedCallback;

    public GraphViewerComponent() {
        // JS will call @ClientCallable to ask for the data after displaying loading screen
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
        JsonObject json = null;
        if (Objects.equals(loader.getLoadingOptions()[0], "Proprietarios"))
            json = buildGraphData(loader.getOwners(), loader.getOwnerRelationships());
        else json = buildGraphData(loader.getPropertiesWithNeighbours());
        System.err.println("Sending graph to JS...");
        getElement().setAttribute("graphData", json.toString());

    }

    public JsonObject buildGraphData(List<Property> properties) {
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("List of properties is null or empty");
        }

        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();
        Set<String> addedNodes = new HashSet<>();

        System.err.println("\nCreating nodes... (" + properties.size() + " properties to evaluate, sorry for the delay :) )");
        // Insert all nodes before checking connections
        for (Property p : properties) {
            String id = p.getParcelaId();

            if (addedNodes.add(id)) {
                JsonObject node = new JsonObject();
                node.addProperty("id", id);
                node.addProperty("label", p.getOwner().getName());

                node.addProperty("value", (int) Math.log(p.getArea())); // este valor será usado para calcular o tamanho

                // Info that appears whenever the node is hovered
                node.addProperty("title", "Parcela: " + p.getParcelaId() + "\nÁrea: " + p.getArea() + " m²\nFreguesia: " + p.getParish());
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

    public JsonObject buildGraphData(Map<Integer, Owner> owners, Map<Owner, Map<Owner, Integer>> relations) {

        JsonArray nodes = new JsonArray();
        JsonArray edges = new JsonArray();
        Set<String> addedNodes = new HashSet<>();

        System.err.println("\nCreating nodes... (" + owners.size() + " properties to evaluate, sorry for the delay :) )");
        // Insert all nodes before checking connections
        for (Integer identifier : owners.keySet()) {
            String id = identifier.toString();

            if (addedNodes.add(id)) {
                JsonObject node = new JsonObject();
                node.addProperty("id", id);
                node.addProperty("label", id);


                double avgArea = owners.get(identifier).calculateAvgArea();

                node.addProperty("value", (int) Math.log(avgArea)); // este valor será usado para calcular o tamanho

                // Info that appears whenever the node is hovered
                node.addProperty("title", "Identificador: " + identifier + "\nÁrea Média: " + avgArea + " m²\nNº de Propriedades: " + owners.get(identifier).getNumOfProperties());
                nodes.add(node);
            } else
                System.err.println("DEBUG: Erro na construção de nodes (Propriedades duplicadas)");
        }

        System.err.println("Creating edges...");
        List<String> addedEdges = new ArrayList<>();
        for (Owner owner : owners.values()) {
            String id = owner.getName();
            for (Owner otherOwner: relations.get(owner).keySet()) {
                String otherId = otherOwner.getName();

                if (addedNodes.add(otherId)) { // Se não foi inserida

                    System.err.println("Algo de errado ocorreu a construir os owners, deveriam ter sido inseridos antes de chegarem aqui:" + otherId);
                }

                String edgeKey = id.compareTo(otherId) < 0 ? id + "-" + otherId : otherId + "-" + id;

                // Verifies if it was already added, only adds if not
                if (!addedEdges.contains(edgeKey)) {
                    addedEdges.add(edgeKey);

                    JsonObject edge = new JsonObject();
                    edge.addProperty("from", id);
                    edge.addProperty("to", otherId);

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
