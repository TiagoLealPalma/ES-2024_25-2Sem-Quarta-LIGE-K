package iscte.lige.k.views;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import iscte.lige.k.dataStructures.Trade;
import iscte.lige.k.service.PropertiesLoader;
import iscte.lige.k.util.TradeEval;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Route("trades")
@Uses(ListBox.class)
@Uses(HorizontalLayout.class)
public class MainView extends VerticalLayout implements AfterNavigationObserver {

    private PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();
    private final Div loadingText = new Div();
    private final Div spinner = new Div();
    private final HorizontalLayout content = new HorizontalLayout();
    private final VerticalLayout vertical = new VerticalLayout();
    private final Span areaLabel = new Span();
    private final Span nOwnersLabel = new Span();
    private final Span avgOwnerLabel = new Span();

    public MainView() {
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        String criteriaEncoded = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault("criteria", List.of(""))
                .get(0);

        String criteria = URLDecoder.decode(criteriaEncoded, StandardCharsets.UTF_8);
        System.err.println("Critério recebido: " + criteria);

        if (criteria.contains("Proprietarios")){
            System.err.println("Loading graph for: Proprietários");
            propertiesLoader.setLoadingOptions(new String[]{"Proprietarios", "null"});
            buildUI();
            return;
        }



        // Se for de propriedades
        String valueEncoded = event.getLocation()
                .getQueryParameters()
                .getParameters()
                .getOrDefault(criteria, List.of(""))
                .get(0);

        String value = URLDecoder.decode(valueEncoded, StandardCharsets.UTF_8);
        System.err.println("Critério recebido: " + value);


        if (!value.isBlank()) {
            System.err.println("Loading graph for: " + value);
            propertiesLoader.setLoadingOptions(new String[]{criteria, value});
            areaLabel.setText("Area média por propriedades: " + propertiesLoader.getAvgArea());
            nOwnersLabel.setText("Nº de propriedades: " + propertiesLoader.getPropertiesWithNeighbours().size());
            avgOwnerLabel.setText("Area média por proprietários: " + propertiesLoader.getAvgAreaByOwner());

            buildUI(value);
        } else {
            removeAll();
            add(new H1("Freguesia não selecionada."));
        }
    }


    private void buildUI(String freguesia) {
        showLoadingScreen(freguesia);
        Div infoDiv = new Div();
        infoDiv.setClassName("info-div");
        infoDiv.setHeight("2rem");
        infoDiv.getStyle().set("display", "flex");
        infoDiv.getStyle().set("justify-content", "space-evenly");
        areaLabel.setClassName("info-span");
        nOwnersLabel.setClassName("info-span");
        avgOwnerLabel.setClassName("info-span");
        infoDiv.add(areaLabel, nOwnersLabel, avgOwnerLabel);
        vertical.setHeight("100%");

        vertical.add(infoDiv, content);


        // Prepare main content layout
        content.setWidthFull();
        content.setHeight("100vh");
        content.setSpacing(true);
        vertical.getStyle().set("display", "none"); // initially hidden until JS loads
        content.getStyle().set("display", "none"); // initially hidden until JS loads

        // Create the graph viewer (must be in DOM early for JS to execute)
        GraphViewerComponent graph = new GraphViewerComponent();
        graph.setWidthFull();
        graph.setHeightFull();
        content.setFlexGrow(1, graph);
        content.add(graph);

        // Create the side list


        ListBox<HorizontalLayout> list = new ListBox<>();

        // DEBUGGING
        List<Trade> trades = propertiesLoader.getTrades().stream().sorted().distinct().toList();

        // Trades list
        for (Trade trade : trades) {
            // Trade item
            HorizontalLayout row = new HorizontalLayout();
            row.addClassName("trade-item");

            // Attributes for P1
            row.getElement().setAttribute("P1", trade.getOwner1Property().getParcelaId());
            row.getElement().setAttribute("P1Main", trade.getOwner1MainProperty().getParcelaId());

            // Attributes for P2
            row.getElement().setAttribute("P2", trade.getOwner2Property().getParcelaId());
            row.getElement().setAttribute("P2Main", trade.getOwner2MainProperty().getParcelaId());

            // Trade label
            Span label = new Span(trade.toString());
            label.addClassName("trade-label");

            // Trade grade
            Span grade = new Span(String.valueOf(trade.getScore()));
            grade.addClassName("trade-grade");
            grade.getStyle().set("background-color", TradeEval.IntToColor(trade.getScore()));

            row.add(label, grade);
            list.add(row);
        }


        list.setWidthFull();
        list.setHeightFull();
        content.setFlexGrow(2, list);
        content.add(list);


        // Add content to the page (graph will still be hidden until ready)
        add(vertical);

        // Show graph and hide loading screen when JS notifies that the graph is ready
        graph.onGraphLoaded(() -> {
            remove(loadingText, spinner);
            content.getStyle().set("display", "flex");
            vertical.getStyle().set("display", "block");
            loadingText.setVisible(false);
            spinner.setVisible(false);
            System.err.println("Graph was loaded, hiding loading screen");
        });
    }

    // Used for proprietarios
    private void buildUI() {
        showLoadingScreen("");

        // Prepare main content layout
        content.setWidthFull();
        content.setHeight("100vh");
        content.setSpacing(true);
        content.getStyle().set("display", "none"); // initially hidden until JS loads

        // Create the graph viewer (must be in DOM early for JS to execute)
        GraphViewerComponent graph = new GraphViewerComponent();
        graph.setWidthFull();
        graph.setHeightFull();
        content.setFlexGrow(1, graph);
        content.add(graph);

        // Add content to the page (graph will still be hidden until ready)
        add(content);

        // Show graph and hide loading screen when JS notifies that the graph is ready
        graph.onGraphLoaded(() -> {
            remove(loadingText, spinner);
            content.getStyle().set("display", "flex");
            loadingText.setVisible(false);
            spinner.setVisible(false);
            System.err.println("Graph was loaded, hiding loading screen");
        });
    }


    // Notificar do comprimento do que estiver ser loaded
    private void showLoadingScreen(String freguesia) {

        setSizeFull();
        getStyle().set("background", "#0f1117");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        if(freguesia.contains("Todas"))
        loadingText.setText("Loading graph..." +
                "\n Your are loading 18 thousand entities, please be patient :)");
        else
            loadingText.setText("Loading graph...");
        loadingText.getStyle()
                .set("color", "white")
                .set("fontSize", "1.5rem")
                .set("marginTop", "2rem");

        spinner.setClassName("spinner");
        add(spinner, loadingText);
    }
}
