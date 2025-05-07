console.log("Script loaded: area-media-viewer.js");

class AreaMediaViewer extends HTMLElement {
    connectedCallback() {
        this.classList.add("area-media-container");

        this.innerHTML = `
                <div style="
            background-color: rgba(255, 241, 208, 0.05);
            padding: 2rem;
            border-radius: 16px;
            max-width: 600px;
            margin: 2rem auto;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
            ">
            <h2 style="
            color: #F66435;
            text-align: center;
            font-size: 1.6rem;
            margin-bottom: 2rem;
            font-weight: 600;
            ">
            Consulta da Área Média
            </h2>
    
            <div style="margin-bottom: 1.5rem;">
                <label for="criterio" style="display: block; margin-bottom: 0.5rem;">Critério:</label>
                <select id="criterio" style="
                    width: 100%;
                    padding: 0.6rem;
                    border-radius: 8px;
                    border: none;
                    font-size: 1rem;
                    background-color: #1a1f23;
                    color: rgb(255, 241, 208);
                ">
                    <option value="Freguesia">Freguesia</option>
                    <option value="Município">Município</option>
                    <option value="Ilha">Ilha</option>
                </select>
            </div>
    
            <div style="margin-bottom: 1.5rem;">
                <label for="localidade" style="display: block; margin-bottom: 0.5rem;">Localidade:</label>
                <select id="localidade" style="
                    width: 100%;
                    padding: 0.6rem;
                    border-radius: 8px;
                    border: none;
                    font-size: 1rem;
                    background-color: #1a1f23;
                    color: rgb(255, 241, 208);
                "></select>
            </div>
    
            <button id="calcular" style="
                width: 100%;
                padding: 0.8rem;
                font-size: 1rem;
                background-color: #F66435;
                color: white;
                border: none;
                border-radius: 10px;
                cursor: pointer;
                font-weight: bold;
                transition: background-color 0.3s ease;
            ">Calcular área média</button>
    
            <p id="resultado" style="
                text-align: center;
                margin-top: 2rem;
                font-size: 1.2rem;
                font-weight: 500;
                color: #62d16f;
            "></p>
        </div>
            `;


        this.localidades = {
            "Freguesia": [
                "Ponta do Sol", "Câmara de Lobos", "Porto da Cruz", "Estreito de Câmara de Lobos",
                "Canhas", "Arco da Calheta", "Fajã da Ovelha", "Calheta", "São Martinho", "Machico",
                "Ponta do Pargo", "Jardim da Serra", "Santo António", "Santo António da Serra",
                "Madalena do Mar", "Quinta Grande", "Estreito da Calheta", "Paul do Mar", "Curral das Freiras",
                "Prazeres", "São Roque", "Agua de Pena", "São Gonçalo", "Monte", "Jardim do Mar",
                "Caniçal", "Funchal (Santa Maria Maior)", "Funchal (Santa Luzia)", "Imaculado Coração de Maria",
                "Funchal (São Pedro)", "Funchal (Sé)", "Tabua", "Campanário", "Faial"
            ],
            "Município": [
                "Câmara de Lobos", "Calheta", "Ponta do Sol", "Machico", "Funchal", "Ribeira Brava",
                "Santa Cruz", "Santana"
            ],
            "Ilha": [
                "Ilha da Madeira (Madeira)", "N/A"
            ]
        };

        this.updateLocalidades("Freguesia");

        this.querySelector("#criterio").addEventListener("change", () => {
            const criterio = this.querySelector("#criterio").value;
            this.updateLocalidades(criterio);
        });

        this.querySelector("#calcular").addEventListener("click", () => {
            const criterio = this.querySelector("#criterio").value;
            const localidade = this.querySelector("#localidade").value;
            if (this.$server) {
                this.$server.requestAverageArea(criterio, localidade);
            } else {
                console.error("Erro: this.$server não está definido.");
            }
        });
    }

    updateLocalidades(criterio) {
        const localidadeSelect = this.querySelector("#localidade");
        localidadeSelect.innerHTML = "";
        this.localidades[criterio].forEach(loc => {
            const option = document.createElement("option");
            option.value = loc;
            option.textContent = loc;
            localidadeSelect.appendChild(option);
        });
    }

    showResult(media) {
        const result = this.querySelector("#resultado");
        result.textContent = `Área média: ${media.toFixed(2)} m²`;
    }
}

customElements.define('area-media-viewer', AreaMediaViewer);
