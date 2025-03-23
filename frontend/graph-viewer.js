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

        const script = document.createElement('script');
        script.src = 'https://unpkg.com/vis-network/standalone/umd/vis-network.min.js';
        script.onload = () => {
            const nodes = new vis.DataSet([
                { id: 1, label: 'Lisboa', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 2, label: 'Pico', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 3, label: 'Porto', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 4, label: 'Faro', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 5, label: 'Coimbra', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 6, label: 'Braga', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 7, label: 'Setúbal', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 8, label: 'Évora', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 9, label: 'Viseu', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } },
                { id: 10, label: 'Beja', color: '#F66435', font: { color: 'rgb(255, 241, 208)', face: 'Inter' } }
            ]);

            const edges = new vis.DataSet([
                { from: 1, to: 2, color: { color: '#F66435' } },
                { from: 1, to: 3, color: { color: '#F66435' } },
                { from: 2, to: 4, color: { color: '#F66435' } },
                { from: 2, to: 5, color: { color: '#F66435' } },
                { from: 3, to: 6, color: { color: '#F66435' } },
                { from: 3, to: 7, color: { color: '#F66435' } },
                { from: 4, to: 8, color: { color: '#F66435' } },
                { from: 5, to: 9, color: { color: '#F66435' } },
                { from: 6, to: 10, color: { color: '#F66435' } },
                { from: 10, to: 1, color: { color: '#F66435' } }, // para criar um loop cíclico
                { from: 7, to: 8, color: { color: '#F66435' } },
                { from: 9, to: 4, color: { color: '#F66435' } }
            ]);


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
                    width: 2,
                    arrows: 'none',
                },
                layout: {
                    improvedLayout: true
                },
                physics: {
                    enabled: true
                }
            };

            const data = { nodes, edges };
            new vis.Network(container, data, options);
        };
        document.head.appendChild(script);
    }
}

customElements.define('graph-viewer', GraphViewer);
