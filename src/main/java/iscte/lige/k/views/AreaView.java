package iscte.lige.k.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("area")
public class AreaView extends VerticalLayout {

    public AreaView() {
        AreaMediaComponent component = new AreaMediaComponent();
        add(component);
    }
}


