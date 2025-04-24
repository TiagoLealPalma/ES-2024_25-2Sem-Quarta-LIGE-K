package iscte.lige.k;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import iscte.lige.k.AreaMediaComponent;

@Route("area")
public class AreaView extends VerticalLayout {

    public AreaView() {
        AreaMediaComponent component = new AreaMediaComponent();
        add(component);
    }
}


