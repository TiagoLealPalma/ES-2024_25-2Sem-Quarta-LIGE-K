// Esta versão usa CDN para ser mais fácil de testar
class GraphViewer extends HTMLElement {
    connectedCallback() {
        this.style.height = "100%";
        this.style.width = "100%";
        this.style.border = "0px solid #ccc";

        const container = document.createElement('div');
        container.style.height = '100%';
        container.style.width = '100%';
        this.appendChild(container);

        const rawData = this.graphData || this.getAttribute('graphData');

        if (!rawData) {
            console.error("Nenhum grafo recebido do backend.");
            return;
        }

        let parsed;
        try {
            parsed = JSON.parse(rawData);
        } catch (e) {
            console.error("Erro ao fazer parse do JSON:", e);
            return;
        }

        const script = document.createElement('script');
        script.src = 'https://unpkg.com/vis-network/standalone/umd/vis-network.min.js';
        script.onload = () => {
            const nodes = new vis.DataSet(parsed.nodes);
            const edges = new vis.DataSet(parsed.edges);

            const options = {
                nodes: {
                    shape: 'ellipse',
                    color: {
                        background: '#F66435',
                        border: '#F66435',
                        highlight: {
                            background: '#FFF1D0',
                            border: '#F66435',
                        }
                    },
                    font: {
                        color: 'rgb(255, 241, 208)',
                        face: 'Inter',
                        size: 20,
                    }
                },
                edges: {
                    color: '#F66435',
                    width: 1.5,
                    arrows: 'none',
                    smooth: {
                        type: 'dynamic'
                    }
                },
                layout: {
                    improvedLayout: true
                },
                physics: {
                    enabled: true,
                    solver: 'forceAtlas2Based',
                    forceAtlas2Based: {
                        gravitationalConstant: -50,
                        centralGravity: 0.01,
                        springLength: 100,
                        springConstant: 0.08,
                        damping: 0.4
                    },
                    stabilization: {
                        iterations: 150,
                        fit: true
                    }
                },
                interaction: {
                    dragView: true,
                    zoomView: true
                }
            };

            const data = { nodes, edges };
            const network = new vis.Network(container, data, options);

            // Desliga a física automaticamente após estabilização
            network.once("stabilizationIterationsDone", function () {
                network.setOptions({ physics: false });
                console.log("Física desligada após estabilização.");
            });
        };

        document.head.appendChild(script);
    }

    set graphData(value) {
        this.setAttribute('graphData', value);
    }

    get graphData() {
        return this.getAttribute('graphData');
    }
}

customElements.define('graph-viewer', GraphViewer);
