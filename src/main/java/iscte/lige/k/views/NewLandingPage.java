package iscte.lige.k.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import iscte.lige.k.service.PropertiesLoader;
import com.google.gson.*;

import java.util.List;


@Route("/Landing")
@JsModule("./newLanding.js")
public class NewLandingPage extends VerticalLayout {

    private final Gson gson = new Gson();
    private ComboBox<String> ilhasDropdown = new ComboBox<>();
    private ComboBox<String> concelhosDropdown = new ComboBox<>();
    private ComboBox<String> freguesiasDropdown = new ComboBox<>();


    private PropertiesLoader propertiesLoader = PropertiesLoader.getInstance();

    public NewLandingPage() {

        addClassName("landing-view");
        setSizeFull();

        // Título
        H1 titulo = new H1("Exploração de Terrenos");
        titulo.addClassName("titulo1");

        // Dropdowns
        ilhasDropdown = new ComboBox<>("Ilha");
        concelhosDropdown = new ComboBox<>("Concelho");
        freguesiasDropdown = new ComboBox<>("Freguesia");

        // Adicionar dados
        List<String> ilhas = propertiesLoader.getIslands();
        List<String> concelhos = propertiesLoader.getCounties();
        List<String> freguesias = propertiesLoader.getParishes();

        ilhasDropdown.setItems(ilhas);
        concelhosDropdown.setItems(concelhos);
        freguesiasDropdown.setItems(freguesias);

        ilhasDropdown.addClassName("dropdown-ilha");
        concelhosDropdown.addClassName("dropdown-concelho");
        freguesiasDropdown.addClassName("dropdown-freguesia");

        // Listeners para alterar visualização dinamicamente
        ilhasDropdown.addValueChangeListener(e -> updateIsland());
        concelhosDropdown.addValueChangeListener(e -> updateConcelho());
        freguesiasDropdown.addValueChangeListener(e -> updateFreguesia());

        // Zona de filtros
        Div filtros = new Div(ilhasDropdown, concelhosDropdown, freguesiasDropdown);
        filtros.addClassName("filters");

        // Search button
        Button verGrafo = new Button("Ver Trocas", e -> verGrafo(ilhasDropdown.getValue(),
                concelhosDropdown.getValue(), freguesiasDropdown.getValue()));
        verGrafo.addClassName("botao-ver-grafo1");

        Anchor proprietariosLink = new Anchor("Trades?criteria=Proprietarios", "Mostre me os proprietários!");
        proprietariosLink.setClassName("prop-link");


        // Área de visualização JS
        Div graphArea = new Div();
        graphArea.setId("visualizacao-grafica");
        graphArea.addClassName("landing-graph-area");

        Div criteria = new Div(filtros, verGrafo, proprietariosLink);
        criteria.addClassName("landing-criteria");

        // Wrapper geral
        add(titulo, graphArea, criteria);

        getElement().executeJs("window.initGraph($0)");
    }

    private void updateIsland(){
        if (ilhasDropdown.getValue().isEmpty()) return;
        concelhosDropdown.setValue("");
        freguesiasDropdown.setValue("");
        atualizarVisualizacao(ilhasDropdown.getValue(), null, null);
    }

    private void updateConcelho(){
        if (concelhosDropdown.getValue().isEmpty()) return;
        ilhasDropdown.setValue("");
        freguesiasDropdown.setValue("");
        atualizarVisualizacao(null, concelhosDropdown.getValue(), null);

    }

    private void updateFreguesia(){
        if (freguesiasDropdown.getValue().isEmpty()) return;
        ilhasDropdown.setValue("");
        concelhosDropdown.setValue("");
        atualizarVisualizacao(null,null,freguesiasDropdown.getValue());

    }

    private void atualizarVisualizacao(String ilha, String concelho, String freguesia) {
        getElement().executeJs("window.updateGraph($0, $1, $2)", ilha, concelho, freguesia);
    }

    private void verGrafo(String ilha, String concelho, String freguesia) {

        if (ilha == null & concelho == null & freguesia == null){ // Load proprietários (Everything null)
            UI.getCurrent().navigate("Trades?criteria=Proprietarios");
        } else if (ilha != null && !ilha.isEmpty()) {    // Load with ilha
            UI.getCurrent().navigate("Trades?criteria=ilha&ilha=" + ilha);
        } else if (concelho != null && !concelho.isEmpty()) {   // Load with concelho
            UI.getCurrent().navigate("Trades?criteria=concelho&concelho=" + concelho);
        } else if (freguesia != null && !freguesia.isEmpty()) {    // Load with freguesia
            UI.getCurrent().navigate("Trades?criteria=freguesia&freguesia=" + freguesia);
        }

    }

}