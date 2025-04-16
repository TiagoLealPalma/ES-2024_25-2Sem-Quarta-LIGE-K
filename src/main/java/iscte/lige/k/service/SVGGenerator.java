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

        // Canvas com resolução aumentada
        int width = 1050;
        int height = 700;


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

            boolean pertenceFiltro =
                    (munincipio == "null" && freguesia == "null") ||
                            (freguesia == "null" && p.getMunincipio().contains(munincipio)) ||
                            p.getFreguesia().contains(freguesia);

            if (pertenceFiltro) {
                // Brilho leve antes do ponto principal
                svgGenerator.setPaint(new Color(234, 175, 24, 30));
                svgGenerator.fillOval((int) normX - 1, (int) normY - 1, 4, 4);

                // Ponto principal pequeno e vibrante
                svgGenerator.setPaint(new Color(234, 175, 24, 200));
                svgGenerator.fillOval((int) normX, (int) normY, 1, 1);
            } else {
                svgGenerator.setPaint(new Color(234, 175, 24, 2)); // ponto quase invisível
                svgGenerator.fillOval((int) normX, (int) normY, 3, 3);
            }
        }

        if (freguesia == null) freguesia = "null";

        System.err.println("A escrever o ficheiro: src/main/resources/META-INF/resources/svgs/" + munincipio + "-" + freguesia + ".svg");

        // Guarda como ficheiro SVG
        try (FileWriter out = new FileWriter("src/main/resources/META-INF/resources/svgs/" + munincipio + "-" + freguesia + ".svg")) {
            svgGenerator.stream(out);
        }
    }
}
