package iscte.lige.k.views;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("Trades")
@Uses(ListBox.class)
@Uses(HorizontalLayout.class)
public class MainView extends VerticalLayout {

    private final Div loadingText = new Div();
    private final Div spinner = new Div();
    private final HorizontalLayout content = new HorizontalLayout();

    public MainView() {
        showLoadingScreen();

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

        // Create the side list
        ListBox<String> list = new ListBox<>();
        list.setItems("Terreno 1 <-> Terreno 3", "Terreno 2 <-> Terreno 6");
        list.setWidthFull();
        list.setHeightFull();
        content.setFlexGrow(2, list);
        content.add(list);

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

    private void showLoadingScreen() {

        setSizeFull();
        getStyle().set("background", "#0f1117");
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        loadingText.setText("Loading graph...");
        loadingText.getStyle()
                .set("color", "white")
                .set("fontSize", "1.5rem")
                .set("marginTop", "2rem");

        spinner.setClassName("spinner");
        add(spinner, loadingText);
    }
}
