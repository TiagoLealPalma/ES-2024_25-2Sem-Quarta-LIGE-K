package iscte.lige.k.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import iscte.lige.k.service.PropertiesLoader;

import java.util.List;


@Route("")
@JsModule("./landing.js")
public class LandingView extends VerticalLayout {
    private PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    public LandingView() {
        addClassName("landing-view");
        setSizeFull();

        // Título
        H1 titulo = new H1("Exploração de Terrenos");
        titulo.addClassName("titulo");

        // Dropdown
        ComboBox<String> freguesiaDropdown = new ComboBox<>("Selecione freguesia");
        List<String> options = propertiesLoader.getFreguesias();
        options.add("Outras");
        options.add("Todas");
        freguesiaDropdown.setItems(options);
        freguesiaDropdown.addClassName("dropdown-freguesia");

        // Botão
        Button verGrafo = new Button("Ver Trocas", e -> verGrafo(freguesiaDropdown.getValue()));
        verGrafo.addClassName("botao-ver-grafo");

        // Zona dos elementos escondidos
        Div escondido = new Div(freguesiaDropdown, verGrafo);
        escondido.addClassName("conteudo-escondido");

        // Wrapper geral
        Div container = new Div(titulo, escondido);
        container.addClassName("conteudo-wrapper");

        add(container);

        // Ativar o listener no titulo
        getElement().executeJs("window.activateTituloOnce && window.activateTituloOnce()");
    }

    private void verGrafo(String freguesia) {
        if (freguesia != null && !freguesia.isEmpty()) {
            UI.getCurrent().navigate("Trades?freguesia=" + freguesia);
        }
    }
}