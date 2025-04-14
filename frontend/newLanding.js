console.log("newLanding.js carregado");

// 🟡 Objeto para guardar imagens pré-carregadas
const preloadedImages = {};

// 🔁 Função que pré-carrega imagens para cache
function preloadSvgs(svgList) {
    svgList.forEach((name) => {
        const img = new Image();
        img.src = `/svgs/${name}`;
        preloadedImages[name] = img;
    });
}

window.initGraph = function (propertiesJson) {
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


    // ✅ Mostra a imagem inicial (pré-carregada se existir)
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
};

window.updateGraph = function (ilha, concelho, freguesia) {
    const container = document.getElementById("visualizacao-grafica");

    let newFile;
    if (ilha && !concelho && !freguesia) {
        newFile = `${ilha}.svg`;
    } else if (concelho && !freguesia) {
        newFile = `${concelho}-null.svg`;
    } else if (concelho && freguesia) {
        newFile = `${concelho}-${freguesia}.svg`;
    } else {
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



        if (currentImg) {
            currentImg.style.zIndex = "1";
            newImg.style.opacity = "1";
            currentImg.style.opacity = "0";
            setTimeout(() => {
                container.removeChild(currentImg);
            }, 1000);
        }
    };

    if (newImg.complete) {
        newImg.onload(); // immediate if already cached
    }
};
