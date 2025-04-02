package iscte.lige.k.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.List;


@Route("")
public class LandingView extends VerticalLayout {

    public LandingView() {
        addClassName("landing-view");
        setSizeFull();

        // Título
        H1 titulo = new H1("Exploração de Terrenos");
        titulo.addClassName("titulo");

        // Dropdown
        ComboBox<String> freguesiaDropdown = new ComboBox<>("Selecione freguesia");
        freguesiaDropdown.setItems(List.of("Ajuda", "Alvalade", "Arroios", "Beato", "Benfica"));
        freguesiaDropdown.addClassName("dropdown-freguesia");

        // Botão
        Button verGrafo = new Button("Ver Grão por Freguesia", e -> verGrafo(freguesiaDropdown.getValue()));
        verGrafo.addClassName("botao-ver-grafo");

        // Zona dos elementos escondidos
        Div escondido = new Div(freguesiaDropdown, verGrafo);
        escondido.addClassName("conteudo-escondido");

        // Wrapper geral
        Div container = new Div(titulo, escondido);
        container.addClassName("conteudo-wrapper");

        add(container);
    }

    private void verGrafo(String freguesia) {
        if (freguesia != null && !freguesia.isEmpty()) {
            UI.getCurrent().navigate("Trades?freguesia=" + freguesia);
        }
    }
}