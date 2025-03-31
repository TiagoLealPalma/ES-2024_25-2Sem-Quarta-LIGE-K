package iscte.lige.k;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
@Uses(ListBox.class)
@Uses(HorizontalLayout.class)
public class MainView extends VerticalLayout {
    public MainView() {
        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setHeight("100vh");
        content.setSpacing(true);

        GraphViewerComponent graph = new GraphViewerComponent();
        graph.setWidthFull();
        graph.setHeightFull();
        content.setFlexGrow(1, graph); // 2/3 da largura

        ListBox<String> list = new ListBox<>();
        list.setItems("Terreno 1 <-> Terreno 3", "Terreno 2 <-> Terreno 6", "Terreno 2 <-> Terreno 6");
        list.setWidthFull();
        list.setHeightFull();
        content.setFlexGrow(2, list); // 1/3 da largura

        content.add(graph, list);
        add(content);
    }
}