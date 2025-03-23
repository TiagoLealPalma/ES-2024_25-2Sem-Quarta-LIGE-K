package iscte.lige.k;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

@Tag("graph-viewer")
@JsModule("./graph-viewer.js")
public class GraphViewerComponent extends Div {
    public GraphViewerComponent() {

    }
}
