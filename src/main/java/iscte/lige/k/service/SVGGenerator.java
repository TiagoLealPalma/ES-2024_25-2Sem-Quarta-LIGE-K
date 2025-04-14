package iscte.lige.k.service;

import iscte.lige.k.dataStructures.SimplerProperty;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

public class SVGGenerator {

    public static void exportPropertiesToSVG(List<SimplerProperty> properties, String munincipio, String freguesia) throws Exception {
        // Cria documento SVG
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        int width = 600;
        int height = 300;

        // Normaliza coordenadas
        double minX = properties.stream().mapToDouble(SimplerProperty::getX).min().orElse(0);
        double maxX = properties.stream().mapToDouble(SimplerProperty::getX).max().orElse(1);
        double minY = properties.stream().mapToDouble(SimplerProperty::getY).min().orElse(0);
        double maxY = properties.stream().mapToDouble(SimplerProperty::getY).max().orElse(1);

        double scaleX = width / (maxX - minX);
        double scaleY = height / (maxY - minY);

        for (SimplerProperty p : properties) {
            double normX = (p.getX() - minX) * scaleX;
            double normY = (p.getY() - minY) * scaleY;

            // desenha círculo laranja translúcido com sombra (simulada)
            if((munincipio == "null" && freguesia == "null") || (freguesia == "null" && p.getMunincipio().contains(munincipio)) ||
                    p.getFreguesia().contains(freguesia))
                svgGenerator.setPaint(new Color(234, 175, 24, 50)); // opacidade 50
            else
                svgGenerator.setPaint(new Color(234, 175, 24, 2));
            svgGenerator.fillOval((int) normX, (int) normY, 3, 3);
        }

        if(freguesia == null)freguesia = "null";

        System.err.print("A escrever o ficheiro: src/main/resources/" + munincipio+"-"+freguesia+".svg");
        // Guarda como ficheiro SVG
        try (FileWriter out = new FileWriter("src/main/resources/" + munincipio+"-"+freguesia+".svg")) {
            svgGenerator.stream(out);
        }
    }
}
