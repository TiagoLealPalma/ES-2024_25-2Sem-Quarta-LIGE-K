package iscte.lige.k.views;

import iscte.lige.k.service.PropertiesLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // Começar a carregar em paralelo os dados para dentro da aplicação, criar a instancia e fazer os calculos necessários.
        new Thread(() -> {
            PropertiesLoader loader = PropertiesLoader.getInstance();
        }).start();
        SpringApplication.run(Application.class, args);
    }
}
