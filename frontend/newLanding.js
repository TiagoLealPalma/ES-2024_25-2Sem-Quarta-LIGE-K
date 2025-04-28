console.log("newLanding.js carregado");

// Objeto para guardar imagens pré-carregadas
const preloadedImages = {};

// Pair da freguesia com o concelho
const freguesiaToConcelho = {
    "Arco da Calheta": "Calheta",
    "Calheta": "Calheta",
    "Estreito da Calheta": "Calheta",
    "Fajã da Ovelha": "Calheta",
    "Jardim do Mar": "Calheta",
    "Paul do Mar": "Calheta",
    "Ponta do Pargo": "Calheta",
    "Prazeres": "Calheta",
    "Câmara de Lobos": "Câmara de Lobos",
    "Curral das Freiras": "Câmara de Lobos",
    "Estreito de Câmara de Lobos": "Câmara de Lobos",
    "Jardim da Serra": "Câmara de Lobos",
    "Quinta Grande": "Câmara de Lobos",
    "Funchal (Santa Luzia)": "Funchal",
    "Funchal (Santa Maria Maior)": "Funchal",
    "Funchal (São Pedro)": "Funchal",
    "Funchal (Sé)": "Funchal",
    "Imaculado Coração de Maria": "Funchal",
    "Monte": "Funchal",
    "Santo António": "Funchal",
    "São Gonçalo": "Funchal",
    "São Martinho": "Funchal",
    "São Roque": "Funchal",
    "Água de Pena": "Machico",
    "Caniçal": "Machico",
    "Machico": "Machico",
    "Porto da Cruz": "Machico",
    "Canhas": "Ponta do Sol",
    "Madalena do Mar": "Ponta do Sol",
    "Ponta do Sol": "Ponta do Sol",
    "Campanário": "Ribeira Brava",
    "Tabua": "Ribeira Brava",
    "Caniço": "Santa Cruz",
    "Faial": "Santana"
};

// Função que pré-carrega imagens para cache
function preloadSvgs(svgList) {
    svgList.forEach((name) => {
        const img = new Image();
        img.src = `/svgs/${name}`;
        preloadedImages[name] = img;
    });
}

window.initGraph = function () {
    const container = document.getElementById("visualizacao-grafica");
    container.innerHTML = '';

    preloadSvgs([
        "Calheta-Arco da Calheta.svg",
        "Calheta-Calheta.svg",
        "Calheta-Estreito da Calheta.svg",
        "Calheta-Fajã da Ovelha.svg",
        "Calheta-Jardim do Mar.svg",
        "Calheta-null.svg",
        "Calheta-Paul do Mar.svg",
        "Calheta-Ponta do Pargo.svg",
        "Calheta-Prazeres.svg",
        "Câmara de Lobos-Câmara de Lobos.svg",
        "Câmara de Lobos-Curral das Freiras.svg",
        "Câmara de Lobos-Estreito de Câmara de Lobos.svg",
        "Câmara de Lobos-Jardim da Serra.svg",
        "Câmara de Lobos-null.svg",
        "Câmara de Lobos-Quinta Grande.svg",
        "Funchal-Funchal (Santa Luzia).svg",
        "Funchal-Funchal (Santa Maria Maior).svg",
        "Funchal-Funchal (São Pedro).svg",
        "Funchal-Funchal (Sé).svg",
        "Funchal-Imaculado Coração de Maria.svg",
        "Funchal-Monte.svg",
        "Funchal-null.svg",
        "Funchal-Santo António.svg",
        "Funchal-São Gonçalo.svg",
        "Funchal-São Martinho.svg",
        "Funchal-São Roque.svg",
        "Machico-Água de Pena.svg",
        "Machico-Caniçal.svg",
        "Machico-Machico.svg",
        "Machico-null.svg",
        "Machico-Porto da Cruz.svg",
        "NA-NA.svg",
        "NA-null.svg",
        "null-null.svg",
        "Ponta do Sol-Canhas.svg",
        "Ponta do Sol-Madalena do Mar.svg",
        "Ponta do Sol-null.svg",
        "Ponta do Sol-Ponta do Sol.svg",
        "Ribeira Brava-Campanário.svg",
        "Ribeira Brava-null.svg",
        "Ribeira Brava-Tabua.svg",
        "Santa Cruz-Caniço.svg",
        "Santa Cruz-null.svg",
        "Santana-Faial.svg",
        "Santana-null.svg"
    ]);


    // Mostra a imagem inicial (pré-carregada se existir)
    const defaultKey = "null-null.svg";
    const preloaded = preloadedImages[defaultKey];
    if (preloaded) {
        container.appendChild(preloaded.cloneNode(true));
    } else {
        const initialImg = document.createElement("img");
        initialImg.src = `/svgs/${defaultKey}`;
        initialImg.alt = "Mapa geral";
        initialImg.style.maxWidth = "100%";
        initialImg.style.height = "auto";
        container.appendChild(initialImg);
    }

    window.updateGraph(null, "calheta", null);
    window.updateGraph(null, null, null);
};

window.updateGraph = function (ilha, concelho, freguesia) {
    const container = document.getElementById("visualizacao-grafica");

    let newFile;
    if (ilha && !concelho && !freguesia) {
        if (ilha == "NA") newFile = `Santana-null.svg`; // SVG that appears to be off
        else  newFile = `null-null.svg`;
    } else if (concelho && !freguesia) {
        console.log(`${concelho}-null.svg`);
        newFile = `${concelho}-null.svg`;
    } else if (concelho && freguesia) {
        console.log(`${concelho}-${freguesia}.svg`);
        newFile = `${concelho}-${freguesia}.svg`;
    }else if (freguesia){
        console.log(`A procura do concelho...`);
        updateGraphFromFreguesiaOnly(freguesia);
        return;
    } else {
        console.log(`null-null.svg`);
        newFile = "null-null.svg";
    }

    const preloaded = preloadedImages[newFile];
    const newImg = preloaded ? preloaded.cloneNode(true) : new Image();
    if (!preloaded) {
        newImg.src = `/svgs/${newFile}`;
        newImg.alt = `Mapa de ${freguesia || concelho || ilha || "geral"}`;
    }

    newImg.classList.add("graph-image");

    // Current image (if any)
    const currentImg = container.querySelector(".graph-image");

    // Insert new image
    container.appendChild(newImg);

    // Handle fade and cleanup
    newImg.onload = () => {
        newImg.style.opacity = "0";
        newImg.style.zIndex = "2";
        newImg.style.transition = "opacity 1s ease-in-out";

        // Força reflow
        newImg.offsetHeight;

        if (currentImg) {
            currentImg.style.zIndex = "1";
            currentImg.style.transition = "opacity 1s ease-in-out";
            newImg.style.opacity = "1";
            currentImg.style.opacity = "0";

            setTimeout(() => {
                container.removeChild(currentImg);
            }, 1000);
        } else {
            newImg.style.opacity = "1";
        }
    };

    if (newImg.complete) {
        newImg.onload(); // immediate if already cached
    }
};

window.updateGraphFromFreguesiaOnly = function(freguesia) {
    const concelho = freguesiaToConcelho[freguesia];
    if (concelho) {
        window.updateGraph(null, concelho, freguesia);
    } else {
        console.warn("Não foi possível encontrar concelho para freguesia:", freguesia);
        window.updateGraph(null, null, null);
    }
};

window.addEventListener('DOMContentLoaded', () => {
    console.log("A tentar iniciar a imagem");
});
