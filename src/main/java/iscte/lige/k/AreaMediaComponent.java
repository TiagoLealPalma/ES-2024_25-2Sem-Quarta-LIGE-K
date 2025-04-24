package iscte.lige.k;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.text.Normalizer;


@Tag("area-media-viewer")
@JsModule("./area-media-viewer.js")
public class AreaMediaComponent extends Div {

    public AreaMediaComponent() {
        System.err.println("AreaMediaComponent inicializado.");
    }

    @ClientCallable
    public void requestAverageArea(String criterio, String localidade) {
        String filePath = "src/main/resources/Madeira-Moodle-1.1.csv";
        double media = calcularAreaMedia(filePath, criterio, localidade.toLowerCase());

        // Envia o resultado para o frontend
        getElement().callJsFunction("showResult", media);
    }

    private double calcularAreaMedia(String filePath, String agrupamentoEscolhido, String localidadeEscolhida) {
        File csv = new File(filePath);
        if (!csv.exists()) {
            System.err.println("Ficheiro CSV não encontrado: " + filePath);
            return 0;
        }

        Map<String, List<Double>> areasMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csv))) {
            String linha;
            boolean isHeader = true;
            StringBuilder recordBuilder = new StringBuilder();

            while ((linha = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue; // Ignora cabeçalho
                }

                if (recordBuilder.length() > 0) {
                    recordBuilder.append("\n");
                }
                recordBuilder.append(linha);

                String[] dataCheck = recordBuilder.toString().split(";", -1);
                if (dataCheck.length < 10) {
                    continue; // Linha ainda incompleta
                }

                String[] data = dataCheck;

                String localidade = switch (agrupamentoEscolhido) {
                    case "Freguesia" -> data[7];
                    case "Município" -> data[8];
                    case "Ilha"      -> data[9];
                    default          -> "";
                };


                if (normalizar(localidade).equals(normalizar(localidadeEscolhida))) {
                    try {
                        double area = Double.parseDouble(data[4]);
                        areasMap.computeIfAbsent(localidadeEscolhida, k -> new ArrayList<>()).add(area);
                    } catch (NumberFormatException e) {
                        System.err.println("Área inválida: " + data[4]);
                    }
                }

                recordBuilder.setLength(0); // Limpa para próximo registo
            }

            List<Double> areas = areasMap.getOrDefault(localidadeEscolhida, Collections.emptyList());
            return areas.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        } catch (IOException e) {
            System.err.println("Erro ao ler o CSV: " + e.getMessage());
            return 0;
        }
    }

    private String normalizar(String texto) {
        if (texto == null) return "";

        // Remove acentos
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Remove espaços extras e torna tudo minúsculas
        return normalizado
                .toLowerCase()
                .replaceAll("\\s+", ""); // remove todos os espaços
    }

}
