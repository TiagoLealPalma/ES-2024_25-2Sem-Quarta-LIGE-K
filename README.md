# ES-2024_25-2Sem-Quarta-LIGE-K


## 👥 Identificação do Grupo
ES-2024_25-2Sem-Quarta-LIGE-K  
**Repositório GitHub:** https://github.com/TiagoLealPalma/ES-2024_25-2Sem-Quarta-LIGE-K/

## 🧑‍🤝‍🧑 Membros do Grupo

|       Nome Completo        | Número de Estudante | GitHub Username    |
|----------------------------|---------------------|--------------------|
| Miguel Orrico              |        120104       |   @miguel_orrico   |
| Maria Beatriz Cruz         |        111348       |   @BeatrizfrCruz   |
| Martim dos Reis Saldanha   |        111245       |   @martimreis      |
| Tiago Leal Palma           |        110679       |   @TiagoLealPalma  |

---

## 📝 Descrição Geral

Este projeto foi desenvolvido no âmbito da unidade curricular de **Engenharia de Software**, com o objetivo de implementar uma aplicação para **gestão do território**. O projeto segue a abordagem **Scrum** e apresenta um ciclo de desenvolvimento iterativo e incremental.

---

## ⚠️ Funcionalidades Não Implementadas / Incompletas

- Tudo implementado.

---

## 🛠️ Tecnologias e Ferramentas Utilizadas

### 🔧 Backend
- **Java 21**
- **Spring Boot 3.2.4**
- **Vaadin 24.3.0**
- **JTS (Java Topology Suite)** para geometria espacial
- **Apache Commons CSV** para parsing de ficheiros CSV
- **Gson** para manipulação de JSON

### 💻 Frontend
- **Vaadin Flow** com componentes personalizados
- **v-leaflet** para visualização interativa de mapas
- **HTML/CSS/JavaScript** com scripts próprios (`area-media-viewer.js`, `leaflet-map-viewer.js`, etc.)

### 📦 Gestão de Dependências
- **Maven** (`pom.xml` com controlo centralizado de versões)

### 🧪 Testes
- **JUnit 4.13.2**
- **Maven Surefire Plugin** 
- **JACOCO 8.13**

### 🧰 Ferramentas de Apoio
- **Git** e **GitHub** para controlo de versões e colaboração
- **Trello / GitHub Projects** para gestão ágil com **Scrum**
- **JavaDoc** para geração automática de documentação do código
- **Ferramenta de cobertura de testes** (ex.: JaCoCo ou IntelliJ Coverage)


---

## 📁 Estrutura do Projeto
```
Terrain-Fragmentation-Helper
├── frontend/                    # Interface e visualização (Vaadin/JS)
│   └── themes/                  # Tema visual personalizado (CSS)
├── src/
│   └── main/
│       ├── java/
│       │   └── iscte/lige/k/
│       │       ├── dataStructures/   # Estruturas principais: Property, Owner, Trade, etc
│       │       ├── service/          # Serviços de carregamento e lógica de negócio
│       │       ├── util/             # Utilitários como avaliação de trocas e geração de svgs para frontend
│       │       └── views/            # Geração de SVG e visualizações
|       |
│       └── resources/         # Ficheiros de configuração e recursos 
│
├── test/
│   └── java/
│       └── iscte/lige/k/      # Testes JUnit organizados por pacote
│
├── pom.xml                    # Gestão de dependências e build com Maven
├── application.properties     # Configurações Spring Boot
├── README.md                  # Documento de apresentação do projeto
├── target/                    # Código compilado e ficheiros gerados
|
└── docs/          # Pasta que guarda documentos de controlo de qualidade, documentação do código e gestão do projeto
      ├── documentation/ # JavaDoc HTMLs
      ├── tests-and-quality-assurance/ # Jacoco reports and IDE Coverage
      └── trello/ # Prints da gestão no Trello
```          

---

## 🧪 Testes e Cobertura

- Todos os testes foram implementados com **JUnit 4**.
- A cobertura de testes excede **50%** em pelo menos duas métricas, incluindo **complexidade ciclomática** e **line coverage**.
- A execução dos testes pode ser feita via IDE ou terminal com `mvn test`.

---

## 📊 Relatório de Qualidade do Software

O relatório de qualidade foi gerado com a ferramenta **JACOCO** e encontra-se disponível na pasta `docs/`.

---

## 📚 JavaDoc

A documentação técnica do projeto foi gerada automaticamente com JavaDoc e encontra-se na diretoria /docs/documentation/apidocs e pode ser acedido pelo ficheiro index.html.

---

## 📅 Gestão Ágil com Scrum

- Ferramenta de gestão: **Trello**
- Planeamento por Sprints com definição de tarefas
- Documentação de:
  - Product Backlog
  - Sprint Planning
  - Sprint Review e Retrospective
  - Burndown Charts
- Rastreabilidade assegurada através de commits e pull requests associados às user stories

---

## 🎥 Vídeo de Demonstração

![Demonstração da App](docs/DemoGIF.gif)

📺 Link para o vídeo no YouTube: https://youtu.be/fU-ICmFsbWs

O vídeo demonstra:
- A gestão ágil do projeto (Scrum)
- Organização do repositório GitHub
- Execução dos testes JUnit e cobertura
- Funcionalidades da aplicação em funcionamento
- Bibliotecas e ferramentas utilizadas

---

## ✅ Estado Final

✔ Projeto funcional  
✔ Testes sem falhas  
✔ Qualidade avaliada e documentada  
✔ Documentação JavaDoc completa  
✔ Gestão Scrum evidenciada  
✔ Entregáveis organizados no repositório  


