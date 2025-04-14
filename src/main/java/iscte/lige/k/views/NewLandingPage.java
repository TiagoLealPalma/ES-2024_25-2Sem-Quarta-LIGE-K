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
import com.google.gson.*;

import java.util.List;


@Route("/Landing")
@JsModule("./newLanding.js")
public class NewLandingPage extends VerticalLayout {

    private final Gson gson = new Gson();

    private PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    public NewLandingPage() {

        addClassName("landing-view");
        setSizeFull();

        // Título
        H1 titulo = new H1("Exploração de Terrenos");
        titulo.addClassName("titulo1");

        // Dropdowns
        ComboBox<String> ilhasDropdown = new ComboBox<>("Ilha");
        ComboBox<String> concelhosDropdown = new ComboBox<>("Concelho");
        ComboBox<String> freguesiasDropdown = new ComboBox<>("Freguesia");

        // Adicionar dados
        List<String> ilhas = propertiesLoader.getIlhas();
        List<String> concelhos = propertiesLoader.getMunincipios();
        List<String> freguesias = propertiesLoader.getFreguesiasUnfiltered();

        ilhasDropdown.setItems(ilhas);
        concelhosDropdown.setItems(concelhos);
        freguesiasDropdown.setItems(freguesias);

        ilhasDropdown.addClassName("dropdown-ilha");
        concelhosDropdown.addClassName("dropdown-concelho");
        freguesiasDropdown.addClassName("dropdown-freguesia");

        // Listeners para alterar visualização dinamicamente
        ilhasDropdown.addValueChangeListener(e -> atualizarVisualizacao(ilhasDropdown.getValue(), null, null));
        concelhosDropdown.addValueChangeListener(e -> atualizarVisualizacao(null, concelhosDropdown.getValue(), null));
        freguesiasDropdown.addValueChangeListener(e -> atualizarVisualizacao(null, null, freguesiasDropdown.getValue()));

        // Zona de filtros
        Div filtros = new Div(ilhasDropdown, concelhosDropdown, freguesiasDropdown);
        filtros.addClassName("filters");

        // Área de visualização JS
        Div graphArea = new Div();
        graphArea.setId("visualizacao-grafica");
        graphArea.addClassName("landing-graph-area");

        // Wrapper geral

        add(titulo, graphArea, filtros);

        getElement().executeJs("window.initGraph($0)");
    }

    private void atualizarVisualizacao(String ilha, String concelho, String freguesia) {
        getElement().executeJs("window.updateGraph($0, $1, $2)", ilha, concelho, freguesia);
    }


}