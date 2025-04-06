package iscte.lige.k;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AreaMediaCalculator {

    // Função principal
    public static void main(String[] args) {
        // Caminho do ficheiro CSV
        String filePath = "src/main/resources/Madeira-Moodle-1.1.csv";

        // Perguntar ao utilizador o critério de agrupamento
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o critério de agrupamento:");
        System.out.println("1 - Freguesia");
        System.out.println("2 - Município");
        System.out.println("3 - Ilha");
        System.out.print("Digite o número correspondente: ");
        int criterio = scanner.nextInt();
        scanner.nextLine();  // Consumir a linha extra após o número

        // Dependendo da escolha do utilizador, perguntar o nome da localidade
        String agrupamentoEscolhido = "";
        String pergunta = "";

        switch (criterio) {
            case 1:
                agrupamentoEscolhido = "Freguesia";
                pergunta = "Qual é a freguesia da qual queres consultar a área média? ";
                break;
            case 2:
                agrupamentoEscolhido = "Município";
                pergunta = "Qual é o município da qual queres consultar a área média? ";
                break;
            case 3:
                agrupamentoEscolhido = "Ilha";
                pergunta = "Qual é a ilha da qual queres consultar a área média? ";
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }

        // Perguntar o nome da localidade ao utilizador
        System.out.print(pergunta);
        String localidadeEscolhida = scanner.nextLine().trim().toLowerCase();

        // Chamar a função para calcular a área média
        calcularAreaMedia(filePath, agrupamentoEscolhido, localidadeEscolhida);
    }

    // Função para calcular a área média
    public static void calcularAreaMedia(String filePath, String agrupamentoEscolhido, String localidadeEscolhida) {
        File csv = new File(filePath);
        if (!csv.exists()) {
            System.err.println("Ficheiro CSV não existe ou caminho está incorreto.");
            return;
        }

        // Map para armazenar as áreas por localidade
        Map<String, List<Double>> areasMap = new HashMap<>();

        try (Scanner s = new Scanner(csv)) {
            s.nextLine(); // Ignorar a linha do cabeçalho

            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] data = line.split(";");

                // Verificar e adicionar a área de acordo com o agrupamento escolhido
                String localidade = "";
                switch (agrupamentoEscolhido) {
                    case "Freguesia":
                        localidade = data[7]; // Freguesia está na coluna 7
                        break;
                    case "Município":
                        localidade = data[8]; // Município está na coluna 8
                        break;
                    case "Ilha":
                        localidade = data[9]; // Ilha está na coluna 9
                        break;
                }

                // Remover espaços extras e garantir que as comparações sejam feitas sem considerar maiúsculas/minúsculas
                localidade = localidade.trim().toLowerCase(); // Normalizar a localidade no CSV

                // Se a localidade corresponde à escolhida pelo utilizador, processar a área
                if (localidade.equals(localidadeEscolhida)) {
                    try {
                        double area = Double.parseDouble(data[4]); // A área está na coluna 4 (Shape_Area)
                        areasMap.computeIfAbsent(localidade, k -> new ArrayList<>()).add(area);
                    } catch (NumberFormatException e) {
                        // Ignorar valores inválidos para a área
                    }
                }
            }

            // Verificar se há áreas para a localidade escolhida
            if (areasMap.containsKey(localidadeEscolhida)) {
                List<Double> areas = areasMap.get(localidadeEscolhida);
                double media = areas.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                System.out.printf("A área média das propriedades em %s é %.2f metros quadrados.\n", localidadeEscolhida, media);
            } else {
                System.out.println("Nenhuma localidade encontrada com esse nome.");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Erro ao ler o ficheiro: " + e.getMessage());
        }
    }
}

