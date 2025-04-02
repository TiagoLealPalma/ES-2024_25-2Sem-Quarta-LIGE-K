package iscte.lige.k;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("")
public class LandingView extends VerticalLayout {

    public LandingView() {
        setSizeFull();
        getStyle().set("background", "#0f1117");

        H1 titulo = new H1("Exploração de Terrenos");
        titulo.getStyle()
                .set("color", "#FFF1D0")
                .set("margin", "2rem auto");

        Button verGrafo = new Button("Ver Grafo por Freguesia", e ->
                verGrafo());

        Button verMapa = new Button("Ver Mapa Geográfico", e ->
                verMapa());

        verGrafo.getStyle().set("margin", "1rem").set("background", "#F66435").set("color", "white");
        verMapa.getStyle().set("margin", "1rem").set("background", "#F66435").set("color", "white");

        add(titulo, verGrafo, verMapa);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    private void verGrafo() {
        UI.getCurrent().navigate("Trades");
    }

    private void verMapa() {
        UI.getCurrent().navigate("mapa");
    }
}
